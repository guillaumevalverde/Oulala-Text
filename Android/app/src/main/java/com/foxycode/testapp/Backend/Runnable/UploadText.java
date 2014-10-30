package com.foxycode.testapp.Backend.Runnable;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.util.Log;
import com.foxycode.testapp.Activity.SharedPreferenceManager;
import com.foxycode.testapp.Backend.BackendService;
import com.foxycode.testapp.Backend.ComBackendManger;
import com.foxycode.testapp.DataBase.Provider.ContentUsProvider;
import com.foxycode.testapp.Exception.MyAppException;
import com.foxycode.testapp.Model.ContentGeneric;
import com.foxycode.testapp.Model.TextContent;
import com.foxycode.testapp.Security.MySecureManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import javax.crypto.SecretKey;

/**
 * Created by gve on 27/10/2014.
 */
public class UploadText implements Runnable {

    private static final String TAG = "UploadText" ;
    SharedPreferenceManager mSharedPref;
    ContentResolver mRes;
    String mText;
    BackendService mService;
    ComBackendManger mComBackend;

    public UploadText(ContentResolver res, String text, BackendService backendService) {
        mRes = res;
        mText = text;
        mService = backendService;
        mSharedPref = SharedPreferenceManager.getInstance();
        mComBackend = new ComBackendManger(backendService.getApplicationContext());
    }

    @Override
    public void run() {

        String pwd =mSharedPref.getPwd_COMBINED();
        SecretKey key =  MySecureManager.deriveKeyPbkdf2(pwd);
        Log.v(TAG, "text to upload: "+ mText);
        String encryptText = MySecureManager.encrypt(mText,key);

        if(encryptText!=null) {
            ContentGeneric content = new TextContent(mText, true);
            content.setStatut(ContentGeneric.TYPE_SENDING.WAITING);
            Log.v("aa", "create file: " + content.isLeftSide);
            Log.v("aa", "create file: " + content.toContentValues().toString());
            Uri uri = mRes.insert(ContentUsProvider.CONTENT_URI, content.toContentValues());

            JSONObject json;
            try {
                json = mComBackend.postContentText(mSharedPref.getUserId(), mSharedPref.getPwd_ID(), encryptText,ContentUris.parseId(uri));
                if (json != null) {
                    content.setIdServerDb(json.getLong("idserver"));
                    content.setStatut(ContentGeneric.TYPE_SENDING.SEND);
                    Log.v(TAG,"update content: "+content.getValue());
                    int update = mRes.update(ContentUris.withAppendedId(ContentUsProvider.CONTENT_URI, ContentUris.parseId(uri)), content.toContentValues(), null, null);
                    Log.v(TAG,"update : "+update);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MyAppException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
     }
}

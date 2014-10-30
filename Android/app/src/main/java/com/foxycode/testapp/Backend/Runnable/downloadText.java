package com.foxycode.testapp.Backend.Runnable;

import android.content.ContentResolver;
import android.content.Context;
import com.foxycode.testapp.Activity.SharedPreferenceManager;
import com.foxycode.testapp.Backend.ComBackendManger;
import com.foxycode.testapp.DataBase.Provider.ContentUsProvider;
import com.foxycode.testapp.Model.ContentGeneric;
import com.foxycode.testapp.Model.TextContent;
import com.foxycode.testapp.Security.MySecureManager;
import org.json.JSONException;
import org.json.JSONObject;
import javax.crypto.SecretKey;

/**
 * Created by gve on 27/10/2014.
 * Download a text from the server, and decrypt it, and save it in the database
 */
public class downloadText implements Runnable {

    private static final String TAG = "downloadText" ;
    SharedPreferenceManager mSharedPref;
    ContentResolver mRes;
    long mTextId;
    ComBackendManger mComBackend;

    public downloadText(ContentResolver res, long textIdTodownload, Context c) {
        mRes = res;
        mTextId = textIdTodownload;
        mSharedPref = SharedPreferenceManager.getInstance();
        mComBackend = new ComBackendManger(c);
    }

    @Override
    public void run() {
        try {
            JSONObject json = mComBackend.downloadTextContent(mTextId);
            if (json != null) {
                String valueEncrypt = json.getString("text");
                String value = decrypt(valueEncrypt);
                ContentGeneric textContent = new TextContent(value,false);
                textContent.setIdServerDb(mTextId);
                mRes.insert(ContentUsProvider.CONTENT_URI,textContent.toContentValues());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * decrypt the downloaded message
     * @param valueEncrypt
     * @return
     */
    private String decrypt(String valueEncrypt) {
        String pwd =mSharedPref.getPwd_COMBINED();
        SecretKey key =  MySecureManager.deriveKeyPbkdf2(pwd);
        return MySecureManager.decrypt(valueEncrypt,key);
    }
}

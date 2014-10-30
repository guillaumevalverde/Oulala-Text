package com.foxycode.testapp.Backend.Runnable;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import com.foxycode.testapp.Activity.SharedPreferenceManager;
import com.foxycode.testapp.Backend.ComBackendManger;
import com.foxycode.testapp.DataBase.Contract.ContentUs;
import com.foxycode.testapp.DataBase.Provider.ContentUsProvider;
import com.foxycode.testapp.Exception.MyAppException;
import com.foxycode.testapp.Model.ContentGeneric;
import com.foxycode.testapp.Model.ImageContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by gve on 27/10/2014.
 * download the file, and save it in the database
 * the file is saved encrypted. it is decrypted when loaded to the UI
 */
public class DeleteContent implements Runnable {

    private static final String TAG = "DeleteContent" ;
    SharedPreferenceManager mSharedPref;
    ContentResolver mRes;
    String mWhere, mWhereLocal ;
    ComBackendManger mComBackend;

    public DeleteContent(ContentResolver res, String where,String whereL, Context c) {
        mRes = res;
        mWhere = where;
        mWhereLocal = whereL;
        mSharedPref = SharedPreferenceManager.getInstance();
        mComBackend = new ComBackendManger(c);
    }

    @Override
    public void run() {
     try {
         Log.v("TAG", "in delete " + mWhere);
         mRes.delete(ContentUsProvider.CONTENT_URI, mWhereLocal, null);
         JSONObject json = mComBackend.postDeleteIds(mWhere);

     } catch (JSONException e) {
            e.printStackTrace();
        } catch (MyAppException e) {
         e.printStackTrace();
     } catch (IOException e) {
         e.printStackTrace();
     }
    }
}

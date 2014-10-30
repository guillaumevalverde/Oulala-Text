package com.foxycode.testapp.Backend.Runnable;

import android.content.ContentResolver;
import android.content.Context;
import com.foxycode.testapp.Activity.SharedPreferenceManager;
import com.foxycode.testapp.Backend.ComBackendManger;
import com.foxycode.testapp.DataBase.Provider.ContentUsProvider;
import com.foxycode.testapp.Model.ContentGeneric;
import com.foxycode.testapp.Model.ImageContent;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gve on 27/10/2014.
 * download the file, and save it in the database
 * the file is saved encrypted. it is decrypted when loaded to the UI
 */
public class downloadFile implements Runnable {

    private static final String TAG = "downloadFile" ;
    SharedPreferenceManager mSharedPref;
    ContentResolver mRes;
    long ImgId;
    ComBackendManger mComBackend;

    public downloadFile(ContentResolver res, long imgIdTodownload,Context c) {
        mRes = res;
        ImgId = imgIdTodownload;
        mSharedPref = SharedPreferenceManager.getInstance();
        mComBackend = new ComBackendManger(c);
    }

    @Override
    public void run() {
     try {
            JSONObject json = mComBackend.downloadImageContent(ImgId);
            if (json != null) {
                String value = json.getString("path");
                ContentGeneric imgContent = new ImageContent(value,false);
                imgContent.setIdServerDb(ImgId);
                mRes.insert(ContentUsProvider.CONTENT_URI,imgContent.toContentValues());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

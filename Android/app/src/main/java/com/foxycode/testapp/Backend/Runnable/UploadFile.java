package com.foxycode.testapp.Backend.Runnable;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import com.foxycode.testapp.Activity.SharedPreferenceManager;
import com.foxycode.testapp.Backend.BackendService;
import com.foxycode.testapp.Backend.ComBackendManger;
import com.foxycode.testapp.DataBase.Provider.ContentUsProvider;
import com.foxycode.testapp.Exception.MyAppException;
import com.foxycode.testapp.Model.ContentGeneric;
import com.foxycode.testapp.Model.ImageContent;
import com.foxycode.testapp.Security.MySecureManager;
import com.foxycode.testapp.Util.BitmapProcessing;
import com.foxycode.testapp.Util.UsUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;

/**
 * Created by gve on 27/10/2014.
 */
public class UploadFile implements Runnable {

    private static final String TAG = "UploadFile" ;
    SharedPreferenceManager mSharedPref;
    ContentResolver mRes;
    String mImgPath;
    BackendService mService;
    ComBackendManger mComBackend;

    public UploadFile(ContentResolver res, String imgPath, BackendService backendService) {
        mRes = res;
        mImgPath = imgPath;
        mService = backendService;
        mSharedPref = SharedPreferenceManager.getInstance();
        mComBackend = new ComBackendManger(backendService.getApplicationContext());
    }

    @Override
    public void run() {

        int orientation = -1;
        try {
            orientation = BitmapProcessing.getImageOrientation(mImgPath);
            Log.v(TAG, "orientation: "+orientation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapProcessing.createAdaptedBitmap(mImgPath, orientation, 640, 480);
        ByteArrayOutputStream boutp = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 85, boutp);
        byte[] array = null;
        try {
            boutp.flush();
            array = boutp.toByteArray();
            Log.v("image", "onactresult imgpath: "+ mImgPath);
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("error", e.getMessage());
        }

        String pwd =mSharedPref.getPwd_COMBINED();
        SecretKey key =  MySecureManager.deriveKeyPbkdf2(pwd);

        Date date = new Date();
        String enc_path = UsUtil.getDirectoryImPath()+"/"+"Enc_"+date.getTime()+".jpg";
        ByteArrayInputStream input;
        FileOutputStream fileOut = null;
        CipherOutputStream cipherOut = null;
        boolean STOP = false;
        ContentGeneric content = null;
        Uri uri = null;
        try {
            if(array!=null) {
                input = new ByteArrayInputStream(array);
			    fileOut = new FileOutputStream(enc_path);
                cipherOut = MySecureManager.encrypt(fileOut, key);
                int count;
                byte[] buffer = new byte[1024]; // temp buffer

                while ((count = input.read(buffer, 0, buffer.length)) != -1) {
                    cipherOut.write(buffer, 0, count);
                }
                cipherOut.flush();
                content = new ImageContent(enc_path, true);
                content.setStatut(ContentGeneric.TYPE_SENDING.WAITING);
                Log.v("aa", "create file: " + content.isLeftSide);
                Log.v("aa", "create file: " + content.toContentValues().toString());
                uri = mRes.insert(ContentUsProvider.CONTENT_URI, content.toContentValues());
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            STOP = true;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            STOP = true;
        }
        finally{
            try {
                cipherOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(STOP)
                mService.stopForegroundIfAllDone();
            File file = new File(mImgPath);
            file.delete();
        }

        if(!STOP) {
            try {
                JSONObject json = mComBackend.postContentImage(mSharedPref.getUserId(), mSharedPref.getPwd_ID(), enc_path,ContentUris.parseId(uri));
                if (json != null && uri !=null) {
                    content.setIdServerDb(json.getLong("idserver"));
                    content.setStatut(ContentGeneric.TYPE_SENDING.SEND);
                    mRes.update(ContentUris.withAppendedId(ContentUsProvider.CONTENT_URI, ContentUris.parseId(uri)), content.toContentValues(), null, null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MyAppException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                mService.stopForegroundIfAllDone();
            }
        }
    }
}

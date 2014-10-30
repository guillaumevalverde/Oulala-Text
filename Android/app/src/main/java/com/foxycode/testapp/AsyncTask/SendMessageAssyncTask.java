package com.foxycode.testapp.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.foxycode.testapp.Backend.ComBackendManger;
import com.foxycode.testapp.DataBase.Contract.ContentUs;
import com.foxycode.testapp.DataBase.Provider.ContentUsProvider;
import com.foxycode.testapp.Exception.MyAppException;
import com.foxycode.testapp.Model.ContentGeneric;
import com.foxycode.testapp.Model.TextContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by gve on 23/10/2014.
 */
public class SendMessageAssyncTask extends AsyncTask<String, Boolean, Boolean> {


    private static final String LOG_TAG = "SendMessageAssyncTask" ;
    Context mContext;
  //  ImageView mIv;
    TextContent message;
    public SendMessageAssyncTask(Context c, TextContent messageC){
        mContext = c;
    //    mIv=iv;
        message = messageC;

    }

    ComBackendManger comBackend = new ComBackendManger(mContext);

    @Override
    protected Boolean doInBackground(String... params) {
        JSONObject ret =null;
        try {
            Log.v(LOG_TAG, "do In background call p 0 1 2: " + params[0] + " " + params[1] );
            ret = comBackend.postMessage(params[0], params[1],message);
            int id_db  = ret.getInt("ID_DB");
            message.setStatut(ContentGeneric.TYPE_SENDING.RECEIVED);
            String WHERE = ContentUs.ID+ " =?";
            String[] args = {""+message.getId()};
            mContext.getContentResolver().update(ContentUsProvider.CONTENT_URI,message.toContentValues(),WHERE,args);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyAppException e) {
            e.printStackTrace();
        }
        if(ret!=null)
            return true;
        else return false;

    }
}

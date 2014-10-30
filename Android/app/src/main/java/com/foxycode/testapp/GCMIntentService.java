package com.foxycode.testapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.foxycode.testapp.Activity.SharedPreferenceManager;
import com.foxycode.testapp.Backend.ComBackendManger;
import com.foxycode.testapp.Backend.Runnable.downloadFile;
import com.foxycode.testapp.Backend.Runnable.downloadText;
import com.foxycode.testapp.DataBase.Contract.ContentUs;
import com.foxycode.testapp.DataBase.Provider.ContentUsProvider;
import com.foxycode.testapp.Model.ContentGeneric;
import com.foxycode.testapp.Model.ImageContent;
import com.foxycode.testapp.Util.UsUtil;
import com.google.android.gcm.GCMBaseIntentService;


public class GCMIntentService extends GCMBaseIntentService {

	// package
	static final String PREFERENCE = "com.google.android.c2dm";
    private static final String LOG = "GCMIntentService";
    private static final String ACTION_RECEIVE_PIC = "picture" ;
    private static final String ACTION_RECEIVE_TEXT = "text";
    private static final String ACTION_RECEIVE_FEEDBACK = "reply";
    private static final String ACTION_DELETE_CONTENT = "delete";
    SharedPreferenceManager mSharedPrefs = SharedPreferenceManager.getInstance();

	//Key
	public static final String KEY_ACTION = "action";
    private static final String PARTNERID = "partner_Id";
    private static final String ACTION_PAIRING_OK = "pairing_ok";
    ComBackendManger mCommanager=null;
	
	

	public static String mRegistrationId = null;
	
	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
        if(mCommanager==null)
            mCommanager = new ComBackendManger(this.getApplicationContext());

        if(intent == null) {
			Log.w(TAG, "onMessage: intent is null");
			return;
		}
		// Get notification's action
		String action = intent.getStringExtra(KEY_ACTION);
        Log.v(LOG,"receive gcm, action: "+action);

        if(action.equals(ACTION_PAIRING_OK)) {
            final String pic_url = intent.getStringExtra(PARTNERID);
            Log.v(LOG,"receive gcm about pairing "+pic_url);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                   boolean retour= mCommanager.downloadFileData(UsUtil.getProfileImPath_Him(),pic_url);
                   mSharedPrefs.setIsWAITINGCONFIRM(false);
                    Intent intent = new Intent();
                    intent.setAction("device.paired");

                    sendBroadcast(intent);
                }
            });
            t.start();
        }
        else if(action.equals(ACTION_RECEIVE_PIC)){
            String idS = intent.getStringExtra("id");

            Log.v(LOG,"receive gcm about image to download "+idS);

            if(idS!=null){
                long id = Long.parseLong(idS);

                Runnable dowRunnable = new downloadFile(getContentResolver(),id,getApplicationContext());
               (new Thread(dowRunnable)).start();
            }


        }
        else if(action.equals(ACTION_RECEIVE_TEXT)){
            String idS = intent.getStringExtra("id");

            Log.v(LOG,"receive gcm about image to download "+idS);

            if(idS!=null){
                long id = Long.parseLong(idS);
                Runnable dowRunnable = new downloadText(getContentResolver(),id,getApplicationContext());
                (new Thread(dowRunnable)).start();
            }


        }
        else if(action.equals(ACTION_RECEIVE_FEEDBACK)){
            final String idS = intent.getStringExtra("id");
            Log.v(LOG,"receive gcm about image to download "+idS);

            if(idS!=null ){
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String WHERE  = ContentUs.ID +" =?";
                        String[ ] arg = {idS};
                        Cursor cursor = getContentResolver().query(ContentUsProvider.CONTENT_URI,ContentUs.getProjection(),WHERE,arg,null);
                        if(cursor.moveToNext()) {
                            ContentGeneric content = ContentGeneric.fromCursor(cursor);
                            content.setStatut(ContentGeneric.TYPE_SENDING.SEND_RECEIVED);
                            cursor.close();
                            getContentResolver().update(ContentUris.withAppendedId(ContentUsProvider.CONTENT_URI, content.getId()), content.toContentValues(), null, null);
                        }
                  }
                });
                t.start();
            }
        }
        else if(action.equals(ACTION_DELETE_CONTENT)){
            final String WHERE = intent.getStringExtra("where");
            Log.v(LOG, "receive delete where:  " + WHERE);

            if(WHERE!=null ){
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                       // String WHERE  = ContentUs.ID +" =?";
                       // String[ ] arg = {idS};
                        getContentResolver().delete(ContentUsProvider.CONTENT_URI, WHERE, null);

                    }
                });
                t.start();
            }
        }

	}





	@Override
	protected void onRegistered(Context context, String registrationId) {
        Log.i( "GCM", "onRegistered: Sending request to Server : regId = " + registrationId);
		try {
            ComBackendManger CommunicationManager = new ComBackendManger(context);

			if(registrationId != null && (registrationId.length()>0)) {
				Log.d(TAG, "onRegistered - registrationId: " + registrationId);
				mRegistrationId = registrationId;
				
				mSharedPrefs.setGCMRegisterId(registrationId);
				Log.d(  "GCM", "gg send update - registrationId: " + registrationId+"device :");
				mSharedPrefs.setGCMUpdate(true);//
				if(SharedPreferenceManager.isLoggedIn())
                    CommunicationManager.postGcmRegistrationId( registrationId);
			}
			else
				Log.e(TAG, "problem");
			
		} catch (Exception e) {
			Log.e("GCM","C2DMReceiver-onRegistered" +e.getMessage());
		}
    //    CommonUtilities.displayMessage(context, getString(R.string.gcm_registered));
    }

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Return the current registration id.
	 *
	 * If result is empty, the registration has failed.
	 *
	 * @return registration id, or empty string if the registration is not complete.
	 */
	public static String getRegistrationId(Context context) {
			final SharedPreferences prefs = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
			return mRegistrationId = prefs.getString("dm_registration", "");
	}
	

	



	
	

}

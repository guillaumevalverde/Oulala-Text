package com.foxycode.testapp.Backend;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.foxycode.testapp.Activity.MainActivity;
import com.foxycode.testapp.Backend.Runnable.DeleteContent;
import com.foxycode.testapp.Backend.Runnable.UploadFile;
import com.foxycode.testapp.Backend.Runnable.UploadText;
import com.foxycode.testapp.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by gve on 27/10/2014.
 */
public class BackendService extends Service {


    private static final String TAG = "BackendService" ;
    private static final int NOTIFICATION_ID = 12;
    // This is the object that receives interactions from clients.  See
    private final IBinder mBinder = new LocalBinder();

    ExecutorService executorService1 ;
    private int mRunningJobs = 0;
    private boolean mIsForeground = false;

    private final Object mLock = new Object();


    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public BackendService getService() {
            return BackendService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    @Override
    public boolean onUnbind(Intent intent) {
        startForegroundIfNeeded();
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        this.stopForeground(true);
        mIsForeground = false;
        super.onRebind(intent);
    }

    private void executeRunnable(Runnable action){
        // Start new job and increase the running job counter
        synchronized (mLock) {
            executorService1.execute(action);

            mRunningJobs++;
            Log.v(  TAG, "execute "+mRunningJobs);


        }
    }
    public void stopForegroundIfAllDone() {
        mRunningJobs--;
        Log.e(TAG, "stopforegroundifalldone : " + mRunningJobs);
        if(mRunningJobs == 0 && mIsForeground) {

            Log.e(TAG, "stopforegroundifalldone 0 shut down executor service");
            stopForeground(true);
            mIsForeground = false;
            //   executorService1.shutdown();
            this.stopSelf();
        }
    }

    public void startForegroundIfNeeded() {
        if(!mIsForeground) {
            Notification notification = buildNotification();
            startForeground(NOTIFICATION_ID, notification);
            mIsForeground = true;
        } }

    private Notification buildNotification(){
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.icon_st);
        builder.setContentTitle("Confidenshare");
        builder.setTicker("working...");
        builder.setContentText("Files working progress");
        Intent notificationIntent = null;

        notificationIntent= new Intent(this, MainActivity.class);

        final PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pi);
        final Notification notification = builder.build();
        return notification;
    }

    @Override
    public void onCreate() {
        Log.v("TEST", "onCreate");

        //    mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // Display a notification about us starting.
        //showNotification();
        executorService1 = Executors.newFixedThreadPool(5);


    }


    /**
     *
     * @param imgPath
     */
    public void uploadFromUrl(String imgPath) {
        Log.d( TAG, "uploadFromUrl fullUrl: "+imgPath);
        Runnable uploadRunnable = new UploadFile( getContentResolver(), imgPath, this);
        executeRunnable(uploadRunnable);
    }

    /**
     *
     * @param text
     */
    public void uploadText(String text) {
        Log.d( TAG, "uploadText fullUrl: "+ text);
        Runnable uploadRunnable = new UploadText(getContentResolver(),text, this);
        executeRunnable(uploadRunnable);
    }

    public void deleteFiles(String where, String whereLocal){
        Log.d( TAG, "uploadText fullUrl: "+ where);
        Runnable uploadRunnable = new DeleteContent(getContentResolver(),where, whereLocal, this);
        executeRunnable(uploadRunnable);


    }
}

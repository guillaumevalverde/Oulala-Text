package com.foxycode.testapp.Activity;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.foxycode.testapp.Security.MySecureManager;

import java.util.Calendar;

/**
 * This class utilises methods regarding with Thread and user interface.
 * 
 * @author <a href="mailto:kikyoung.kwon@gmail.com">Kikyoung Kwon</a>
 */
public class SharedPreferenceManager extends Application {

	private static final String ISSETUP = "issetup";
    private static final String ISWAITINGPAIRING = "iswaitingconfirm";
    private static final String PIN = "pin";
    private static final String PWD = "PWD";
    private static final String PWD_ID = "PWD_ID";
    private static final String USER_ID = "USER_ID";

    private static final String PWD_COMBINED = "PWD_COMBINED";
	private static final String MY_PREFS = "US_PREF";
    private static final String COOKIE = "cookie";
    private static final String ISWAITINGPAIRING2 = "iswainting comfirmother";
    public static String STORAGE_STRING;

    private static final String GCM = "GCM";
    public static final String GCM_UPDATE = "GCM_UPDATE";

    public final static String EXPIRATION_DATE = "expiration";

	SharedPreferences settings;
	Editor e;

    private long cookieExpirationDate;
    private String cookie;
    private static boolean isLogged = false;

	private static SharedPreferenceManager singleInstance;// = new SharedPreferenceManagerGVE();
	public static SharedPreferenceManager getInstance() {
			return singleInstance;
	}
	
	@Override
	public final void onCreate() {
		super.onCreate();
		singleInstance = this;
		settings =getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE);
		e = settings.edit();
		STORAGE_STRING=this.getFilesDir().getPath().toString() ;
	}


    /**
     * define if the application is set up or not
     * @param level
     */
	public  void setIsSetup(boolean level) {
		e.putBoolean(ISSETUP, level);
		e.commit();
	}
    /**
     * define if the application is set up or not
     * @param level
     */
    public  void setIsWAITINGCONFIRM(boolean level) {
        e.putBoolean(ISWAITINGPAIRING, level);
        e.commit();
    }

    /**
     * define if the application is set up or not
     * @param level
     */
    public  void setIsWAITINGCONFIRM2(boolean level) {
        e.putBoolean(ISWAITINGPAIRING2, level);
        e.commit();
    }

    /**
     * get if the application is set up
     * @return
     */
	public boolean isSetUp() {
		return settings.getBoolean(ISSETUP, false);
	}

    public boolean isWaitingPairing() {
        return settings.getBoolean(ISWAITINGPAIRING, true);
    }

    public boolean isWaitingPairing2() {
        return settings.getBoolean(ISWAITINGPAIRING2, false);
    }

    /**
     * define the pin to get in the app once set up
     * @param pin
     */
    public  void setPin(String pin) {
        e.putString(PIN, "1234");
        e.commit();
    }

    /**
     * get if the application is set up
     * @return
     */
    public String getPin() {
        return settings.getString(PIN,null);
    }


    public void setPwdId(String s){
        e.putString(PWD_ID, s);
        e.commit();
    }
    public void setUserId(String s){
        e.putString(USER_ID, s);
        e.commit();
    }
    /**
     * set the pwd
     * @param pwd
     */
    public  void setPWD(String pwd) {
        e.putString(PWD, pwd);
        e.commit();
    }



    public String getPwd(){
        String ret =  settings.getString(PWD, null);
        if(ret==null)
        {
            ret = MySecureManager.generateCode();
            setPWD(ret);
        }
        return ret;
    }

    /**
     * get random 5 string letter saved in phone or generate it
     * @return
     */
    public String getPwd_ID(){
        String ret =  settings.getString(PWD_ID, null);
        if(ret==null)
        {
            ret = MySecureManager.generateCode();
            setPwdId(ret);
        }
        return ret;
    }

    /**
     * get random 5 string letter saved in phone or generate it
     * @return
     */
    public String getUserId(){
        String ret =  settings.getString(USER_ID, null);
        if(ret==null)
        {
            ret = MySecureManager.generateCode();
            setUserId(ret);
        }
        return ret;
    }


    /**
     * get random 5 string letter saved in phone or generate it
     * @return
     */
    public String getPwd_COMBINED(){
        return settings.getString(PWD_COMBINED, "coolcool");
    }

    /**
     * set the pwd
     * @param pwd
     */
    public  void setPWD_COMBINED(String pwd) {
        e.putString(PWD_COMBINED, getPwd()+pwd);
        e.commit();
    }

    /**
     * set the cookie in the shared pref
     *
     * @param val
     */
    public void setCookie(String val) {
        e.putString(COOKIE, val);
        this.cookieExpirationDate = Calendar.getInstance().getTimeInMillis();
        e.putLong(EXPIRATION_DATE, this.cookieExpirationDate);
        this.cookie = val;
        e.commit();

    }

    /**
     * return the cookie saved in the shared pref
     **/
    public String getCookie() {
       // return this.cookie;
         return settings.getString(COOKIE, "");
    }

    public boolean validCookie() {
        long days_1 = 1000 * 60 * 60 * 24;
        Log.d("ShindigApp", " shindig the value of the cookie is " + this.cookieExpirationDate + "current time " + System.currentTimeMillis());
        if (System.currentTimeMillis() - this.cookieExpirationDate > days_1) {
            // here, more than 30 days
            return false;
        } else {
            return true;
        }
    }


    public  void setGCMRegisterId(String Id) {
        //SALog.d( ( (Object)this).getClass(),"GCM ID is set tup : "+Id);
        e.putString(GCM, Id);
        e.commit();
    }
    public String getGCMRegisteredID() {
        return settings.getString(GCM, null);
    }

    public Boolean isGCMNeedsUpdate() {
        return settings.getBoolean(GCM_UPDATE, true);
    }


    public void setGCMUpdate(boolean update) {
        e.putBoolean(GCM_UPDATE,update);
        e.commit();
    }

    public static boolean isLoggedIn() {
        return isLogged;
    }
    public static void setIsLoggedIn(boolean logged){
        isLogged = logged;
    }


} // end of class

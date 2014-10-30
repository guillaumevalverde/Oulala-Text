package com.foxycode.testapp.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;

import com.foxycode.testapp.R;


/**
 * SplashScreen Activity, Redirect to setting of the application if it never had been done,
 * Otherwhise launch the main activity.
 * @author Guillaume Valverde
 *
 */
@SuppressLint("HandlerLeak")
public class SplashScreenActivity extends Activity{

	private static final int STOPSPLASH = 0;

    /**
     * Default duration for the splash screen (milliseconds)
     */
    private static final long SPLASHTIME = 1000;

    private Button mButton= null;
    private EditText mPWD_ET = null;
    private char[] mPwd;
    private Context mContext;
    
   	private final transient Handler splashHandler = new Handler()
	    {
	        @Override
	        public void handleMessage(Message msg)
	        {
	        	if (msg.what == STOPSPLASH)
	            {
	        		final Intent intent;
	        		/** check if the set up has been done, should be done the first time the user use it*/
	        		if( !SharedPreferenceManager.getInstance().isSetUp() )
	        		{
	        			intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
	        		}
                    else if( SharedPreferenceManager.getInstance().isWaitingPairing() )
                    {
                        intent = new Intent(SplashScreenActivity.this, SetUpActivity.class);
                    }
                    else{
	        			intent = new Intent(SplashScreenActivity.this, MainActivity.class);
	        		}

                    startActivity(intent);
	                finish();
	            }
	        	super.handleMessage(msg);
	        }
	    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashscreen);
		
		final Message msg = new Message();
        msg.what = STOPSPLASH;
        mContext  = this;
        
		mButton  = (Button) findViewById(R.id.splashscreen_OK_ET);
		mPWD_ET = (EditText) findViewById(R.id.splashscreen_PWD_ET);

	    splashHandler.sendMessageDelayed(msg, SPLASHTIME);
	}
	
	
}
	
	
	



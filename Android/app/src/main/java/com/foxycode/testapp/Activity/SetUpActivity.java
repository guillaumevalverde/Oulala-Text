package com.foxycode.testapp.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foxycode.testapp.Backend.ComBackendManger;
import com.foxycode.testapp.Exception.MyAppException;
import com.foxycode.testapp.R;
import com.foxycode.testapp.Security.MySecureManager;
import com.foxycode.testapp.Util.CircularImageView;
import com.foxycode.testapp.Util.UsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by gve on 10/09/2014.
 */
public class SetUpActivity extends Activity {


    Button mPairing_B;
    CircularImageView mPortrait1_IV;
    CircularImageView mPortrait2_IV;
    TextView mYou_TV;
    TextView mHerHis_TV;
    TextView mPWD_TV;
    TextView mexplanation_TV;
    TextView mwaiting_TV;
    EditText mpwd_ET;

    LinearLayout mGauche_LL;
    LinearLayout mDroite_LL;
    SharedPreferenceManager mSharedPref;
    Context mContext;

    Bitmap mYouImage;
    Bitmap mHerhIsImage;
    MyReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setuppairing);

        mContext = this;
        mSharedPref = SharedPreferenceManager.getInstance();

        mPairing_B = (Button) findViewById(R.id.setupP_B);
        mPortrait1_IV = (CircularImageView) findViewById(R.id.setup_IV4);
        mPortrait2_IV = (CircularImageView) findViewById(R.id.setup_IV3);
        mGauche_LL = (LinearLayout) findViewById(R.id.setup_gauche_LL);
        mDroite_LL = (LinearLayout) findViewById(R.id.setup_droite_LL);
        mYou_TV = (TextView) findViewById(R.id.setupP_TV1);
        mHerHis_TV = (TextView) findViewById(R.id.setupP_TV2);
        mexplanation_TV = (TextView) findViewById(R.id.setupP_tv5);
        mwaiting_TV = (TextView) findViewById(R.id.setupP_tv6);
        mpwd_ET = (EditText) findViewById(R.id.setupP_ET);

        mPWD_TV = (TextView) findViewById(R.id.setupP_tv4);
        mPWD_TV.setText(mSharedPref.getPwd());

        mPairing_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mpwd_ET.getText().toString().length() > 4) {

                    (new SendJointPwdCheck()).execute();

                }
         }
        });

        mYouImage = BitmapFactory.decodeFile(UsUtil.getProfileImPath());
        mPortrait1_IV.setImageBitmap(mYouImage);
        receiver = new MyReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!mSharedPref.isWaitingPairing()){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(mSharedPref.isWaitingPairing2()){
            startAnimation();
            mPortrait2_IV.setImageBitmap(mYouImage);

            mwaiting_TV.setVisibility(View.VISIBLE);
            mexplanation_TV.setVisibility(View.GONE);
            mpwd_ET.setVisibility((View.GONE));
            mPairing_B.setVisibility(View.GONE);
        }
        IntentFilter filter = new IntentFilter("device.paired");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    private void startAnimation() {
        Animation anim =AnimationUtils.loadAnimation(this, R.anim.droite);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                    goMainScreen();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mGauche_LL.startAnimation(anim);
        mDroite_LL.startAnimation(AnimationUtils.loadAnimation(this, R.anim.gauche));
    }

    private void goMainScreen() {
        Toast.makeText(this,"Go main screen",Toast.LENGTH_LONG).show();
    }

    // Async Task Class
    class SendJointPwdCheck extends AsyncTask<String, String, Boolean> {

        ComBackendManger comBackend = new ComBackendManger(mContext);
        @Override
        protected Boolean doInBackground(String... f_url) {

            String receivedpwd = "sdfasd";
            mSharedPref.setPWD_COMBINED(receivedpwd);
            Log.v("comB", "pwdsjoin :" + mSharedPref.getPwd_COMBINED());
            String joinpwd = MySecureManager.getJoinedPwd(mSharedPref.getPwd_COMBINED());
            try {
                JSONObject json =comBackend.postPairingPwd(joinpwd.substring(0, joinpwd.length() - 1));
                /**
                 * in the case both device are paired
                 */
                if(json.getString("paired").contentEquals("wait")){
                    mSharedPref.setIsWAITINGCONFIRM2(true);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MyAppException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mwaiting_TV.setVisibility(View.VISIBLE);
            mexplanation_TV.setVisibility(View.GONE);
            mpwd_ET.setVisibility((View.GONE));
            mPairing_B.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if(s==true) {
                mPortrait2_IV.setImageBitmap(mYouImage);
                startAnimation();
            }
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("com" ,"received Broadcast");
            Intent intentstart = new Intent(context, MainActivity.class);
            context.startActivity(intentstart);
        }
    }
}

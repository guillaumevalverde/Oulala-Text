package com.foxycode.testapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.foxycode.testapp.Backend.ComBackendManger;
import com.foxycode.testapp.Exception.MyAppException;
import com.foxycode.testapp.R;
import com.foxycode.testapp.Util.BitmapProcessing;
import com.foxycode.testapp.Util.UsUtil;
import com.google.android.gcm.GCMRegistrar;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by gve on 10/09/2014.
 */
public class HomeActivity extends Activity{

    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String LOG_TAG = "HomeActivity";
//    private static final int[] IMAGES = { R.drawable.cercle_bleu, R.drawable.cercle_rose };

    SharedPreferenceManager mSharedPref;
    String mUserId, mUserPwd;
    Context mContext;
    Button changeBg_B;
    ImageSwitcher imageSwitcher;
    ImageButton cam_B;
    ImageView avatar_IV;

    Uri fileUri;
    String mImagePath;
    Bitmap bmp;
    String mCurrentPhotoPath;
    int mOrientation  = 0;;

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName_path = UsUtil.getProfileImPath();
        String path_profile = UsUtil.getDirectoryPath();
        String path_image_dir = UsUtil.getDirectoryImPath();
        File storageDir1 = new File(path_profile);
        storageDir1.mkdirs();
        File storageDir2 = new File(path_image_dir);
        storageDir2.mkdirs();

        File image = new File(imageFileName_path );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mImagePath = image.getAbsolutePath();
        Log.v(LOG_TAG, "mImagePath: "+ mImagePath);
        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_setup);
        mSharedPref = SharedPreferenceManager.getInstance();
        mUserId = mSharedPref.getUserId();
        mUserPwd = mSharedPref.getPwd_ID();
        avatar_IV = (ImageView) findViewById(R.id.setup_avatar_IV);
        avatar_IV.setVisibility(View.INVISIBLE);
        cam_B = (ImageButton) findViewById(R.id.setup_cam_B);
        cam_B.setVisibility(View.VISIBLE);
        imageSwitcher = (ImageSwitcher)findViewById(R.id.setup_imageSwitcher);

        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.
                        MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return myView;
            }

        });

        changeBg_B = (Button)findViewById(R.id.setUP_B);

        changeBg_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSwitch(view);
            }
        });
        changeBg_B.setVisibility(View.INVISIBLE);
        View.OnClickListener clickCam = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT,
                        null);
                galleryIntent.setType("image/*");
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Log.v(LOG_TAG, "FILE created");
                    fileUri =   Uri.fromFile(photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            fileUri);
                }
                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
                chooser.putExtra(Intent.EXTRA_TITLE,
                        getResources().getString(R.string.dialog_title));

                Intent[] intentArray = { cameraIntent };
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooser,
                        UsUtil.SELECT_IMAGE_REQUEST);
            }
        };

        cam_B.setOnClickListener( clickCam);
        avatar_IV.setOnClickListener(clickCam);

        //---------------GCM google--------------------------//
        try{
            GCMRegistrar.checkDevice(this);
            GCMRegistrar.checkManifest(this);

            final String regId = GCMRegistrar.getRegistrationId(this);
            if (regId.equals("")) {
                GCMRegistrar.register(this, getResources().getString(R.string.GCM_ID));
            }
            else if(mSharedPref.isGCMNeedsUpdate()){
                mSharedPref.setGCMRegisterId(regId);
            }
        }
        catch(Exception e){
            Log.e( LOG_TAG,"GCM error: "+e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
       Bitmap bmp = BitmapFactory.decodeFile(UsUtil.getProfileImPath());
       if(bmp!=null){
           avatar_IV.setImageBitmap(bmp);
           avatar_IV.setVisibility(View.VISIBLE);
           cam_B.setVisibility(View.INVISIBLE);
           changeBg_B.setVisibility(View.VISIBLE);
       }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri  mImageUri;
            if (requestCode == UsUtil.SELECT_IMAGE_REQUEST) {
                Log.d(LOG_TAG, "Getting image result");
                if (data != null) { // Picture taken from the gallery
                    Log.d(LOG_TAG, "Data not null");
                    mImageUri = data.getData();
                    Log.d(LOG_TAG,  "mImageUri onActivityResults"  + mImageUri.toString());
                    try {
                        mOrientation = BitmapProcessing
                                .getImageOrientation(mImagePath);
                        Log.v(LOG_TAG, "orientation: "+mOrientation);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Log.v(LOG_TAG, "mImagerURI: "+mImageUri+" fileUri: "+fileUri+ " mImagePath: "+mImagePath);
                    new Crop(mImageUri).output(fileUri).asSquare().start(this);
                }
                else {
                    try {
                        mOrientation = BitmapProcessing
                                .getImageOrientation(UsUtil.getProfileImPath());
                        fileUri = Uri.fromFile(new File(UsUtil.getProfileImPath()));
                        Log.v(LOG_TAG, "orientation: "+mOrientation);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    new Crop(fileUri).output(fileUri).asSquare().start(this);
                }


            }
            else if (requestCode == Crop.REQUEST_CROP){
                bmp = BitmapProcessing.createAdaptedBitmap(mImagePath, mOrientation, 400, 400);
                if (bmp != null) {
                    avatar_IV.setImageBitmap(bmp);
                    BitmapProcessing.saveImageOnDisk(bmp, mImagePath);
                    cam_B.setVisibility(View.INVISIBLE);
                    avatar_IV.setVisibility(View.VISIBLE);
                    changeBg_B.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    public void onSwitch(View view) {
      //  imageSwitcher.setBackgroundResource(IMAGES[mPosition]);
      //  mPosition = (mPosition + 1) % IMAGES.length;
        (new RegisterUserTask()).execute(mUserId,mUserPwd,UsUtil.getProfileImPath());
    }


    class RegisterUserTask extends AsyncTask<String, Void, Boolean> {

       ComBackendManger comBackend = new ComBackendManger(mContext);

        @Override
        protected Boolean doInBackground(String... params) {
            JSONObject ret =null;
            try {
                Log.v( LOG_TAG, "do In background call p 0 1 2: "+params[0]+" "+params[1]+" "+params[2]);
                ret = comBackend.postSignIn(params[0], params[1],params[2]);
                if(mSharedPref.isGCMNeedsUpdate()){
                    Log.v( LOG_TAG, "update GCM");
                    comBackend.postGcmRegistrationId( mSharedPref.getGCMRegisteredID());
                }

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

        @Override
        protected void onPostExecute(Boolean msg) {
            isSignedIn(msg);
        }
    }


    /**
     * if isSignedIn true, first phase is set up, we start SetUpActivity
     * @param isSignedIn
     */
    private void isSignedIn(boolean isSignedIn) {
        if(isSignedIn)
        {
            Toast.makeText(mContext," signd in success ",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,SetUpActivity.class);
            mSharedPref.setIsSetup(true);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(mContext,"server Down, retry later ",Toast.LENGTH_SHORT).show();
        }
    }

}

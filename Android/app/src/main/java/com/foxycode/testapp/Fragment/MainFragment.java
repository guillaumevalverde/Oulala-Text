package com.foxycode.testapp.Fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.foxycode.testapp.Activity.SharedPreferenceManager;
import com.foxycode.testapp.Adapter.ContentUdCursorAdapter;
import com.foxycode.testapp.DataBase.Contract.ContentUs;
import com.foxycode.testapp.DataBase.Provider.ContentUsProvider;
import com.foxycode.testapp.DisplayBitmap.util.ImageCache;
import com.foxycode.testapp.DisplayBitmap.util.ImageFetcher;
import com.foxycode.testapp.DisplayBitmap.util.Utils;
import com.foxycode.testapp.Model.ContentGeneric;
import com.foxycode.testapp.Model.ImageContent;
import com.foxycode.testapp.Model.TextContent;
import com.foxycode.testapp.R;
import com.foxycode.testapp.Security.MySecureManager;
import com.foxycode.testapp.Util.BitmapProcessing;
import com.foxycode.testapp.Util.EditTextBackEvent;
import com.foxycode.testapp.Util.ImageViewWithTriangle;
import com.foxycode.testapp.Util.RoundedImageView;
import com.foxycode.testapp.Util.UsUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;



/**
 * A placeholder fragment containing a simple view.
 */
public  class MainFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, ContentUdCursorAdapter.CommWithFragment, EditTextBackEvent.EditTextImeBackListener {

    private static final String IMAGE_CACHE_DIR = "thumbs";
    private static final int LOADER_ID = 11;
    private static final String TAG =  "MainFragment";
    String mImagePath=null;

    public ContentUdCursorAdapter mAdapter;
    ListView mListView;
    EditTextBackEvent mEditText;
    ImageButton mButton;
    ContentResolver mRes;


    private int TookPicture = 2;

    String mDirectory_Path;
    SharedPreferenceManager mSharedPref;
    ComWithActivity mCallBack;





    /** parametert for bitmap image*/
    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageFetcher mImageFetcher;

    public interface ComWithActivity{
       public void encryptImageFile(String Path);
       public void encryptSendMessage(String mess);
       public void changeMenu(boolean b);

    }

    public static final MainFragment newInstance(String crsCode) {

        MainFragment fragment = new MainFragment();

        final Bundle args = new Bundle(1);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallBack = (ComWithActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ComWithActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ContentUdCursorAdapter(getActivity(), null,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,this);


        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);

    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri mImageUri;
            if (requestCode == TookPicture ) {
                Log.d(TAG, "Getting image result: "+mImagePath);
                if(mCallBack==null)
                    Log.d(TAG, "mCallBack is null");

                mCallBack.encryptImageFile(mImagePath);//new encryptImageFile(getContentResolver())).execute(mImagePath);


               // bmp.copyPixelsToBuffer(); //Move the byte data to the buffer

//                 = buffer.array();
                //imgageTest.setImageBitmap(bmp);


            }


        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView) rootView.findViewById(R.id.main_LV);
        mListView.setAdapter(mAdapter);
        mDirectory_Path = UsUtil.getDirectoryImPath();
        mImagePath = mDirectory_Path+"/camera.jpg";

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        mEditText = (EditTextBackEvent) rootView.findViewById(R.id.main_ET);

        mEditText.setOnEditTextImeBackListener(this);
        mButton = (ImageButton) rootView.findViewById(R.id.main_IB);
         mRes = getActivity().getContentResolver();

        mSharedPref = SharedPreferenceManager.getInstance();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = mEditText.getText().toString();
                if (text != null && text.length() > 0)
                {
                    mCallBack.encryptSendMessage(text);
                   // ContentGeneric content = new TextContent(text, true);
                   // content.setStatut(ContentGeneric.TYPE_SENDING.WAITING);
                   // mRes.insert(ContentUsProvider.CONTENT_URI,content.toContentValues());

                }
                else{
                    // (new DecryptImageFile()).execute();
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                    File photoFile=new File(mImagePath);//mDirectory_Path+"/"+imageFileName);
                    mImagePath = photoFile.getAbsolutePath();

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Log.v("TAG", "FILE created");
                        Uri fileUri =   Uri.fromFile(photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                fileUri);

                        startActivityForResult(cameraIntent, TookPicture);
                    }

                }
                mEditText.setText("");
                mEditText.clearFocus();
                hideKeyboard();

            }
        });

        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    mButton.setImageDrawable(getResources().getDrawable(R.drawable.send));
                else
                    mButton.setImageDrawable(getResources().getDrawable(R.drawable.camera_icon));

            }
        });


        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, this);

        return rootView;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        Date date = new Date();
        String imageFileName = "avatarUs_"+date.getTime();
        File storageDir = Environment.getDataDirectory();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        Log.v("TAG", "mCurrentPhotoPath: "+ image.getAbsolutePath());
        return image;
    }


    @Override
    public void onResume() {
        super.onResume();
        mEditText.clearFocus();
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = ContentUs.getProjection();

        Long dateMiliseconds = Calendar.getInstance().getTimeInMillis();
        String[] params = null;// = new String[3];
        // fix this sometimes is null;

        // Log.d("selecting events for user", "param " + params[0]);
        Log.d("EventListFragment", "shindig currentDate " + dateMiliseconds);
        String selection = "No selection";

        return new CursorLoader(getActivity(),
                ContentUsProvider.CONTENT_URI, projection,
                null, null, null) {
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        mListView.post(new Runnable() {
            public void run() {
                mListView.setSelection(mListView.getCount() - 1);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void setActionBar(boolean b) {
        mCallBack.changeMenu(b);
    }

    @Override
    public void loadImage(String path, boolean isLeft,ImageViewWithTriangle view, ImageViewWithTriangle viewd) {
        if(!isLeft)

            mImageFetcher.loadImage(path, viewd);
        else
            mImageFetcher.loadImage(path, view);
    }

   // @Override
    public void loadImage(String path, ImageViewWithTriangle view) {
        mImageFetcher.loadImage(path, view);
    }

    @Override
    public void onImeBack(EditTextBackEvent ctrl, String text) {
        if(text==null || text.length()<1) {
            mButton.setImageDrawable(getResources().getDrawable(R.drawable.camera_icon));
            mEditText.clearFocus();
        }
        else
            mButton.setImageDrawable(getResources().getDrawable(R.drawable.send));


    }



}

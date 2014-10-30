package com.foxycode.testapp.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foxycode.testapp.Activity.SharedPreferenceManager;
import com.foxycode.testapp.DataBase.Contract.ContentUs;
import com.foxycode.testapp.Model.ContentGeneric;
import com.foxycode.testapp.R;
import com.foxycode.testapp.Security.MySecureManager;
import com.foxycode.testapp.Util.CircularImageView;
import com.foxycode.testapp.Util.ImageViewWithTriangle;
import com.foxycode.testapp.Util.RoundedImageView;
import com.foxycode.testapp.Util.TextViewWithTriangle;
import com.foxycode.testapp.Util.UsUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;

/**
 * Created by gve on 22/09/2014.
 */
public class ContentUdCursorAdapter extends CursorAdapter {
    private static final String LOG = "ContentUdCursorAdapter";
    private static final String TAG = "ContentUdCursorAdapter";
    LayoutInflater inflater;
    Context mContext;
    List<ContentGeneric> selectedList = new ArrayList<ContentGeneric>();
    CommWithFragment mCallback;
    Bitmap mGauche;
    Bitmap mDroite;
   ;



    public void clearList() {
        selectedList.clear();
    }

    public interface CommWithFragment{
        public void setActionBar(boolean b);
        public void loadImage(String path,boolean b,ImageViewWithTriangle view,ImageViewWithTriangle viewd);
    }

    public List<ContentGeneric> getListContent(){
        return selectedList;
    }

    public ContentUdCursorAdapter(Context context, Cursor c, int flags,CommWithFragment ca) {
        //
        super(context, c, flags);
        Log.d(LOG, "creating cursor adapter");
        inflater = LayoutInflater.from(context);
        mContext = context;
        mCallback = ca;
        mGauche = BitmapFactory.decodeFile(UsUtil.getProfileImPath());
        mDroite = BitmapFactory.decodeFile(UsUtil.getProfileImPath());

    }

    public static class MessageViewHolder {
        public TextView date;
        public TextViewWithTriangle content_TV;
        public CircularImageView image_gauche_RIV;
        public CircularImageView image_droite_RIV;
        public ImageViewWithTriangle content_IV;
        public ImageViewWithTriangle content_d_IV;
        public ImageView statutIm_IV;
        public ImageView statutText_IV;
        public RelativeLayout rl;
        public ImageView layerSelect_IV;
    }


    Bitmap getBitmap(String imgpath){
        Log.v("TAG" , "getBitmap "+imgpath);
        String pwd = SharedPreferenceManager.getInstance().getPwd_COMBINED();
        SecretKey key =  MySecureManager.deriveKeyPbkdf2(pwd);

        FileInputStream input = null;
        ByteArrayOutputStream fileOut = null;
       // FileOutputStream fileOut = null;
        CipherInputStream cipherIn = null;
        boolean STOP = false;
        Bitmap bmp = null;
        try {

            input = new FileInputStream(imgpath);
			/* create new temp File */
            fileOut = new ByteArrayOutputStream();
            //fileOut = new FileOutputStream(UsUtil.getDirectoryImPath()+"/test.jpg");
            cipherIn = MySecureManager.decrypt(input, key);


            double fileSize = input.getChannel().size();
            double total = 0;
            int count = 0;
            int progTemp1 = 0;
            int progTemp2 = 0;
            byte[] buffer = new byte[1024]; // temp buffer

            while ((count = cipherIn.read(buffer, 0, buffer.length)) != -1) {
                fileOut.write(buffer, 0, count);
                total += count;
            }
            fileOut.flush();
           // Log.e(TAG, "after flush: "+ fileOut.size());
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inMutable = true;
          //  bmp =BitmapFactory.decodeFile(UsUtil.getDirectoryImPath()+"/test.jpg");
            bmp = BitmapFactory.decodeByteArray(fileOut.toByteArray(), 0, fileOut.size(), options);

        }
        catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            //  mCallBack.failure(mPath,RequestManagerServiceEasy.UPLOAD,Const.ERROR_ENCRYPTION,e.getMessage());
            STOP = true;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            // mCallBack.failure(mPath,RequestManagerServiceEasy.UPLOAD,Const.ERROR_ENCRYPTION,e.getMessage());
            e.printStackTrace();
            STOP = true;

         }
        finally{
            try {
                cipherIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOut.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(STOP)
                return null;
        }


        return bmp;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View eventView = inflater.inflate(R.layout.adapter_main, null);
        MessageViewHolder holder = new MessageViewHolder();
        holder.content_TV = (TextViewWithTriangle) eventView.findViewById(R.id.main_Adapt_TV);
        holder.content_IV = (ImageViewWithTriangle) eventView.findViewById(R.id.main_Adapt_IV);
        holder.content_d_IV = (ImageViewWithTriangle) eventView.findViewById(R.id.main_Adapt_IV2);
        holder.image_gauche_RIV = (CircularImageView) eventView.findViewById(R.id.main_Adapt_gauche_RIV);
        holder.image_droite_RIV = (CircularImageView) eventView.findViewById(R.id.main_Adapt_droite_RIV);
        holder.statutIm_IV = (ImageView) eventView.findViewById(R.id.main_Adapt_statutIm_IV);
        holder.statutText_IV = (ImageView) eventView.findViewById(R.id.main_Adapt_statutTe_IV);
        holder.rl = (RelativeLayout) eventView.findViewById(R.id.main_Adapt_rl);
        holder.layerSelect_IV = (ImageView) eventView.findViewById(R.id.main_Adapt_select_IV);
        eventView.setTag(holder);
        return eventView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final MessageViewHolder holder = (MessageViewHolder)view.getTag();
        final ContentGeneric contentG = ContentGeneric.fromCursor(cursor);

        contentG.displayOnView(holder,context);

        if(contentG.getType()==1){
            //Bitmap bmp=getBitmap(contentG.getValue());
           // if(bmp!=null)
            //    holder.content_IV.setImageBitmap(bmp);
            mCallback.loadImage(contentG.getValue(),contentG.isLeftSide,holder.content_IV,holder.content_d_IV);
        }

        // if selected we display a transparanccy layer
        if(selectedList.contains(contentG)) {
            holder.layerSelect_IV.setVisibility(View.VISIBLE);
        }
        else{
            holder.layerSelect_IV.setVisibility(View.INVISIBLE);
        }

        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedList.contains(contentG)) {
                    holder.layerSelect_IV.setVisibility(View.INVISIBLE);
                    selectedList.remove(contentG);
                    Log.v("TAG","remove from list : "+contentG.getId());

                }
                else{
                    holder.layerSelect_IV.setVisibility(View.VISIBLE);
                    selectedList.add(contentG);

                    Log.v("TAG","add from list : "+contentG.getId());
                }

                String where ="";
                for(ContentGeneric c:selectedList)
                    where += ContentUs.ID + " =  "+c.getId() +" OR ";
                Log.v("TAG","listt : "+where);
                Log.v("TAG","listt empty  : "+selectedList.isEmpty());

                if(selectedList.isEmpty())
                    mCallback.setActionBar( true   );
                else
                    mCallback.setActionBar( false   );

            }
        });

        if(mDroite!=null)
            holder.image_droite_RIV.setImageBitmap(mDroite);

        if(mGauche!=null)
            holder.image_gauche_RIV.setImageBitmap(mGauche);




    }
}

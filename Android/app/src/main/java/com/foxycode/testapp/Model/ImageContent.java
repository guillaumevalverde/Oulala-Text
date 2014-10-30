package com.foxycode.testapp.Model;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.foxycode.testapp.Adapter.ContentUdCursorAdapter;
import com.foxycode.testapp.R;
import com.foxycode.testapp.Util.ImageViewWithTriangle;
import com.foxycode.testapp.Util.TextViewWithTriangle;

import static com.foxycode.testapp.Model.ContentGeneric.TYPE_SENDING.RECEIVED;

/**
 * Created by gve on 19/09/2014.
 */
public class ImageContent extends ContentGeneric {


    @Override
    public int getType() {
        return TextContent.TYPE.IMAGE.getint();
    }


    @Override
    public void displayOnView(ContentUdCursorAdapter.MessageViewHolder holder,Context context) {
        holder.content_TV.setVisibility(View.GONE);
        holder.content_IV.setVisibility(View.VISIBLE);
        holder.statutText_IV.setVisibility(View.GONE);

        holder.content_IV.setImageDrawable(context.getResources().getDrawable(R.drawable.imwait));

        Log.v("TAG", "from isleftside: " + isLeftSide);

        if(isLeftSide){
            holder.image_droite_RIV.setVisibility(View.GONE);
            holder.image_gauche_RIV.setVisibility(View.VISIBLE);
            Log.v("triangle", "from isleftside: WEST" );
           holder.content_IV.setVisibility(View.VISIBLE);
            holder.content_d_IV.setVisibility(View.INVISIBLE);
            holder.statutIm_IV.setVisibility(View.VISIBLE);
            if(statut == RECEIVED.getValue()){
                holder.statutIm_IV.setVisibility(View.GONE);
            }
            else{
                holder.statutIm_IV.setVisibility(View.VISIBLE);
                switch(statut){
                    case 0:
                        holder.statutIm_IV.setImageDrawable(context.getResources().getDrawable( R.drawable.send_));
                        break;
                    case 1:
                        holder.statutIm_IV.setImageDrawable(context.getResources().getDrawable( R.drawable.send_ok));
                        break;
                    case 2:
                        holder.statutIm_IV.setImageDrawable(context.getResources().getDrawable( R.drawable.send_okok));
                        break;
                }

            }
        }
        else{

            Log.v("triangle", "from isleftside: EAST" );

            holder.content_d_IV.setVisibility(View.VISIBLE);
            holder.content_IV.setVisibility(View.INVISIBLE);
            holder.image_droite_RIV.setVisibility(View.VISIBLE);
            holder.image_gauche_RIV.setVisibility(View.GONE);
            holder.statutIm_IV.setVisibility(View.INVISIBLE);
        }
        Log.v("TAG", "from cursor: " + statut);



    }




    String path;

    public String getPath(){
        return path;
    }

    public ImageContent(String value,boolean l){
        super(value, l);
    }

    public ImageContent(String value,boolean l,String date){
        super(value,l,date);
    }
}

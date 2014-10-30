package com.foxycode.testapp.Model;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.foxycode.testapp.Adapter.ContentUdCursorAdapter;
import com.foxycode.testapp.R;
import com.foxycode.testapp.Util.TextViewWithTriangle;

import static com.foxycode.testapp.Model.ContentGeneric.TYPE_SENDING.*;

/**
 * Created by gve on 19/09/2014.
 */
public class TextContent extends ContentGeneric {

    public static enum TYPE{
        TEXT(0),
        IMAGE(1);

        int value;

        TYPE(int i){
            value = i;
        }

        public int getint(){
            return value;
        }
    }

    @Override
    public int getType() {
        return TYPE.TEXT.getint();
    }

    @Override
    public void displayOnView(ContentUdCursorAdapter.MessageViewHolder holder,Context context) {
        holder.content_IV.setVisibility(View.GONE);
        holder.content_d_IV.setVisibility(View.GONE);

        holder.content_TV.setVisibility(View.VISIBLE);
        holder.statutIm_IV.setVisibility(View.GONE);
        holder.statutIm_IV.setVisibility(View.GONE);
        holder.content_TV.setText(mValue);

        if(isLeftSide){
            holder.image_droite_RIV.setVisibility(View.GONE);
            holder.image_gauche_RIV.setVisibility(View.VISIBLE);
            holder.content_TV.setDirection(true);//
            holder.content_TV.changeDirection(TextViewWithTriangle.Direction.WEST);

            holder.statutText_IV.setVisibility(View.VISIBLE);
            switch(statut){
                case 0:
                    holder.statutText_IV.setImageDrawable(context.getResources().getDrawable( R.drawable.send_));
                    break;
                case 1:
                    holder.statutText_IV.setImageDrawable(context.getResources().getDrawable( R.drawable.send_ok));
                    break;
                case 2:
                    holder.statutText_IV.setImageDrawable(context.getResources().getDrawable( R.drawable.send_okok));
                    break;
            }
        }
        else{
            holder.content_TV.setDirection(false);
            holder.content_TV.changeDirection(TextViewWithTriangle.Direction.EAST);

            holder.image_droite_RIV.setVisibility(View.VISIBLE);
            holder.image_gauche_RIV.setVisibility(View.GONE);
            holder.statutText_IV.setVisibility(View.GONE);
        }
        Log.v("TAG","from cursor: "+ statut);

       /* if(statut == RECEIVED.getValue()){
            holder.statutText_IV.setVisibility(View.GONE);
        }
        else{
            holder.statutText_IV.setVisibility(View.VISIBLE);
            switch(statut){
                case 0:
                    holder.statutText_IV.setImageDrawable(context.getResources().getDrawable( R.drawable.send_));
                    break;
                case 1:
                    holder.statutText_IV.setImageDrawable(context.getResources().getDrawable( R.drawable.send_ok));
                    break;
                case 2:
                    holder.statutText_IV.setImageDrawable(context.getResources().getDrawable( R.drawable.send_okok));
                    break;
            }

        }
        */

    }


    public TextContent(String value,boolean isleft){
        super(value,isleft);
    }

    public TextContent(String value,boolean isleft, String date){
        super(value,isleft,date);
    }

}

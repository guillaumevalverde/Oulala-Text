package com.foxycode.testapp.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.foxycode.testapp.Adapter.ContentUdCursorAdapter;
import com.foxycode.testapp.DataBase.Contract.ContentUs;
import com.foxycode.testapp.DataBase.DatabaseUtils;

import java.util.Date;

/**
 * Created by gve on 19/09/2014.
 */
public abstract class ContentGeneric {

    private static final String TAG ="ContentGeneric" ;
    Date mDate;
    int id = -2;

    String mValue;
    long idServerDb = -4;
     public boolean isLeftSide = true;
    int statut=-1;

    public int getId(){
        return id;
    }

    @Override
    public boolean equals(Object o) {
        ContentGeneric f = (ContentGeneric)o;
        if(id==-2 || f.id==-2)
            return (mValue.contentEquals(mValue) && statut==f.statut );
        else
            return (id==f.id);
    }

    public String getValue(){
        return mValue;
    }

    public enum TYPE_SENDING{
        RECEIVED(-1),
        WAITING(0),
        SEND(1),
        SEND_RECEIVED(2);
        int value;

        TYPE_SENDING(int i){
            value = i;
        }
        public final int getValue(){
            return value;
        }
    }
    protected  ContentGeneric(String value,boolean b){
        mDate = new Date();
        mValue = value;
        isLeftSide = b;
    }

    protected  ContentGeneric(String value,boolean b,String date){
       // mDate = new Date(date);
        mValue = value;
        isLeftSide = b;
        Log.v("aa", "in construc: "+isLeftSide);
    }

    public void setStatut(TYPE_SENDING type){
        statut = type.getValue();
    }

    public void setStatut(int type){
       if(type<-1 || type >2)
           statut = -1;
        else
           statut = type;
    }

    public void setIdServerDb(long i){
        idServerDb = i;
    }
    public long getIdServerDb(){
        return idServerDb;
    }

    public abstract int getType();

    public boolean hasDate(){
        return(  !(mDate==null));
    }
    public boolean hasValue(){
        return(  !TextUtils.isEmpty(mValue));
    }

    /**
     * Generate a {@link ContentValues} using the attributes of the provided user. The result can be
     * used to insert or update a record in the database.
     *
     * @return {@link ContentValues} containing user attributes. Empty if the user is null.
     */
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        if (hasDate())
            values.put(ContentUs.CONTENT_DATE, mDate.toString());
        if (hasValue())
            values.put(ContentUs.CONTENT_VALUE, mValue);

         values.put(ContentUs.CONTENT_TYPE, getType());
        values.put(ContentUs.CONTENT_STATUT, statut);

        values.put(ContentUs.CONTENT_ID_SERVERDB, idServerDb);
        values.put(ContentUs.CONTENT_HIS_ME,isLeftSide);
        Log.v(TAG, "islefteside : " + isLeftSide);
        return values;
    }
    /**
     * Utility method used to generate a user object from a database record (provided as a
     * {@link Cursor}).
     *
     * @param cursor
     *            Cursor containing a user record.
     * @return User object
     */
    public static  ContentGeneric fromCursor(Cursor cursor) {
        ContentGeneric content = null;
        if( DatabaseUtils.getInt(cursor, ContentUs.ContentColumn.CONTENT_TYPE) == 1){
            content = new ImageContent( DatabaseUtils.getString(cursor, ContentUs.ContentColumn.CONTENT_VALUE),
                    DatabaseUtils.getBoolean(cursor, ContentUs.ContentColumn.CONTENT_HIS_ME),
                    DatabaseUtils.getString(cursor, ContentUs.ContentColumn.CONTENT_DATE));

        }
        else{
            content = new TextContent( DatabaseUtils.getString(cursor, ContentUs.ContentColumn.CONTENT_VALUE),
                    DatabaseUtils.getBoolean(cursor, ContentUs.ContentColumn.CONTENT_HIS_ME)
                    ,DatabaseUtils.getString(cursor, ContentUs.ContentColumn.CONTENT_DATE));
       }
        content.setStatut(DatabaseUtils.getInt(cursor, ContentUs.ContentColumn.CONTENT_STATUT));
        content.id = (DatabaseUtils.getInt(cursor, ContentUs.ContentColumn.ID));
        content.idServerDb = (DatabaseUtils.getLong(cursor, ContentUs.ContentColumn.CONTENT_ID_SERVERDB));

        return content;
    }


    public abstract void displayOnView(ContentUdCursorAdapter.MessageViewHolder holder,Context context);
}

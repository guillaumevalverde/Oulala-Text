package com.foxycode.testapp.DataBase;

import java.io.Closeable;
import java.io.IOException;

import android.database.Cursor;

/**
 * Class containing utility methods when manipulating databases.
 * 
 * @author Jean-Francois Moy
 */
public final class DatabaseUtils {

	/** Return the string at the specified column of the cursor */
	public static String getString(Cursor cursor, DatabaseColumn column) {
		int index = cursor.getColumnIndex(column.getName());
		return (index != -1) ? cursor.getString(index) : "";
	}

	/** Return the long stored in the specified column of the cursor, -1 if column does not exist */
	public static long getLong(Cursor cursor, DatabaseColumn column) {
		int index = cursor.getColumnIndex(column.getName());
		return (index != -1 ) ? cursor.getLong(index) : index;
	}

	/** Return the int stored in the specified column of the cursor, -1 if column does not exist */
	public static int getInt(Cursor cursor, DatabaseColumn column) {
		int index = cursor.getColumnIndex(column.getName());
		return (index != -1 ) ? cursor.getInt(index) : index;
	}
	
	/** Return the int stored in the specified column of the cursor, -1 if column does not exist */
	public static boolean getBoolean(Cursor cursor, DatabaseColumn column) {
		int index = cursor.getColumnIndex(column.getName());
		boolean retour = false;
		if(index!=-1){
			retour = (cursor.getInt(index)>0) ;
		//	SALog.v(DatabaseUtils.class, "boolean "+retour);
		}
		return retour;
	}

	
	/** Return the int stored in the specified column of the cursor, -1 if column does not exist */
	public static byte[] getByteArray(Cursor cursor, DatabaseColumn column) {
		int index = cursor.getColumnIndex(column.getName());
		return (index != -1 ) ?   cursor.getBlob(index) : null;
	}

	
	/**
	 * Close the closeable object provided if needed. Should be executed in final blocks only as
	 * exceptions are ignored
	 */
	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// do nothing
			}
		}
	}

	/** Close the cursor if it exists */
	public static void closeQuietly(Cursor cursor) {
		if (cursor != null) {
			cursor.close();
		}
	}

}
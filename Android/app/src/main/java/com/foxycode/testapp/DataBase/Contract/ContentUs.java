package com.foxycode.testapp.DataBase.Contract;

import com.foxycode.testapp.DataBase.DatabaseColumn;

/**
 * Created by gve on 19/09/2014.
 */


public class ContentUs {

    // public static final String TABLE_NAME = "contactStorage";
    public static final String ID = "_id";
    public static final String CONTENT_VALUE = "value";
    public static final String CONTENT_TYPE = "type";
    public static final String CONTENT_DATE = "date";
    public static final String CONTENT_HIS_ME = "hisMe";
    public static final String CONTENT_STATUT = "statut";
    public static final String CONTENT_ID_SERVERDB = "id_inserver_db";


    public static String[] getProjection(){
        return new String[]{ID, CONTENT_VALUE, CONTENT_TYPE,CONTENT_STATUT,CONTENT_ID_SERVERDB,CONTENT_HIS_ME};
    }

    /**
     * Enumeration that defines the table columns to store a {@link com.foxycode.testapp.Model.ContentGeneric} instance.
     *
     * <p>
     * It associates the name of the column to its SQL type and the version of the database it has been
     * introduced at.
     */
    public enum ContentColumn implements DatabaseColumn {

        ID(ContentUs.ID, "INTEGER PRIMARY KEY AUTOINCREMENT", 1),
        CONTENT_VALUE(ContentUs.CONTENT_VALUE, "TEXT NOT NULL", 1),
        CONTENT_TYPE(ContentUs.CONTENT_TYPE, "INTEGER", 1),
        CONTENT_DATE(ContentUs.CONTENT_DATE,"TEXT NOT NULL",1),
        CONTENT_HIS_ME(ContentUs.CONTENT_HIS_ME,"Boolean",1),
        CONTENT_ID_SERVERDB(ContentUs.CONTENT_ID_SERVERDB, "LONG", 1),
        CONTENT_STATUT(ContentUs.CONTENT_STATUT,"INTEGER",1);

        private final String mName;
        private final String mSqlType;
        private final int mSinceVersion;

        private ContentColumn(String name, String sqlType, int sinceVersion) {
            mName = name;
            mSqlType = sqlType;
            mSinceVersion = sinceVersion;
        }

        @Override
        public String getName() {
            return mName;
        }

        @Override
        public String getType() {
            return mSqlType;
        }

        @Override
        public int getSinceVersion() {
            return mSinceVersion;
        }

    }


}

package org.centum.techconnect.sql;

import android.provider.BaseColumns;

/**
 * Created by doranwalsten on 10/15/16.
 */
public class CommentTableData {


    public CommentTableData() {

    }

    public static abstract class CommentTableInfo implements BaseColumns {

        //Columns and Table Name
        public static final String TABLE_NAME = "comments";
        public static final String COMMENT_ID = "_id";
        public static final String PARENT_ID = "parentId";
        public static final String PARENT_TYPE = "parentType";
        public static final String OWNER = "owner";
        public static final String TEXT = "text";
        public static final String CREATED_DATE = "createdDate";
        public static final String ATTACHMENT = "attachment";

        //Create the Comment Table
        public static final String CREATE_COMMENT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            COMMENT_ID +        " TEXT PRIMARY KEY NOT NULL UNIQUE," +
            PARENT_ID +   " TEXT," +
            PARENT_TYPE +  " TEXT," +
            OWNER +       " TEXT," +
            TEXT +       " TEXT," +
            CREATED_DATE + " DATE," +
            ATTACHMENT +  " TEXT);";

    }
}

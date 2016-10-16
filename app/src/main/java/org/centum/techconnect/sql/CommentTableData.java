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
        public static final String CREATE_COMMENT_TABLE = "CREATE TABLE IF NOT EXISTS " + CommentTableInfo.TABLE_NAME + " (" +
            CommentTableInfo.COMMENT_ID +        " TEXT PRIMARY KEY NOT NULL UNIQUE," +
            CommentTableInfo.PARENT_ID +   " TEXT," +
            CommentTableInfo.PARENT_TYPE +  " TEXT," +
            CommentTableInfo.OWNER +       " TEXT," +
            CommentTableInfo.TEXT +       " TEXT," +
            CommentTableInfo.CREATED_DATE + " DATE," +
            CommentTableInfo.ATTACHMENT +  " TEXT);";

    }
}

package org.centum.techconnect.sql;

import android.provider.BaseColumns;

/**
 * Created by doranwalsten on 10/15/16.
 */
public class CommentTableData {


    public CommentTableData() {

    }

    public static abstract class CommentTableInfo implements BaseColumns {
        public static final String COMMENT_ID = "_id";
        public static final String PARENT_ID = "parentId";
        public static final String PARENT_TYPE = "parentType";
        public static final String OWNER = "owner";
        public static final String TEXT = "text";
        public static final String CREATED_DATE = "createdDate";
        public static final String ATTACHMENT = "attachment";

    }
}

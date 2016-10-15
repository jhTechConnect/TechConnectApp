package org.centum.techconnect.sql;

import android.provider.BaseColumns;

/**
 * Created by doranwalsten on 10/15/16.
 */
public class VerticesTableData {
    public VerticesTableData (){

    }

    public static abstract class VerticesTableInfo implements BaseColumns {
        public static final String VERTEX_ID= "_id";
        public static final String GRAPH_ID= "graphId";
        public static final String NAME= "name";
        public static final String DETAILS= "details";
        public static final String RESOURCES= "resources";
        public static final String IMAGES= "images";

    }
}

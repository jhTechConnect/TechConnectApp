package org.centum.techconnect.sql;

import android.provider.BaseColumns;
import org.centum.techconnect.sql.GraphTableData.GraphTableInfo;

/**
 * Created by doranwalsten on 10/15/16.
 */
public class VerticesTableData {
    public VerticesTableData (){

    }

    public static abstract class VerticesTableInfo implements BaseColumns {
        //Columns and Table Name
        public static final String TABLE_NAME = "vertices";
        public static final String VERTEX_ID= "_id";
        public static final String GRAPH_ID= "graphId";
        public static final String NAME= "name";
        public static final String DETAILS= "details";
        public static final String RESOURCES= "resources";
        public static final String IMAGES= "images";

        //Creates the Vertices Table Object
        public static final String CREATE_VERTEX_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            VERTEX_ID + " TEXT PRIMARY KEY NOT NULL UNIQUE," +
            GRAPH_ID + " TEXT," +
            NAME + " TEXT," +
            DETAILS + " TEXT," +
            RESOURCES + " TEXT," +
            IMAGES + " TEXT," +
            "FOREIGN KEY (graphId) REFERENCES " + GraphTableInfo.TABLE_NAME + " (_id));";

    }
}

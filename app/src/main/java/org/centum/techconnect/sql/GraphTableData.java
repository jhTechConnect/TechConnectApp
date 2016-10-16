package org.centum.techconnect.sql;

import android.provider.BaseColumns;

/**
 * Created by doranwalsten on 10/15/16.
 */
public class GraphTableData {

    public GraphTableData() {

    }

    public static abstract class GraphTableInfo implements BaseColumns {
        //Columns and Table Name
        public static final String TABLE_NAME = "graphs";
        public static final String GRAPH_ID = "_id";
        public static final String FIRSTVERTEX = "firstVertex";

        //Create the Table
        public static final String CREATE_GRAPH_TABLE = "CREATE TABLE IF NOT EXISTS " + GraphTableInfo.TABLE_NAME + " (" +
            GraphTableInfo.GRAPH_ID + "TEXT PRIMARY KEY NOT NULL UNIQUE," +
            GraphTableInfo.FIRSTVERTEX + "TEXT," +
            "FOREIGN KEY (firstVertex) REFERENCES " + VerticesTableData.VerticesTableInfo.TABLE_NAME +  " (_id));";

    }
}

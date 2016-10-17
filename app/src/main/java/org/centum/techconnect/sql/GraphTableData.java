package org.centum.techconnect.sql;

import android.provider.BaseColumns;
import org.centum.techconnect.sql.VerticesTableData.VerticesTableInfo;

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
        public static final String CREATE_GRAPH_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            GRAPH_ID + " TEXT PRIMARY KEY NOT NULL UNIQUE, " +
            FIRSTVERTEX + " TEXT, FOREIGN KEY (firstVertex) REFERENCES " + VerticesTableInfo.TABLE_NAME +
            " (_id) DEFERRABLE INITIALLY DEFERRED);";

    }
}

package org.centum.techconnect.sql;

import android.provider.BaseColumns;
import org.centum.techconnect.sql.VerticesTableData.VerticesTableInfo;

/**
 * Created by doranwalsten on 10/15/16.
 */
public class EdgeTableData {

    public EdgeTableData() {

    }

    public static abstract class EdgeTableInfo implements BaseColumns {
        //Column and Table name
        public static final String TABLE_NAME = "edges";
        public static final String EDGE_ID = "_id";
        public static final String GRAPH_ID = "graph_id";
        public static final String LABEL = "_label";
        public static final String OUTV = "_outV";
        public static final String INV = "_inV";
        public static final String DETAILS = "details";

        //Create the edges table
        public static final String CREATE_EDGE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            EDGE_ID + " TEXT PRIMARY KEY NOT NULL UNIQUE," +
            GRAPH_ID + " TEXT," +
            LABEL + " TEXT," +
            OUTV + " TEXT," +
            INV + " TEXT," +
            DETAILS + " TEXT, FOREIGN KEY (graphId) REFERENCES graphs (_id)," +
            "FOREIGN KEY (_outV) REFERENCES " + VerticesTableInfo.TABLE_NAME + " (_id), FOREIGN KEY (_inV) REFERENCES " +
            VerticesTableInfo.TABLE_NAME + " (_id));";

    }
}

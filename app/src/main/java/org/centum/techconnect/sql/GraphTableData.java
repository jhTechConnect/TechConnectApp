package org.centum.techconnect.sql;

import android.provider.BaseColumns;

/**
 * Created by doranwalsten on 10/15/16.
 */
public class GraphTableData {

    public GraphTableData() {

    }

    public static abstract class GraphTableInfo implements BaseColumns {
        public static final String GRAPH_ID = "_id";
        public static final String FIRSTVERTEX = "firstVertex";

    }
}

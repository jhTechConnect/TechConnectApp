package org.centum.techconnect.sql;

import android.provider.BaseColumns;

/**
 * Created by doranwalsten on 10/15/16.
 */
public class EdgeTableData {

    public EdgeTableData() {

    }

    public static abstract class EdgeTableInfo implements BaseColumns {
        public static final String EDGE_ID = "_id";
        public static final String GRAPH_ID = "graph_id";
        public static final String LABEL = "_label";
        public static final String OUTV = "_outV";
        public static final String INV = "_inV";
        public static final String DETAILS = "details";

    }
}

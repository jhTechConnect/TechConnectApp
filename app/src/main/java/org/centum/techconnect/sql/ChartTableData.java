package org.centum.techconnect.sql;

import android.provider.BaseColumns;

/**
 * This is a file used to define the structure of the "Chart" table within the SQL database on the
 * phone.
 *
 * Created by doranwalsten on 10/13/16.
 */
public class ChartTableData {

    public ChartTableData() {

    }

    //Following the SQL Database definition provided by Phani in
    //Android SQL Specs
    public static abstract class ChartTableInfo implements BaseColumns {
        public static final String CHART_ID = "_id";
        public static final String CHART_NAME = "name";
        public static final String CHART_DESC = "description";
        public static final String UPDATE_DATE = "updateDate";
        public static final String CHART_VERSION = "version";
        public static final String CHART_OWNER = "owner";
        public static final String GRAPH_ID = "graphId";
        public static final String CHART_ALL_RES = "all_res";
        public static final String CHART_IMAGE = "image";
        public static final String CHART_RESOURCES = "resources";
        public static final String CHART_TYPE = "type";
        public static final String CHART_SCORE = "score";

    }
}

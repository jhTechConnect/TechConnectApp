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
        //Importantly, I need to store the name of the table to be referenced in other classes
        public static final String TABLE_NAME = "charts";

        //Column Names to be safe
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

        //Create the Chart Table object
        public static final String CREATE_CHART_TABLE = "CREATE TABLE IF NOT EXISTS " + ChartTableInfo.TABLE_NAME + " (" +
            ChartTableInfo.CHART_ID + " TEXT PRIMARY KEY NOT NULL UNIQUE," +
            ChartTableInfo.CHART_NAME + " TEXT," +
            ChartTableInfo.CHART_DESC + " TEXT," +
            ChartTableInfo.UPDATE_DATE + " DATE," +
            ChartTableInfo.CHART_VERSION + " TEXT," +
            ChartTableInfo.CHART_OWNER + " TEXT," +
            ChartTableInfo.GRAPH_ID + " TEXT," +
            ChartTableInfo.CHART_ALL_RES + " TEXT," +
            ChartTableInfo.CHART_IMAGE + " TEXT," +
            ChartTableInfo.CHART_RESOURCES + " TEXT," +
            ChartTableInfo.CHART_TYPE + " TEXT," +
            ChartTableInfo.CHART_SCORE + " INTEGER," +
            "FOREIGN KEY (graphId) REFERENCES " + GraphTableData.GraphTableInfo.TABLE_NAME + " (_id)) WITHOUT ROWID;";

    }
}

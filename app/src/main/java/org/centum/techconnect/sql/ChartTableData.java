package org.centum.techconnect.sql;

import android.provider.BaseColumns;
import org.centum.techconnect.sql.GraphTableData.GraphTableInfo;

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
        public static final String CREATE_CHART_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            CHART_ID + " TEXT PRIMARY KEY NOT NULL UNIQUE," +
            CHART_NAME + " TEXT," +
            CHART_DESC + " TEXT," +
            UPDATE_DATE + " DATE," +
            CHART_VERSION + " TEXT," +
            CHART_OWNER + " TEXT," +
            GRAPH_ID + " TEXT," +
            CHART_ALL_RES + " TEXT," +
            CHART_IMAGE + " TEXT," +
            CHART_RESOURCES + " TEXT," +
            CHART_TYPE + " TEXT," +
            CHART_SCORE + " INTEGER," +
            "FOREIGN KEY (graphId) REFERENCES " + GraphTableInfo.TABLE_NAME + " (_id)) WITHOUT ROWID;";

    }
}

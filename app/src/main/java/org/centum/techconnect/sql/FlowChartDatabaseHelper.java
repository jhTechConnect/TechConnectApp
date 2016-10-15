package org.centum.techconnect.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.centum.techconnect.sql.ChartTableData.ChartTableInfo;


/**
 * Created by doranwalsten on 10/13/16.
 */
public class FlowChartDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FlowChart.db";

    /*
      Here, we define the queries that we're interested in. Pulled from the
      SQL specs defined by Phani online
     */

    //Create the Chart Table object
    public static final String CREATE_CHART_TABLE = "CREATE TABLE IF NOT EXISTS charts (" +
            ChartTableInfo.CHART_ID +  " TEXT PRIMARY KEY NOT NULL UNIQUE," +
            ChartTableInfo.CHART_NAME +        " TEXT," +
            ChartTableInfo.CHART_DESC + " TEXT," +
            ChartTableInfo.UPDATE_DATE + " DATE," +
            ChartTableInfo.CHART_VERSION +    " TEXT," +
            ChartTableInfo.CHART_OWNER +      " TEXT," +
            ChartTableInfo.GRAPH_ID +    " TEXT," +
            ChartTableInfo.CHART_ALL_RES +     " TEXT," +
            ChartTableInfo.CHART_IMAGE  +    " TEXT," +
            ChartTableInfo.CHART_RESOURCES +   " TEXT," +
            ChartTableInfo.CHART_TYPE +       " TEXT," +
            ChartTableInfo.CHART_SCORE +      " INTEGER," +
            "FOREIGN KEY (graphId) REFERENCES graphs (_id)) WITHOUT ROWID;";


    public FlowChartDatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sql) {
        sql.execSQL(CREATE_CHART_TABLE);//Creates the table for Charts

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

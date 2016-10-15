package org.centum.techconnect.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.centum.techconnect.sql.ChartTableData.ChartTableInfo;
import org.centum.techconnect.sql.GraphTableData.GraphTableInfo;
import org.centum.techconnect.sql.VerticesTableData.VerticesTableInfo;
import org.centum.techconnect.sql.EdgeTableData.EdgeTableInfo;
import org.centum.techconnect.sql.CommentTableData.CommentTableInfo;

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

    //Create the Graph Table Object
    public static final String CREATE_GRAPH_TABLE = "CREATE TABLE IF NOT EXISTS graphs (" +
            GraphTableInfo.GRAPH_ID + "TEXT PRIMARY KEY NOT NULL UNIQUE," +
            GraphTableInfo.FIRSTVERTEX + "TEXT," +
            "FOREIGN KEY (firstVertex) REFERENCES vertices (_id));";

    //Creates the Vertices Table Object
    public static final String CREATE_VERTEX_TABLE = "CREATE TABLE IF NOT EXISTS vertices (" +
            VerticesTableInfo.VERTEX_ID + " TEXT PRIMARY KEY NOT NULL UNIQUE," +
            VerticesTableInfo.GRAPH_ID +   " TEXT," +
            VerticesTableInfo.NAME +      " TEXT," +
            VerticesTableInfo.DETAILS +   " TEXT," +
            VerticesTableInfo.RESOURCES  +   " TEXT," +
            VerticesTableInfo.IMAGES +    " TEXT," +
            "FOREIGN KEY (graphId) REFERENCES graphs (_id));";


    public static final String CREATE_EDGE_TABLE = "CREATE TABLE IF NOT EXISTS edges (" +
            EdgeTableInfo.EDGE_ID +     " TEXT PRIMARY KEY NOT NULL UNIQUE," +
            EdgeTableInfo.GRAPH_ID +  " TEXT," +
            EdgeTableInfo.LABEL  + " TEXT," +
            EdgeTableInfo.OUTV +   " TEXT," +
            EdgeTableInfo.INV +    " TEXT," +
            EdgeTableInfo.DETAILS + " TEXT, FOREIGN KEY (graphId) REFERENCES graphs (_id)," +
            "FOREIGN KEY (_outV) REFERENCES vertices (_id), FOREIGN KEY (_inV) REFERENCES vertices (_id));";

    public static final String CREATE_COMMENT_TABLE = "CREATE TABLE IF NOT EXISTS comments (" +
            CommentTableInfo.COMMENT_ID +        " TEXT PRIMARY KEY NOT NULL UNIQUE," +
            CommentTableInfo.PARENT_ID +   " TEXT," +
            CommentTableInfo.PARENT_TYPE +  " TEXT," +
            CommentTableInfo.OWNER +       " TEXT," +
            CommentTableInfo.TEXT +       " TEXT," +
            CommentTableInfo.CREATED_DATE + " DATE," +
            CommentTableInfo.ATTACHMENT +  " TEXT);";

    public FlowChartDatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sql) {
        sql.execSQL(CREATE_CHART_TABLE);//Creates the table for Charts
        sql.execSQL(CREATE_GRAPH_TABLE);//Creates the table for Graph
        sql.execSQL(CREATE_VERTEX_TABLE);//Creates the table for Vertices
        sql.execSQL(CREATE_EDGE_TABLE);//Creates the table for Edges
        sql.execSQL(CREATE_COMMENT_TABLE);//Creates the table for Comments

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

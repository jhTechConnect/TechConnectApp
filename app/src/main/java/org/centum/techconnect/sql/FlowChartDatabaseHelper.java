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

    public FlowChartDatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sql) {
        sql.execSQL(ChartTableInfo.CREATE_CHART_TABLE);//Creates the table for Charts
        sql.execSQL(GraphTableInfo.CREATE_GRAPH_TABLE);//Creates the table for Graph
        sql.execSQL(VerticesTableInfo.CREATE_VERTEX_TABLE);//Creates the table for Vertices
        sql.execSQL(EdgeTableInfo.CREATE_EDGE_TABLE);//Creates the table for Edges
        sql.execSQL(CommentTableInfo.CREATE_COMMENT_TABLE);//Creates the table for Comments

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

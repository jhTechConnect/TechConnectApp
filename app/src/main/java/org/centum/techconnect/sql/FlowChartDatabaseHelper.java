package org.centum.techconnect.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.java.model.Edge;
import com.java.model.FlowChart;
import com.java.model.Graph;
import com.java.model.Vertex;

import org.centum.techconnect.sql.ChartTableData.ChartTableInfo;
import org.centum.techconnect.sql.CommentTableData.CommentTableInfo;
import org.centum.techconnect.sql.EdgeTableData.EdgeTableInfo;
import org.centum.techconnect.sql.GraphTableData.GraphTableInfo;
import org.centum.techconnect.sql.VerticesTableData.VerticesTableInfo;

import java.util.List;

/**
 * Using a singleton class in order to make the same database visible/accessible to any activity
 * during the operation of the app
 * Created by doranwalsten on 10/13/16.
 */
public class FlowChartDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FlowChart.db";
    private static FlowChartDatabaseHelper instance = null;
    private final Context context;

    //Singleton private constructor
    private FlowChartDatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null, DATABASE_VERSION);
        this.context = context;
    }

    //Singleton get method to return the single instance of the class
    public static FlowChartDatabaseHelper get(Context context) {
        if (instance == null) { instance = new FlowChartDatabaseHelper(context); }
        return instance;
    }

    //Do I want to check whether the instance has been set?
    public static FlowChartDatabaseHelper get() {return instance;}

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

        //Used to add columns/tables, etc. Not relevant at this point
    }

    public void putCharts(List<FlowChart> charts) {
        for (FlowChart f : charts) {
            putChart(f);
        }
    }

    /**
     * This method takes a FlowChart object and pushes all information into the SQL database
     *
     * @param flowChart - The Flowchart object
     */
    public void putChart(FlowChart flowChart) {

        // Generating random IDs for graphs

        String graph_ID = randomId();

        //Insert Graph, Vertices, and Edges
        putGraph(flowChart.getGraph(), graph_ID);

        //Create ContentValues object with all columns and values for just Chart
        ContentValues cv_chart =  new ContentValues();
        cv_chart.put(ChartTableInfo.CHART_ID,flowChart.getId());
        cv_chart.put(ChartTableInfo.CHART_NAME,flowChart.getName());
        cv_chart.put(ChartTableInfo.CHART_DESC,flowChart.getDescription());
        cv_chart.put(ChartTableInfo.UPDATE_DATE,flowChart.getUpdatedDate());
        cv_chart.put(ChartTableInfo.CHART_VERSION,flowChart.getVersion());
        cv_chart.put(ChartTableInfo.CHART_OWNER,flowChart.getOwner());
        cv_chart.put(ChartTableInfo.GRAPH_ID, graph_ID);

        //Join all resources together as a comma-separated list
        String all_res_list = TextUtils.join(",", flowChart.getAllRes());

        cv_chart.put(ChartTableInfo.CHART_ALL_RES,all_res_list);
        cv_chart.put(ChartTableInfo.CHART_IMAGE,flowChart.getImage());

        //Join all general resources in a list
        String res_list = TextUtils.join(",",flowChart.getResources());
        cv_chart.put(ChartTableInfo.CHART_RESOURCES, res_list);

        cv_chart.put(ChartTableInfo.CHART_TYPE, flowChart.getType().toString());
        cv_chart.put(ChartTableInfo.CHART_SCORE, flowChart.getScore());

        SQLiteDatabase sql = getWritableDatabase();
        //Insert chart
        try {
            sql.insert(ChartTableInfo.TABLE_NAME, null, cv_chart);
        } catch (Exception e) {
            Log.e("Database Op",e.getMessage());
        }

        Log.d("Database Op", "Chart Info Inserted Successfully");
    }

    /** This method takes a Graph object and pushes all information into the SQL database
     *
     * @param g
     */
    public void putGraph(Graph g, String graphID) {
        SQLiteDatabase sql = getWritableDatabase();
        sql.beginTransaction();//Insert Graph into graph table, vertices into vertex table

        //Create ContentValues object with all columns and values for just Graph
        ContentValues cv_graph = new ContentValues();
        cv_graph.put(GraphTableInfo.GRAPH_ID, graphID);
        cv_graph.put(GraphTableInfo.FIRSTVERTEX, g.getFirstVertex());

        //Insert Graph w/o firstVertex
        try {
            sql.insert(GraphTableInfo.TABLE_NAME, null, cv_graph);
            Log.d("Database Op", "Graph Info Inserted Successfully");
            //Insert all vertices
            putVertices(sql, g.getVertices(), graphID);
            sql.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Database Op",e.getMessage());
            sql.endTransaction();
        }
        sql.endTransaction(); //End the transaction of inserting into graph and vertices

        //Insert all edges
        sql.beginTransaction(); //Insert edges
        try {
            putEdges(sql, g.getEdges(), graphID);
            sql.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Database Op",e.getMessage());
            sql.endTransaction();
        }
        sql.endTransaction(); //End the transaction of inserting edges
    }

    /**
     * Create Content values for vertices and insert
     * @param sql - SQLDatabase writer to put vertices in table
     * @param vertices - List of Vertices to insert
     */
    public void putVertices(SQLiteDatabase sql, List<Vertex> vertices,  String graphID) {
        for (Vertex v: vertices) {
            ContentValues cv_vertex = new ContentValues();
            cv_vertex.put(VerticesTableInfo.VERTEX_ID, v.getId());
            cv_vertex.put(VerticesTableInfo.GRAPH_ID, graphID);
            cv_vertex.put(VerticesTableInfo.NAME, v.getName() );
            cv_vertex.put(VerticesTableInfo.DETAILS, v.getDetails());

            //Join all resources together as a comma-separated list
            String all_res_list = TextUtils.join(",", v.getResources());
            String all_img_list = TextUtils.join(",", v.getImages());

            cv_vertex.put(VerticesTableInfo.RESOURCES, all_res_list);
            cv_vertex.put(VerticesTableInfo.IMAGES, all_img_list);

            sql.insert(VerticesTableInfo.TABLE_NAME, null, cv_vertex);


        }
        Log.d("Database Op", "Vertices Info Inserted Successfully");
    }

    /**
     * Create Content values for edges and insert
     * @param sql - SQLDatabase writer to put edges in table
     * @param edges - List of edges to insert
     */
    public void putEdges(SQLiteDatabase sql, List<Edge> edges,  String graphID) {
        for (Edge e: edges) {
            ContentValues cv_edge = new ContentValues();
            cv_edge.put(EdgeTableInfo.EDGE_ID, e.getId());
            cv_edge.put(EdgeTableInfo.GRAPH_ID, graphID);
            cv_edge.put(EdgeTableInfo.LABEL, e.getLabel());
            cv_edge.put(EdgeTableInfo.OUTV, e.getOutV());
            cv_edge.put(EdgeTableInfo.INV, e.getInV());
            cv_edge.put(EdgeTableInfo.DETAILS, e.getDetails());

            sql.insert(EdgeTableInfo.TABLE_NAME, null, cv_edge);
        }
        Log.d("Database Op", "Edges Info Inserted Successfully");
    }



    private static String randomId(){
        String validChars = "23456789ABCDEFGHJKLMNPQRSTWXYZabcdefghijkmnopqrstuvwxyz";
        char chars[] = new char[17];
        for(int i = 0; i < chars.length; i++){
            int rand = (int)(Math.random()*chars.length);
            chars[i] = validChars.charAt(rand);
        }
        return new String(chars);
    }
}

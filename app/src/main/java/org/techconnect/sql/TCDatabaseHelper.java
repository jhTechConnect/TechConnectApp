package org.techconnect.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import org.techconnect.networkhelper.model.Edge;
import org.techconnect.networkhelper.model.FlowChart;
import org.techconnect.networkhelper.model.Graph;
import org.techconnect.networkhelper.model.Vertex;
import org.techconnect.sql.TCDatabaseContract.ChartEntry;

import java.util.List;

/**
 * Using a singleton class in order to make the same database visible/accessible to any activity
 * during the operation of the app
 * Created by doranwalsten on 10/13/16.
 */
public class TCDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FlowChart.db";
    private static TCDatabaseHelper instance = null;
    private final Context context;

    private TCDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static TCDatabaseHelper get(Context context) {
        if (instance == null) {
            instance = new TCDatabaseHelper(context);
        }
        return instance;
    }

    public static TCDatabaseHelper get() {
        return instance;
    }

    /**
     * Generates a random ID 17 chars long.
     *
     * @return A random ID
     */
    private static String randomId() {
        String validChars = "23456789ABCDEFGHJKLMNPQRSTWXYZabcdefghijkmnopqrstuvwxyz";
        char chars[] = new char[17];
        for (int i = 0; i < chars.length; i++) {
            int rand = (int) (Math.random() * chars.length);
            chars[i] = validChars.charAt(rand);
        }
        return new String(chars);
    }

    @Override
    public void onCreate(SQLiteDatabase sql) {
        sql.execSQL(TCDatabaseContract.ChartEntry.CREATE_CHART_TABLE);
        sql.execSQL(TCDatabaseContract.GraphEntry.CREATE_GRAPH_TABLE);
        sql.execSQL(TCDatabaseContract.VertexEntry.CREATE_VERTEX_TABLE);
        sql.execSQL(TCDatabaseContract.EdgeEntry.CREATE_EDGE_TABLE);
        sql.execSQL(TCDatabaseContract.CommentEntry.CREATE_COMMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public void insertCharts(List<FlowChart> charts) {
        for (FlowChart f : charts) {
            insertChart(f);
        }
    }

    /**
     * This method takes a FlowChart object and pushes all information into the SQL database
     *
     * @param flowChart - The Flowchart object
     */
    public void insertChart(FlowChart flowChart) {
        //Insert Graph, Vertices, and Edges
        String graphId = insertGraph(flowChart.getGraph());

        //Create ContentValues object with all columns and values for just Chart
        ContentValues chartContentValues = new ContentValues();
        chartContentValues.put(ChartEntry.CHART_ID, flowChart.getId());
        chartContentValues.put(ChartEntry.CHART_NAME, flowChart.getName());
        chartContentValues.put(ChartEntry.CHART_DESC, flowChart.getDescription());
        chartContentValues.put(ChartEntry.UPDATE_DATE, flowChart.getUpdatedDate());
        chartContentValues.put(ChartEntry.CHART_VERSION, flowChart.getVersion());
        chartContentValues.put(ChartEntry.CHART_OWNER, flowChart.getOwner());
        chartContentValues.put(ChartEntry.GRAPH_ID, graphId);

        //Join all resources together as a comma-separated list
        String allResList = TextUtils.join(",", flowChart.getAllRes());

        chartContentValues.put(ChartEntry.CHART_ALL_RES, allResList);
        chartContentValues.put(ChartEntry.CHART_IMAGE, flowChart.getImage());

        //Join all general resources in a list
        String resList = TextUtils.join(",", flowChart.getResources());
        chartContentValues.put(ChartEntry.CHART_RESOURCES, resList);

        chartContentValues.put(ChartEntry.CHART_TYPE, flowChart.getType().toString());
        chartContentValues.put(ChartEntry.CHART_SCORE, flowChart.getScore());

        SQLiteDatabase sql = getWritableDatabase();
        //Insert chart
        try {
            sql.insert(ChartEntry.TABLE_NAME, null, chartContentValues);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
        }

        Log.d(this.getClass().getName(), "Chart Info Inserted Successfully");
    }

    /**
     * This method takes a Graph object and pushes all information into the SQL database
     *
     * @param g
     */
    private String insertGraph(Graph g) {
        String graphId = randomId();
        SQLiteDatabase sql = getWritableDatabase();
        sql.beginTransaction();//Insert Graph into graph table, vertices into vertex table

        //Create ContentValues object with all columns and values for just Graph
        ContentValues graphContentValues = new ContentValues();
        graphContentValues.put(TCDatabaseContract.GraphEntry.GRAPH_ID, graphId);
        graphContentValues.put(TCDatabaseContract.GraphEntry.FIRST_VERTEX, g.getFirstVertex());

        //Insert Graph w/o firstVertex
        try {
            sql.insert(TCDatabaseContract.GraphEntry.TABLE_NAME, null, graphContentValues);
            Log.d(this.getClass().getName(), "Graph Info Inserted Successfully");
            //Insert all vertices
            insertVertices(sql, g.getVertices(), graphId);
            sql.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
            sql.endTransaction();
        }
        sql.endTransaction(); //End the transaction of inserting into graph and vertices

        //Insert all edges
        sql.beginTransaction(); //Insert edges
        try {
            insertEdges(sql, g.getEdges(), graphId);
            sql.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
            sql.endTransaction();
        }
        sql.endTransaction();
        return graphId;
    }

    /**
     * Create Content values for vertices and insert
     *
     * @param sql      - SQLDatabase writer to put vertices in table
     * @param vertices - List of Vertices to insert
     */
    private void insertVertices(SQLiteDatabase sql, List<Vertex> vertices, String graphID) {
        for (Vertex v : vertices) {
            ContentValues vertexContentValues = new ContentValues();
            vertexContentValues.put(TCDatabaseContract.VertexEntry.VERTEX_ID, v.getId());
            vertexContentValues.put(TCDatabaseContract.VertexEntry.GRAPH_ID, graphID);
            vertexContentValues.put(TCDatabaseContract.VertexEntry.NAME, v.getName());
            vertexContentValues.put(TCDatabaseContract.VertexEntry.DETAILS, v.getDetails());

            //Join all resources together as a comma-separated list
            String allRes = TextUtils.join(",", v.getResources());
            String allImgs = TextUtils.join(",", v.getImages());

            vertexContentValues.put(TCDatabaseContract.VertexEntry.RESOURCES, allRes);
            vertexContentValues.put(TCDatabaseContract.VertexEntry.IMAGES, allImgs);

            sql.insert(TCDatabaseContract.VertexEntry.TABLE_NAME, null, vertexContentValues);
        }
        Log.d(this.getClass().getName(), "Vertices Info Inserted Successfully");
    }

    /**
     * Create Content values for edges and insert
     *
     * @param sql   - SQLDatabase writer to put edges in table
     * @param edges - List of edges to insert
     */
    private void insertEdges(SQLiteDatabase sql, List<Edge> edges, String graphID) {
        for (Edge e : edges) {
            ContentValues edgeContentValues = new ContentValues();
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.EDGE_ID, e.getId());
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.GRAPH_ID, graphID);
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.LABEL, e.getLabel());
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.OUT_VERTEX, e.getOutV());
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.IN_VERTEX, e.getInV());
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.DETAILS, e.getDetails());

            sql.insert(TCDatabaseContract.EdgeEntry.TABLE_NAME, null, edgeContentValues);
        }
        Log.d(this.getClass().getName(), "Edges Info Inserted Successfully");
    }
}

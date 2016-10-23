package org.techconnect.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import org.techconnect.networkhelper.model.Comment;
import org.techconnect.networkhelper.model.Edge;
import org.techconnect.networkhelper.model.FlowChart;
import org.techconnect.networkhelper.model.Graph;
import org.techconnect.networkhelper.model.Vertex;
import org.techconnect.sql.TCDatabaseContract.ChartEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Using a singleton class in order to make the same database visible/accessible to any activity
 * during the operation of the app
 * Created by doranwalsten on 10/13/16.
 */
public class TCDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FlowChart.db";
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
    private static String getRandomId() {
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

    public void upsertCharts(FlowChart charts[]) {
        for (FlowChart f : charts) {
            upsertChart(f);
        }
    }

    public void insertCharts(FlowChart charts[]) {
        for (FlowChart f : charts) {
            insertChart(f);
        }
    }

    /**
     * Get a map of chart names, mapping to their id.
     */
    public Map<String, String> getChartNamesAndIDs() {
        Map<String, String> set = new HashMap<>();
        Cursor c = getReadableDatabase().query(ChartEntry.TABLE_NAME, new String[]{ChartEntry.ID, ChartEntry.NAME}, null,
                null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            set.put(c.getString(c.getColumnIndexOrThrow(ChartEntry.NAME)), c.getString(c.getColumnIndexOrThrow(ChartEntry.ID)));
            c.moveToNext();
        }
        return set;
    }

    public String[] getAllChartIds() {
        List<String> ids = new LinkedList<>();
        Cursor c = getReadableDatabase().query(ChartEntry.TABLE_NAME, new String[]{ChartEntry.ID}, null,
                null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            ids.add(c.getString(c.getColumnIndexOrThrow(ChartEntry.ID)));
            c.moveToNext();
        }
        return ids.toArray(new String[ids.size()]);
    }

    public Cursor getAllFlowchartsCursor() {
        return getAllFlowchartsCursor(null);
    }

    public Cursor getAllFlowchartsCursor(String filter) {
        if (filter == null || TextUtils.isEmpty(filter.trim())) {
            return getReadableDatabase().query(ChartEntry.TABLE_NAME, null, null,
                    null, null, null, null);
        } else {
            String selection = ChartEntry.NAME + " LIKE ?";
            String selectArgs[] = new String[]{"%" + filter.trim() + "%"};
            return getReadableDatabase().query(ChartEntry.TABLE_NAME, null, selection,
                    selectArgs, null, null, null);
        }
    }


    public FlowChart getChart(String id) {
        String selection = ChartEntry.ID + " = ?";
        String selectArgs[] = {id};
        Cursor c = getReadableDatabase().query(ChartEntry.TABLE_NAME, null, selection,
                selectArgs, null, null, null);
        c.moveToFirst();
        if (c.getCount() == 0) {
            return null;
        }
        FlowChart chart = new FlowChart();
        chart.setId(c.getString(c.getColumnIndexOrThrow(ChartEntry.ID)));
        chart.setName(c.getString(c.getColumnIndexOrThrow(ChartEntry.NAME)));
        chart.setDescription(c.getString(c.getColumnIndexOrThrow(ChartEntry.DESCRIPTION)));
        chart.setUpdatedDate(c.getString(c.getColumnIndexOrThrow(ChartEntry.UPDATED_DATE)));
        chart.setVersion(c.getString(c.getColumnIndexOrThrow(ChartEntry.VERSION)));
        chart.setOwner(c.getString(c.getColumnIndexOrThrow(ChartEntry.OWNER)));
        chart.setImage(c.getString(c.getColumnIndexOrThrow(ChartEntry.IMAGE)));
        chart.setType(FlowChart.ChartType.valueOf(c.getString(c.getColumnIndexOrThrow(ChartEntry.TYPE))));
        chart.setScore(c.getInt(c.getColumnIndexOrThrow(ChartEntry.SCORE)));
        String allRes = c.getString(c.getColumnIndexOrThrow(ChartEntry.ALL_RESOURCES));
        String res = c.getString(c.getColumnIndexOrThrow(ChartEntry.RESOURCES));
        if (allRes != null && !allRes.trim().equals("")) {
            chart.setAllRes(Arrays.asList(allRes.split(",")));
        } else {
            chart.setAllRes(new ArrayList<String>());
        }
        if (res != null && !res.trim().equals("")) {
            chart.setResources(Arrays.asList(res.split(",")));
        } else {
            chart.setResources(new ArrayList<String>());
        }

        String graphId = c.getString(c.getColumnIndexOrThrow(ChartEntry.GRAPH_ID));
        chart.setGraph(getGraph(graphId));
        chart.setComments(getComments(id, TCDatabaseContract.CommentEntry.PARENT_TYPE_CHART));

        return chart;
    }

    public void upsertChart(FlowChart flowChart) {
        if (getChart(flowChart.getId()) == null) {
            insertChart(flowChart);
        } else {
            String graphId = insertGraph(flowChart.getGraph());
            ContentValues chartContentValues = getChartContentValues(flowChart, graphId);
            try {
                insertComments(flowChart.getComments(), flowChart.getId(), TCDatabaseContract.CommentEntry.PARENT_TYPE_CHART);
                getWritableDatabase().update(ChartEntry.TABLE_NAME, chartContentValues,
                        ChartEntry.ID + " = ?", new String[]{flowChart.getId()});
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage());
            }

            Log.d(this.getClass().getName(), "Chart Info Updated Successfully");
        }
    }

    /**
     * This method takes a FlowChart object and pushes all information into the SQL database
     * <p/>
     *
     * @param flowChart - The Flowchart object
     */
    public void insertChart(FlowChart flowChart) {
        String graphId = insertGraph(flowChart.getGraph());

        //Create ContentValues object with all columns and values for just Chart
        ContentValues chartContentValues = getChartContentValues(flowChart, graphId);

        //Insert chart & comments
        try {
            insertComments(flowChart.getComments(), flowChart.getId(), TCDatabaseContract.CommentEntry.PARENT_TYPE_CHART);
            getWritableDatabase().insert(ChartEntry.TABLE_NAME, null, chartContentValues);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
        }

        Log.d(this.getClass().getName(), "Chart Info Inserted Successfully");
    }

    @NonNull
    private ContentValues getChartContentValues(FlowChart flowChart, String graphId) {
        ContentValues chartContentValues = new ContentValues();
        chartContentValues.put(ChartEntry.ID, flowChart.getId());
        chartContentValues.put(ChartEntry.NAME, flowChart.getName());
        chartContentValues.put(ChartEntry.DESCRIPTION, flowChart.getDescription());
        chartContentValues.put(ChartEntry.UPDATED_DATE, flowChart.getUpdatedDate());
        chartContentValues.put(ChartEntry.VERSION, flowChart.getVersion());
        chartContentValues.put(ChartEntry.OWNER, flowChart.getOwner());
        chartContentValues.put(ChartEntry.GRAPH_ID, graphId);

        //Join all resources together as a comma-separated list
        String allResList = TextUtils.join(",", flowChart.getAllRes());

        chartContentValues.put(ChartEntry.ALL_RESOURCES, allResList);
        chartContentValues.put(ChartEntry.IMAGE, flowChart.getImage());

        //Join all general resources in a list
        String resList = TextUtils.join(",", flowChart.getResources());
        chartContentValues.put(ChartEntry.RESOURCES, resList);

        chartContentValues.put(ChartEntry.TYPE, flowChart.getType().toString());
        chartContentValues.put(ChartEntry.SCORE, flowChart.getScore());
        return chartContentValues;
    }

    private List<Comment> getComments(String parentId, String parentType) {
        String selection = TCDatabaseContract.CommentEntry.PARENT_ID + " = '" + parentId + "' AND " +
                TCDatabaseContract.CommentEntry.PARENT_TYPE + " = '" + parentType + "'";
        Cursor c = getReadableDatabase().query(TCDatabaseContract.CommentEntry.TABLE_NAME,
                null, selection, null, null, null, null);
        c.moveToFirst();
        List<Comment> comments = new ArrayList<>(c.getCount());

        while (!c.isAfterLast()) {
            Comment comment = new Comment();
            comment.setOwner(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.CommentEntry.OWNER)));
            comment.setAttachment(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.CommentEntry.ATTACHMENT)));
            comment.setCreatedDate(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.CommentEntry.CREATED_DATE)));
            comment.setText(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.CommentEntry.TEXT)));
            comments.add(comment);
            c.moveToNext();
        }
        return comments;
    }

    private void insertComments(List<Comment> comments, String parentId, String parentType) {
        for (Comment comment : comments) {
            insertComment(comment, parentId, parentType);
        }
    }

    private void insertComment(Comment comment, String parentId, String parentType) {
        ContentValues commentContentValues = new ContentValues();
        commentContentValues.put(TCDatabaseContract.CommentEntry.ID, getRandomId());
        commentContentValues.put(TCDatabaseContract.CommentEntry.PARENT_ID, parentId);
        commentContentValues.put(TCDatabaseContract.CommentEntry.PARENT_TYPE, parentType);
        commentContentValues.put(TCDatabaseContract.CommentEntry.OWNER, comment.getOwner());
        commentContentValues.put(TCDatabaseContract.CommentEntry.TEXT, comment.getText());
        commentContentValues.put(TCDatabaseContract.CommentEntry.CREATED_DATE, comment.getCreatedDate());
        commentContentValues.put(TCDatabaseContract.CommentEntry.ATTACHMENT, comment.getAttachment());

        try {
            getWritableDatabase().insert(TCDatabaseContract.CommentEntry.TABLE_NAME, null, commentContentValues);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
        }

        Log.d(this.getClass().getName(), "Comment Info Inserted Successfully");
    }

    private Graph getGraph(String id) {
        String selection = TCDatabaseContract.GraphEntry.ID + " = ?";
        String selectionArgs[] = {id};
        Cursor c = getReadableDatabase().query(TCDatabaseContract.GraphEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            String firstVertex = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.GraphEntry.FIRST_VERTEX));
            return new Graph(getVertices(id), getEdges(id), firstVertex);
        }
        return null;
    }

    private List<Vertex> getVertices(String graphId) {
        String selection = TCDatabaseContract.VertexEntry.GRAPH_ID + " = ?";
        String selectionArgs[] = {graphId};
        Cursor c = getReadableDatabase().query(TCDatabaseContract.VertexEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        List<Vertex> verticies = new ArrayList<>(c.getCount());

        while (!c.isAfterLast()) {
            Vertex vertex = new Vertex();
            vertex.setId(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.VertexEntry.ID)));
            vertex.setName(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.VertexEntry.NAME)));
            vertex.setDetails(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.VertexEntry.DETAILS)));
            vertex.setComments(getComments(vertex.getId(), TCDatabaseContract.CommentEntry.PARENT_TYPE_VERTEX));

            String images = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.VertexEntry.IMAGES));
            String res = c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.VertexEntry.RESOURCES));
            if (images != null && !images.trim().equals("")) {
                vertex.setImages(Arrays.asList(images.split(",")));
            } else {
                vertex.setImages(new ArrayList<String>());
            }
            if (res != null && !res.trim().equals("")) {
                vertex.setResources(Arrays.asList(res.split(",")));
            } else {
                vertex.setResources(new ArrayList<String>());
            }

            verticies.add(vertex);
            c.moveToNext();
        }
        return verticies;
    }

    private List<Edge> getEdges(String graphId) {
        String selection = TCDatabaseContract.EdgeEntry.GRAPH_ID + " = ?";
        String selectionArgs[] = {graphId};
        Cursor c = getReadableDatabase().query(TCDatabaseContract.EdgeEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        List<Edge> edges = new ArrayList<>(c.getCount());

        while (!c.isAfterLast()) {
            Edge edge = new Edge();
            edge.setId(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.EdgeEntry.ID)));
            edge.setLabel(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.EdgeEntry.LABEL)));
            edge.setInV(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.EdgeEntry.IN_VERTEX)));
            edge.setOutV(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.EdgeEntry.OUT_VERTEX)));
            edge.setDetails(c.getString(c.getColumnIndexOrThrow(TCDatabaseContract.EdgeEntry.DETAILS)));
            edges.add(edge);
            c.moveToNext();
        }
        return edges;
    }

    /**
     * This method takes a Graph object and pushes all information into the SQL database
     *
     * @param g
     */
    private String insertGraph(Graph g) {
        String graphId = getRandomId();
        SQLiteDatabase sql = getWritableDatabase();
        sql.beginTransaction();//Insert Graph into graph table, vertices into vertex table

        //Create ContentValues object with all columns and values for just Graph
        ContentValues graphContentValues = new ContentValues();
        graphContentValues.put(TCDatabaseContract.GraphEntry.ID, graphId);
        graphContentValues.put(TCDatabaseContract.GraphEntry.FIRST_VERTEX, g.getFirstVertex());

        //Insert Graph w/o firstVertex
        try {
            sql.insert(TCDatabaseContract.GraphEntry.TABLE_NAME, null, graphContentValues);
            Log.d(this.getClass().getName(), "Graph Info Inserted Successfully");
            //Insert all vertices
            upsertVertices(sql, g.getVertices(), graphId);
            sql.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
            sql.endTransaction();
        }
        sql.endTransaction(); //End the transaction of inserting into graph and vertices

        //Insert all edges
        sql.beginTransaction(); //Insert edges
        try {
            upsertEdges(sql, g.getEdges(), graphId);
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
    private void upsertVertices(SQLiteDatabase sql, List<Vertex> vertices, String graphID) {
        for (Vertex v : vertices) {
            upsertVertex(sql, graphID, v);
        }
        Log.d(this.getClass().getName(), "Vertices Info Inserted Successfully");
    }

    private void upsertVertex(SQLiteDatabase sql, String graphID, Vertex v) {
        ContentValues vertexContentValues = new ContentValues();
        vertexContentValues.put(TCDatabaseContract.VertexEntry.ID, v.getId());
        vertexContentValues.put(TCDatabaseContract.VertexEntry.GRAPH_ID, graphID);
        vertexContentValues.put(TCDatabaseContract.VertexEntry.NAME, v.getName());
        vertexContentValues.put(TCDatabaseContract.VertexEntry.DETAILS, v.getDetails());

        //Join all resources together as a comma-separated list
        String allRes = TextUtils.join(",", v.getResources());
        String allImgs = TextUtils.join(",", v.getImages());

        vertexContentValues.put(TCDatabaseContract.VertexEntry.RESOURCES, allRes);
        vertexContentValues.put(TCDatabaseContract.VertexEntry.IMAGES, allImgs);

        insertComments(v.getComments(), v.getId(), TCDatabaseContract.CommentEntry.PARENT_TYPE_VERTEX);

        sql.insertWithOnConflict(TCDatabaseContract.VertexEntry.TABLE_NAME, null, vertexContentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Create Content values for edges and insert
     *
     * @param sql   - SQLDatabase writer to put edges in table
     * @param edges - List of edges to insert
     */
    private void upsertEdges(SQLiteDatabase sql, List<Edge> edges, String graphID) {
        for (Edge e : edges) {
            ContentValues edgeContentValues = new ContentValues();
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.ID, e.getId());
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.GRAPH_ID, graphID);
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.LABEL, e.getLabel());
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.OUT_VERTEX, e.getOutV());
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.IN_VERTEX, e.getInV());
            edgeContentValues.put(TCDatabaseContract.EdgeEntry.DETAILS, e.getDetails());

            sql.insertWithOnConflict(TCDatabaseContract.EdgeEntry.TABLE_NAME, null, edgeContentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        Log.d(this.getClass().getName(), "Edges Info Inserted Successfully");
    }
}

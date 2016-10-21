package org.centum.techconnect.sql;

import android.provider.BaseColumns;

public class TCDatabaseContract {

    private TCDatabaseContract() {

    }

    /**
     * Chart table definitions.
     */
    public static class ChartEntry implements BaseColumns {
        public static final String TABLE_NAME = "charts";
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
                " FOREIGN KEY (graphId) REFERENCES " + GraphEntry.TABLE_NAME + " (_id)) WITHOUT ROWID;";

        private ChartEntry() {

        }

    }

    /**
     * Comment table definitions.
     */
    public static class CommentEntry implements BaseColumns {
        public static final String TABLE_NAME = "comments";
        public static final String COMMENT_ID = "_id";
        public static final String PARENT_ID = "parentId";
        public static final String PARENT_TYPE = "parentType";
        public static final String OWNER = "owner";
        public static final String TEXT = "text";
        public static final String CREATED_DATE = "createdDate";
        public static final String ATTACHMENT = "attachment";
        //Create the Comment Table
        public static final String CREATE_COMMENT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COMMENT_ID + " TEXT PRIMARY KEY NOT NULL UNIQUE," +
                PARENT_ID + " TEXT," +
                PARENT_TYPE + " TEXT," +
                OWNER + " TEXT," +
                TEXT + " TEXT," +
                CREATED_DATE + " DATE," +
                ATTACHMENT + " TEXT);";

        private CommentEntry() {
        }

    }

    /**
     * Graph table definitions.
     */
    public static class GraphEntry implements BaseColumns {
        public static final String TABLE_NAME = "graphs";
        public static final String GRAPH_ID = "_id";
        public static final String FIRST_VERTEX = "firstVertex";
        //Create the Table
        public static final String CREATE_GRAPH_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                GRAPH_ID + " TEXT PRIMARY KEY NOT NULL UNIQUE, " +
                FIRST_VERTEX + " TEXT, FOREIGN KEY (firstVertex) REFERENCES " + VertexEntry.TABLE_NAME +
                " (_id) DEFERRABLE INITIALLY DEFERRED);";

        private GraphEntry() {
        }

    }

    /**
     * Edge table definitions.
     */
    public static class EdgeEntry implements BaseColumns {
        public static final String TABLE_NAME = "edges";
        public static final String EDGE_ID = "_id";
        public static final String GRAPH_ID = "graphId";
        public static final String LABEL = "_label";
        public static final String OUT_VERTEX = "_outV";
        public static final String IN_VERTEX = "_inV";
        public static final String DETAILS = "details";
        //Create the edges table
        public static final String CREATE_EDGE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                EDGE_ID + " TEXT PRIMARY KEY NOT NULL UNIQUE," +
                GRAPH_ID + " TEXT," +
                LABEL + " TEXT," +
                OUT_VERTEX + " TEXT," +
                IN_VERTEX + " TEXT," +
                DETAILS + " TEXT, FOREIGN KEY (graphId) REFERENCES graphs (_id)," +
                " FOREIGN KEY (_outV) REFERENCES " + VertexEntry.TABLE_NAME + " (_id), FOREIGN KEY (_inV) REFERENCES " +
                VertexEntry.TABLE_NAME + " (_id));";

        private EdgeEntry() {
        }

    }

    /**
     * Vertex table definitions.
     */
    public static class VertexEntry implements BaseColumns {
        public static final String TABLE_NAME = "vertices";
        public static final String VERTEX_ID = "_id";
        public static final String GRAPH_ID = "graphId";
        public static final String NAME = "name";
        public static final String DETAILS = "details";
        public static final String RESOURCES = "resources";
        public static final String IMAGES = "images";
        //Creates the Vertices Table Object
        public static final String CREATE_VERTEX_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                VERTEX_ID + " TEXT PRIMARY KEY NOT NULL UNIQUE," +
                GRAPH_ID + " TEXT," +
                NAME + " TEXT," +
                DETAILS + " TEXT," +
                RESOURCES + " TEXT," +
                IMAGES + " TEXT, FOREIGN KEY (graphId) REFERENCES " + GraphEntry.TABLE_NAME + " (_id));";

        private VertexEntry() {
        }

    }
}

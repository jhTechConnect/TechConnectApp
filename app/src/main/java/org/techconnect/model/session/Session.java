package org.techconnect.model.session;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.techconnect.model.FlowChart;
import org.techconnect.model.GraphTraversal;
import org.techconnect.model.Vertex;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Phani on 1/26/2016.
 * <p>
 * A particular "session" is a flowchart traversal. This logs the flow
 * of the session and generates the report.
 */
public class Session implements Parcelable {

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };
    private String flowchart_id;
    private GraphTraversal traversal; //Step through the graph

    private long createdDate;
    private String department = "";
    private String modelNumber = "";
    private String serialNumber = "";
    private String notes = "";
    private boolean finished = false;

    private List<String> history = new ArrayList<>(); //list of seen vertex IDs
    private List<String> optionHistory = new ArrayList<>();//list of user responses


    public Session(FlowChart flowchart) {
        this.createdDate = new Date().getTime();
        this.flowchart_id = flowchart.getId();
        this.traversal = new GraphTraversal(flowchart.getGraph());
        history.add(this.traversal.getCurrentVertex().getId());
    }

    /**
     * Build a Session from a Parcel object
     * @param in
     */
    public Session(Parcel in) {
        this.createdDate = in.readLong();
        finished = in.readByte() != 0;
        this.department = in.readString();
        this.modelNumber = in.readString();
        this.serialNumber = in.readString();
        this.notes = in.readString();
        in.readList(this.history,String.class.getClassLoader());
        in.readList(this.optionHistory,String.class.getClassLoader());
        flowchart_id = in.readString();

        //Will wait to setup the Graph Traversal until solidly within the activity
    }

    /*
    public String getReport() {
        StringBuilder report = new StringBuilder();
        report.append("Date: ").append(new Date(createdDate).toString()).append('\n');
        report.append("Department: ").append(department).append('\n');
        report.append("Notes: ").append(notes).append('\n');
        report.append("Flowchart: ").append(flowchart.getName()).append('\n');
        //report.append("Role: " + ((role == 0) ? "Technician" : "End User"));
        report.append("History:\n------------------------").append("\n\n");
        for (int i = 0; i < history.size(); i++) {
            String question = history.get(i).getName();
            if (question.length() > 26) {
                question = question.substring(0, 23) + "...";
            }
            report.append(question).append(": ").append(optionHistory.get(i)).append("\n\n");
        }
        return report.toString();
    }
    */

    public void setTraversal(FlowChart f) {
        if (f.getId().equals(flowchart_id)) {
            traversal = new GraphTraversal(f.getGraph());
            if (history.size() > 1) {
                //Get last vertex, that's where we're at
                traversal.setCurrentVertex(history.get(history.size() -1));
            }
        } else {
            Log.e("Session Setup","Incorrect Flowchart supplied to setup traversal");
        }
    }

    //Save
    public long getCreatedDate() {
        return createdDate;
    }

    //Save
    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    //Save
    public String getDepartment() {
        return department;
    }

    //Save
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getSerialNumber() {
        return serialNumber;

    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<String> getHistory() {
        return history;
    }

    public List<String> getOptionHistory() {
        return optionHistory;
    }

    /**
     * Return the current vertex so it's fields can be used by different view.
     * This is the crucial method needed to interact with the underlying flowchart object
     *
     * @return Current vertex the traversal object is looking at
     */
    public Vertex getCurrentVertex() {
        return this.traversal.getCurrentVertex();//Simplify where this is referenced
    }

    /**
     * Need to return the current options to views so they can populate buttons and the like
     *
     * @return The keyset of the GraphTraversal object
     */
    public Set<String> getCurrentOptions() {
        return this.traversal.getOptions();
    }

    public String getFlowchart() {
        return flowchart_id;
    }


    public void selectOption(String option) {
        optionHistory.add(option);
        traversal.selectOption(option);//Select, update the traversal object
        history.add(traversal.getCurrentVertex().getId());
    }

    public void goBack() {
        //Safety check. In theory, should only be able to be called when the back button is enabled,
        //which is when the session has a previous step? May be able to remove
        if (traversal.hasPrevious()) {
            optionHistory.add("Back");
            traversal.stepBack();
            history.add(traversal.getCurrentVertex().getId());
        }
    }

    public boolean hasPrevious() {
        return traversal.hasPrevious();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //Decided to not write GraphTraversal object since this can be initialized from the
        //FlowChart graph object and the end of history if need be
        parcel.writeLong(createdDate);
        parcel.writeByte((byte) (finished ? 1 : 0));
        parcel.writeString(department);
        parcel.writeString(modelNumber);
        parcel.writeString(serialNumber);
        parcel.writeString(notes);
        parcel.writeList(history);
        parcel.writeList(optionHistory);
        parcel.writeString(flowchart_id);//Just need the flowchart, not traversal

    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

}

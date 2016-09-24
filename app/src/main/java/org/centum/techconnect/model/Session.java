package org.centum.techconnect.model;

import com.java.model.FlowChart;
import com.java.model.GraphTraversal;
import com.java.model.Vertex;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Phani on 1/26/2016.
 *
 * A particular "session" is a flowchart traversal. This logs the flow
 * of the session and generates the report.
 */
public class Session {

    private long createdDate;
    private String department;
    private FlowChart device;
    private GraphTraversal flowchart; //Step through the graph
    private String notes;
    
    private List<Flowchart_old> history = new LinkedList<>();//Wiating until we decide what to do with this
    private List<String> optionHistory = new LinkedList<>();//Waiting until we decide what to do with this

    public String getReport() {
        StringBuilder report = new StringBuilder();
        report.append("Date: ").append(new Date(createdDate).toString()).append('\n');
        report.append("Department: ").append(department).append('\n');
        report.append("Notes: ").append(notes).append('\n');
        report.append("Device: ").append(device.getName()).append('\n');
        //report.append("Role: " + ((role == 0) ? "Technician" : "End User"));
        report.append("History:\n------------------------").append("\n\n");
        for(int i = 0; i < history.size(); i++){
            String question = history.get(i).getQuestion();
            if(question.length() > 26){
                question = question.substring(0, 23)+"...";
            }
            report.append(question).append(": ").append(optionHistory.get(i)).append("\n\n");
        }
        return report.toString();
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


    /**
     * Return the current vertex so it's fields can be used by different view.
     * This is the crucial method needed to interact with the underlying flowchart object
     * @return Current vertex the traversal object is looking at
     */
    public Vertex getCurrentVertex() {
        return this.flowchart.getCurrentVertex();//Simplify where this is referenced
    }

    public FlowChart getDevice() { return device;}

    public void setDevice(FlowChart device) {
        this.device = device;
        this.flowchart = new GraphTraversal(device.getGraph());

    }

    //save
    public String getNotes() {
        return notes;
    }
    //Save
    public void setNotes(String notes) {
        this.notes = notes;
    }

    //Modify
    public void selectOption(String option){
        flowchart.selectOption(option);//Select, update the traversal object
    }

    //modify
    public void goBack() {
        //Safety check. In theory, should only be able to be called when the back button is enabled,
        //which is when the session has a previous step? May be able to remove
        if (flowchart.hasPrevious()) {
            flowchart.stepBack();
        }
    }

    public boolean hasPrevious() {
        return flowchart.hasPrevious();
    }


    public enum Urgency {
        Low, Medium, High, Critical
    }
}

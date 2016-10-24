package org.techconnect.model.session;

import org.techconnect.model.FlowChart;
import org.techconnect.model.GraphTraversal;
import org.techconnect.model.Vertex;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Phani on 1/26/2016.
 * <p>
 * A particular "session" is a flowchart traversal. This logs the flow
 * of the session and generates the report.
 */
public class Session {

    private FlowChart flowchart;
    private GraphTraversal traversal; //Step through the graph

    private long createdDate;
    private String department;
    private String modelNumber;
    private String serialNumber;
    private String notes;

    private List<Vertex> history = new LinkedList<>();//Wiating until we decide what to do with this
    private List<String> optionHistory = new LinkedList<>();//Waiting until we decide what to do with this

    public Session(FlowChart flowchart) {
        this.flowchart = flowchart;
        this.traversal = new GraphTraversal(flowchart.getGraph());
    }

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

    public FlowChart getFlowchart() {
        return flowchart;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void selectOption(String option) {
        traversal.selectOption(option);//Select, update the traversal object
    }

    public void goBack() {
        //Safety check. In theory, should only be able to be called when the back button is enabled,
        //which is when the session has a previous step? May be able to remove
        if (traversal.hasPrevious()) {
            traversal.stepBack();
        }
    }

    public boolean hasPrevious() {
        return traversal.hasPrevious();
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }


    public enum Urgency {
        Low, Medium, High, Critical
    }
}

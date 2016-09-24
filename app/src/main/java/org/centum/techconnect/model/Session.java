package org.centum.techconnect.model;

import com.java.model.FlowChart;
import com.java.model.Graph;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

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
    private Graph flowchart;
    //private Device device;
    //private DeviceRole role;
    private String notes;
    //private Flowchart_old currentFlowchart;
    //Stack of previous flowcharts shown
    private Stack<Flowchart_old> stack = new Stack<>();
    private List<Flowchart_old> history = new LinkedList<>();
    private List<String> optionHistory = new LinkedList<>();

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

    public FlowChart getDevice() { return device;}

    public void setDevice(FlowChart device) {
        this.device = device;
        this.flowchart = device.getGraph();

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
        advanceTo(getCurrentFlowchart().getChild(option));
        optionHistory.add(option);
    }
    //modify
    private void advanceTo(Flowchart_old newFlowchart) {
        stack.push(this.currentFlowchart);
        this.currentFlowchart = newFlowchart;
        history.add(newFlowchart);
    }
    //modify
    public void goBack() {
        if (hasPrevious()) {
            this.currentFlowchart = stack.pop();
            history.add(this.currentFlowchart);
            optionHistory.add("*Back*");
        }
    }

    public boolean hasPrevious() {
        return stack.size() > 0;
    }
    //Delete
    public void setRole(DeviceRole role) {
        this.role = role;
        this.currentFlowchart = role.getFlowchart();
    }

    public enum Urgency {
        Low, Medium, High, Critical
    }
}

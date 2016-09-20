package org.centum.techconnect.model;

/**
 * Created by Phani on 1/23/2016.
 *
 * A particular role references a particular flowchart.
 * Really just a wrapper, but can be extended.
 */
public class DeviceRole {

    private String jsonFile;
    private Flowchart_old flowchart;


    public DeviceRole(String jsonFile) {
        this.jsonFile = jsonFile;
    }

    public Flowchart_old getFlowchart() {
        return flowchart;
    }

    public void setFlowchart(Flowchart_old flowchart) {
        this.flowchart = flowchart;
    }

    public String getJsonFile() {
        return jsonFile;
    }

    public void setJsonFile(String jsonFile) {
        this.jsonFile = jsonFile;
    }
}

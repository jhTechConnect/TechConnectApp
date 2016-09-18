package org.centum.techconnect.model;

import java.util.ArrayList;
import java.util.List;

public class Catalog {

	private List<FlowChart_tempo> flowcharts = new ArrayList<FlowChart_tempo>();
	
	/**
	* 
	* @return
	* The flowcharts
	*/
	public List<FlowChart_tempo> getFlowCharts() {
	return flowcharts;
	}
	
	/**
	* 
	* @param flowcharts
	* The flowcharts
	*/
	public void setFlowcharts(List<FlowChart_tempo> flowcharts) {
	this.flowcharts = flowcharts;
}

}


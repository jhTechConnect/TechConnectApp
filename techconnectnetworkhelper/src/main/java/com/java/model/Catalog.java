package com.java.model;

import java.util.ArrayList;
import java.util.List;

public class Catalog {

	private List<FlowChart> flowcharts = new ArrayList<FlowChart>();
	
	/**
	* 
	* @return
	* The flowcharts
	*/
	public List<FlowChart> getFlowCharts() {
	return flowcharts;
	}
	
	/**
	* 
	* @param flowcharts
	* The flowcharts
	*/
	public void setFlowcharts(List<FlowChart> flowcharts) {
	this.flowcharts = flowcharts;
}

}


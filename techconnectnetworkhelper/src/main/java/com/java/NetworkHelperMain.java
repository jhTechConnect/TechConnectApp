package com.java;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import main.java.model.Edge;
import main.java.model.FlowChart;
import main.java.model.Graph;
import main.java.model.Vertex;

public class NetworkHelperMain {
	//Need to use this guy to make major operations
	public static Gson gson = new Gson();

	public static void main(String[] args) throws IOException {
		TechConnectNetworkHelper dude = new TechConnectNetworkHelper();
		
		//TinkerGraph real_graph = new TinkerGraph();
		//GraphSONReader reader = new GraphSONReader(real_graph);
		
		//Try out logging in to the site
		

		try {
			dude.login("dwalste1@jhu.edu","dwalsten" );
			System.out.println("Success");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		
		//Test out the capabilities of Retrofit to read the catalog - FUNCTIONAL!
		/*
		List<FlowChart> flowcharts = dude.getCatalog();
		System.out.println(flowcharts.get(0).getId());
		System.out.println(flowcharts.get(0).getDescription());
		*/
		
		//Test the simple flowchart get method - FUNCTIONAL!
		
		FlowChart important = dude.getChart("testchart99999999");
		System.out.println(important.getName());
		System.out.println(important.getDescription());
		System.out.println(important.getType());
		//Convert TCGraph to the TinkerGraph
		//convertToGraphSON(reader,important.getGraph());
		Graph real_graph;
		real_graph = important.getGraph();
		
		
		for(Vertex v : real_graph.getVertices()) {
			//Use the GraphSONUtility to convert and add each vertex the graph?
			System.out.println(v.getId());
			System.out.println(v.getName());
			for (String e : v.getInEdges()) {
				System.out.println(real_graph.getEdge(e).getLabel());
			}
			
		}
		//
		
		/*Test out the capability of getting multiple flowcharts
		ArrayList<String> list_charts = new ArrayList<String>();
		list_charts.add("testchart99999999");
		String[] list = {"testchart99999999"};
		List<FlowChart> charts = dude.getCharts(list);
		System.out.println(charts.get(0).getName());
		System.out.println(charts.get(0).getDescription());
		System.out.println(charts.get(0).getAllRes());
		*/
		
		//Then logout
		try {
			dude.logout();
			System.out.println("SUCCESS"); 
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		

	}

}

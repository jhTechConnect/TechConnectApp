package com.java.model;

import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * This class allows me to have a structured approach to traversing
 * a flowchart graph object during the self help fragment. Can modify as
 * we see fit to extract data
 * Created by doranwalsten on 9/24/16.
 */
public class GraphTraversal {

    private Graph g; //This is the graph that we are traversing
    private Map<String,String> currOptions;//Map of the current option, next vertex pairs for the current v
    private String curr;//Id of the current vertex we're working with
    private boolean done; //Store whether the current vertex corresponds to the end of a flowchart
    private Stack<String> stack = new Stack<>();//Use this to track a current traversal of the graph

    public GraphTraversal(Graph g) {
        this.g = g;
        this.curr = g.getFirstVertex();
        this.currOptions = g.getOptions(g.getFirstVertex());
        this.done = false;
    }

    /**
     * When we select an option to go to the next vertex in a flowchart, traverse to that vertex
     * in the graph.
     * @param opt - The string option which corresponds to the next vertex in the graph to visit
     */
    public void selectOption(String opt) {
        //Push the past vertex to the stack
        stack.push(this.curr);
        //Update the current Vertex
        this.curr = this.currOptions.get(opt);
        //Update the new options available
        this.currOptions.clear();
        this.currOptions = this.g.getOptions(this.curr);
        //Check to
    }


    /**
     * If we need to go back to the previous vertex seen
     */
    public void stepBack() {
        //Get the ID of the Vertex to visit
        String prev = stack.pop();
        //Update the ID of the current Vertex
        this.curr = prev;
        //Update the options now available
        this.currOptions.clear();
        this.currOptions = this.g.getOptions(this.curr);

    }

    /**
     * Used to determine if there have been previous steps in the traversal of the graph
     * @return If the stack is not empty, there have been previous steps
     */
    public boolean hasPrevious() {
        return !stack.isEmpty();
    }

    /**
     * Use this method to reset the stack so a new traversal can begin
     */
    public void resetTraversal() {
        this.stack.clear();
    }

    //Figure out if we need to modify this structure to not be a set. I think we can still iterate
    //over it easily
    public Set<String> getOptions() {
        return this.currOptions.keySet();
    }

    /**
     * Use the currVertex string to return the current Vertex object if we need to access fields
     * This is because most operations are on the actual object itself intead of the ID
     * We store the ID in the traversal to save space (?)
     * @return The corresponding vertex object
     */
    public Vertex getCurrentVertex() {
        return this.g.getVertex(this.curr);
    }

}

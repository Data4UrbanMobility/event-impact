package de.l3s.event_impact.spatial;

import de.l3s.event_impact.Event;
import de.l3s.event_impact.road_network.TraversalEdge;

import java.util.Date;
import java.util.Set;

public class SpatialImpactResult {
    private Event event;
    private Date time;
    private double impact;
    private Set<Integer> affectedGraph;
    private TraversalEdge mostDistantEdge;

    public SpatialImpactResult(Event event, Date time, double impact, Set<Integer> affectedGraph, TraversalEdge mostDistantEdge) {
        this.event = event;
        this.time = time;
        this.impact = impact;
        this.affectedGraph = affectedGraph;
        this.mostDistantEdge = mostDistantEdge;
    }

    public Event getEvent() {
        return event;
    }

    public Date getTime() {
        return time;
    }

    public double getImpact() {
        return impact;
    }

    public Set<Integer> getAffectedGraph() {
        return affectedGraph;
    }

    public TraversalEdge getMostDistantEdge() {
        return mostDistantEdge;
    }
}

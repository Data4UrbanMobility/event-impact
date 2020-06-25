package de.l3s.event_impact.road_network;

;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TraversalEdge {
    private int id;
    private TraversalEdge predecessor;
    private boolean written;
    private List<TraversalEdge> additionalPaths;
    private boolean emptyEdge=false;

    public TraversalEdge() {
        emptyEdge=true;
    }

    public TraversalEdge(Street s, TraversalEdge predecessor) {
        this.id = s.getId();
        this.predecessor = predecessor;
        this.written=false;
        this.additionalPaths=new ArrayList<>();
    }


    public TraversalEdge(Street s) {
        this.id = s.getId();
        this.predecessor=null;
        this.written=false;
        this.additionalPaths=new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public boolean isWritten() {
        return written;
    }

    public void addAdditional(TraversalEdge te) {
        additionalPaths.add(te);
    }

    public void write(Set<Integer> graph) {
        if (written) return;
        graph.add(id);
        written=true;
        for (TraversalEdge te: additionalPaths) {
            te.write(graph);
        }
        if (predecessor != null) predecessor.write(graph);
    }



    public List<Integer> getPath() {
        if (emptyEdge) {
            return new ArrayList<>();
        }

        List<Integer> path = new ArrayList<>();
        getPathRecursion(path);
        return path;

    }

    public TraversalEdge getPredecessor() {
        return predecessor;
    }

    public void getPathRecursion(List<Integer> path) {
        path.add(id);
        if (predecessor!=null) {
            predecessor.getPathRecursion(path);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TraversalEdge that = (TraversalEdge) o;
        return id == that.id &&
                Objects.equals(predecessor, that.predecessor);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, predecessor);
    }
}

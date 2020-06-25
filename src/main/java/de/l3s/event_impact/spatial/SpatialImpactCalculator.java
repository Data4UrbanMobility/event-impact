package de.l3s.event_impact.spatial;

import de.l3s.event_impact.Event;
import de.l3s.event_impact.AbstractImpactCalculator;
import de.l3s.event_impact.road_network.Street;
import de.l3s.event_impact.road_network.Streetgraph;
import de.l3s.event_impact.road_network.TrafficSnapshot;
import de.l3s.event_impact.road_network.TraversalEdge;
import de.l3s.event_impact.util.configuration.Configuration;
import de.l3s.event_impact.util.configuration.ConfigurationParser;

import java.util.*;

public class SpatialImpactCalculator extends AbstractImpactCalculator {

    private double th_affected;
    private Streetgraph streetGraph;
    private List<SpatialImpactResult> results;

    public static void addConfigurationEntries(ConfigurationParser cp) {
    }
    public SpatialImpactCalculator(Configuration config) {
        super(config);
        th_affected = config.getDoubleOption("th_affected");

        streetGraph = new Streetgraph(config);
        results = Collections.synchronizedList(new ArrayList<SpatialImpactResult>());
    }

    public List<SpatialImpactResult> getResults() {
        return results;
    }

    @Override
    protected void processEvent(Event e) {
        List<Date> targetTimePoints = getRelevantTimes(e);
        targetTimePoints.stream().forEach(t -> {
            determineSpatialImpact(e,t);
        });

    }

    private void determineSpatialImpact(Event e, Date time) {
        TrafficSnapshot snap = new TrafficSnapshot(config, time);
        List<Integer> seeds= streetGraph.getSeedStreets(e);


        List<TraversalEdge> open = new LinkedList<>();
        Map<Integer, Set<String>> openPaths = new HashMap<>();

        seeds.forEach((s) -> {
            Street st = streetGraph.getStreet(s);
            TraversalEdge te = new TraversalEdge(st);
            Set<String> op = new HashSet<>();
            op.add(st.getSource()+";"+st.getTarget());
            openPaths.put(te.getId(), op);
            open.add(te);
        });

        Set<Integer> eventGraph = new HashSet<>();
        double maxDistance=0;
        TraversalEdge maxEdge=null;
        Map<Integer, TraversalEdge> visited = new HashMap<>();

        while (open.size() >0) {
            TraversalEdge current = open.remove(0);
            int currentId = current.getId();
            Set<String> path = openPaths.get(currentId);

            visited.put(currentId, current);

            boolean isEnd = snap.isIQROutlier(currentId);

            if (isEnd) {
                current.write(eventGraph);
                double dist= streetGraph.getDistance(currentId, e.getPosition());
                if (dist > maxDistance) {
                    maxDistance=dist;
                    maxEdge=current;
                }
            }


            Set<Street> neigh = streetGraph.getNeighbours(streetGraph.getSource(currentId), streetGraph.getTarget(currentId));
            List<Street> continuations = new ArrayList<>();
            neigh.forEach((n) -> {
                boolean continuationCond = (snap.isAffected(n.getId()) || snap.isIQROutlier(n.getId()));
                boolean directionCond = !path.contains(n.getTarget() + ";" + n.getSource());
                boolean visitedCond = !visited.keySet().contains(n.getId());
                boolean openCond =  !openPaths.keySet().contains(n.getId());

                boolean predCond = (current.getPredecessor() == null) ||(current.getPredecessor().getId() != n.getId());


                if (((!visitedCond) || (!openCond)) && predCond ) {
                    TraversalEdge te=null;

                    if (!visitedCond) {
                        te = visited.get(n.getId());
                    } else if (!openCond) {
                        for (TraversalEdge openTe: open) {
                            if (openTe.getId()==n.getId()) {
                                te=openTe;
                                break;

                            }
                        }
                    }
                    if (te.isWritten()) {
                        current.write(eventGraph);
                    } else {
                        te.addAdditional(current);
                    }

                }

                if (continuationCond && directionCond && visitedCond && openCond) continuations.add(n);
            });

            if (continuations.size() == 1) {
                TraversalEdge te = new TraversalEdge(continuations.get(0), current);
                openPaths.put(te.getId(), path);
                open.add(0, te);
            } else if (continuations.size() > 1) {
                for (Street s : continuations) {
                    Set<String> newPath = new HashSet<>(path);
                    newPath.add(s.getSource() + ";" + s.getTarget());
                    TraversalEdge te = new TraversalEdge(s, current);
                    openPaths.put(te.getId(), newPath);
                    open.add(0, te);
                }
            }
        }

        results.add(new SpatialImpactResult(e, time, maxDistance, eventGraph, maxEdge));
    }


}

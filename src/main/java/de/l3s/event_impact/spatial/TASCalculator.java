package de.l3s.event_impact.spatial;

import de.l3s.event_impact.util.configuration.Configurable;
import de.l3s.event_impact.util.configuration.Configuration;
import de.l3s.event_impact.util.configuration.ConfigurationParser;

import java.util.*;

/**
 * Class that determines the typically affected subgraph (TAS)
 */
public class TASCalculator extends Configurable {


    private List<SpatialImpactResult> spatialImpactResultList;
    private Map<String, Set<Integer>> typicallyAffectedSubgraphs;
    private double th_ta;

    public static void addConfigurationEntries(ConfigurationParser cp) {
        cp.addDoubleOption("th_ta", "th_typicallyAffected", "Threshold for edges of the typically affected subgraphs.");
    }

    public TASCalculator(Configuration config, List<SpatialImpactResult> spatialImpactResultList) {
        super(config);
        this.spatialImpactResultList = spatialImpactResultList;
        this.typicallyAffectedSubgraphs = new HashMap<>();
        this.th_ta = config.getDoubleOption("th_typicallyAffected");
    }


    public void run() {
        //group by event venue
        Map<String, List<SpatialImpactResult>> venueToResult = new HashMap<>();
        spatialImpactResultList.stream().forEach(r -> {
            String venue = r.getEvent().getVenue();

            if (! venueToResult.containsKey(venue)) {
                venueToResult.put(venue, new ArrayList<>());
            }
            venueToResult.get(venue).add(r);
        });

        //calculate intersections
        venueToResult.entrySet().stream().forEach(entry -> {
            List<SpatialImpactResult> results = entry.getValue();

            Map<Integer, Integer> edgeCounts = new HashMap<>();
            Set<Integer> allEdges = new HashSet<>();

            results.stream().forEach(r -> {
                r.getAffectedGraph().stream().forEach(edge -> {
                    if (!edgeCounts.containsKey(edge)) {
                        edgeCounts.put(edge, 0);
                    }
                    edgeCounts.put(edge, edgeCounts.get(edge)+1);
                });
                allEdges.addAll(r.getAffectedGraph());
            });

            Set<Integer> typicallyAffectedGraph = new HashSet<>();
            edgeCounts.entrySet().stream().forEach(ec -> {
                int edge = ec.getKey();
                int count = ec.getValue();
                double score = ((double) count) / ((double) allEdges.size());

                if (score >= th_ta) {
                    typicallyAffectedGraph.add(edge);
                }
            });
            typicallyAffectedSubgraphs.put(entry.getKey(), typicallyAffectedGraph);
        });
    }

    public Map<String, Set<Integer>> getTypicallyAffectedSubgraphs() {
        return typicallyAffectedSubgraphs;
    }
}

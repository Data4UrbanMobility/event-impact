package de.l3s.event_impact.temporal;

import de.l3s.event_impact.Event;
import de.l3s.event_impact.AbstractImpactCalculator;
import de.l3s.event_impact.util.configuration.Configuration;
import de.l3s.event_impact.util.configuration.ConfigurationParser;

import java.util.Map;
import java.util.Set;

public class TemporalImpactCalculator extends AbstractImpactCalculator {

    private Map<String, Set<Integer>> typicallyAffectedSubgraphs;


    public TemporalImpactCalculator(Configuration config, Map<String, Set<Integer>> typicallyAffectedSubgraphs) {
        super(config);
        this.typicallyAffectedSubgraphs = typicallyAffectedSubgraphs;
    }

    public static void addConfigurationEntries(ConfigurationParser cp) {
    }

    @Override
    protected void processEvent(Event e) {

    }
}

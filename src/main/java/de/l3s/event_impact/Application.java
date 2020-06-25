package de.l3s.event_impact;

import de.l3s.event_impact.road_network.Streetgraph;
import de.l3s.event_impact.road_network.TrafficSnapshot;
import de.l3s.event_impact.spatial.TASCalculator;
import de.l3s.event_impact.spatial.SpatialImpactCalculator;
import de.l3s.event_impact.spatial.SpatialImpactResult;
import de.l3s.event_impact.temporal.TemporalImpactCalculator;
import de.l3s.event_impact.util.configuration.Configurable;
import de.l3s.event_impact.util.configuration.Configuration;
import de.l3s.event_impact.util.configuration.ConfigurationParser;
import de.l3s.event_impact.util.db.PostgreDB;

import java.util.List;

public class Application extends Configurable {

    public Application(Configuration config) {
        super(config);
    }

    public static void addConfigurationEntries(ConfigurationParser cp) {
        cp.addBooleanOption("si", "spatialImpact", "Activate or deactivate the spatial impact calculation");
        cp.addBooleanOption("ti", "temporalImpact", "Activate or deactivate the temporal impact calculation");
        cp.addBooleanOption("tas", "typicallyAffectedSubgraph", "Activate or deactivate the typically affected subgraph calculation");
        PostgreDB.addConfigEntries(cp);
        AbstractImpactCalculator.addConfigurationEntries(cp);
        SpatialImpactCalculator.addConfigurationEntries(cp);
        TASCalculator.addConfigurationEntries(cp);
        TemporalImpactCalculator.addConfigurationEntries(cp);
        Streetgraph.addConfigurationEntries(cp);
        TrafficSnapshot.addConfigurationEntries(cp);
    }

    private void run() {

        if (config.getBooleanOption("spatialImpact", false)
                || config.getBooleanOption("typicallyAffectedSubgraph", false)
                || config.getBooleanOption("temporalImpact", false)) {

            SpatialImpactCalculator spatialImpactCalculator = new SpatialImpactCalculator(config);
            spatialImpactCalculator.run();
            List<SpatialImpactResult> spatialImpactResults = spatialImpactCalculator.getResults();

            if (config.getBooleanOption("typicallyAffectedSubgraph", false)
                    || config.getBooleanOption("temporalImpact", false)) {

                TASCalculator TASCalculator = new TASCalculator(config, spatialImpactResults);
                TASCalculator.run();

                if (config.getBooleanOption("temporalImpact", false)) {
                    TemporalImpactCalculator temporalImpactCalculator = new TemporalImpactCalculator(config,
                            TASCalculator.getTypicallyAffectedSubgraphs());
                    temporalImpactCalculator.run();
                }
            }
        }
    }

    public static void main(String[] args) {
        ConfigurationParser confPars = new ConfigurationParser();
        addConfigurationEntries(confPars);
        List<Configuration> configs = confPars.getConfigs();
        for (Configuration c: configs) {
            Application app = new Application(c);
            app.run();
        }
    }
}

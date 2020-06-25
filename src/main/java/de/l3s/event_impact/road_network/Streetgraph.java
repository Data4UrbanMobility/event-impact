package de.l3s.event_impact.road_network;


import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.coords.LatLonPoint;
import de.l3s.event_impact.Event;
import de.l3s.event_impact.util.configuration.Configurable;
import de.l3s.event_impact.util.configuration.Configuration;
import de.l3s.event_impact.util.configuration.ConfigurationParser;
import de.l3s.event_impact.util.db.PostgreDB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Streetgraph extends Configurable {

    //source -> target
    private Map<Integer, Set<Street>> targets;
    //target -> source
    private Map<Integer, Set<Street>> sources;
    //id -> street
    private Map<Integer, Street> streets;

    private PostgreDB db;

    //configuration entries
    String streetGraphTable, sourceColumn, targetColumn, idColumn, geometryColumn;

    public static void addConfigurationEntries(ConfigurationParser cp) {
        cp.addStringOption("sg", "streetGraphTable", "Table of pgrouting graph");
        cp.addStringOption("sc", "sourceColumn", "Column of source ids in streetGraphTable");
        cp.addStringOption("tc", "tagetColumn", "Column of target ids in streetGraphTable");
        cp.addStringOption("ic", "idColumn", "Column of street ids in streetGraphTable");
        cp.addStringOption("gc", "geometryColumn", "Column of geometry in streetGraphTable");
    }

    public Streetgraph(Configuration config)  {
        super(config);
        db = new PostgreDB(config);

        targets = new HashMap<>();
        sources = new HashMap<>();
        streets = new HashMap<>();

        streetGraphTable = config.getStringOption("streetGraphTable");
        sourceColumn = config.getStringOption("sourceColumn");
        targetColumn = config.getStringOption("targetColumn");
        idColumn = config.getStringOption("idColumn");
        geometryColumn = config.getStringOption("geometryColumn");


        fetchStreetGraph();
    }

    public void fetchStreetGraph() {
        String graphSelectQuery = "SELECT "+sourceColumn+","+targetColumn+", "+idColumn+", ST_ASTEXT("+geometryColumn+") FROM "+streetGraphTable+";";

        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(graphSelectQuery)) {
            while (rs.next()) {
                Integer source = rs.getInt(1);
                Integer target = rs.getInt(2);
                Integer vid = rs.getInt(3);

                Street s = new Street(vid, source, target, rs.getString(4));

                if (!targets.containsKey(source)) {
                    targets.put(source, new HashSet<>());
                }
                targets.get(source).add(s);

                if (!sources.containsKey(target)) {
                    sources.put(target, new HashSet<>());
                }
                sources.get(target).add(s);
                streets.put(vid, s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    public double getDistance(int streetId, LatLonPoint p) {
        List<LatLonPoint> points = streets.get(streetId).getPoints();

        double currentDist = 0;
        for (LatLonPoint sp: points) {
            double radDist = p.distance(sp);
            double kmDist = Length.KM.fromRadians(radDist);

            currentDist = Math.max(currentDist, kmDist);
        }
        return currentDist;
    }

    public int getSource(int id) {
        return streets.get(id).getSource();
    }

    public int getTarget(int id) {
        return streets.get(id).getTarget();
    }

    public Street getStreet(int id) {
        return streets.get(id);
    }


    public Set<Street> getNeighbours(int source, int target) {
        Set<Street> result;
        if (!targets.containsKey(target)) {
            result = new HashSet<>();
        } else {
            result =  new HashSet<>(targets.get(target));
        }

        if (sources.containsKey(source)) {
            result.addAll(sources.get(source));
        }
        return result;
    }


    public List<Integer> getSeedStreets(Event e)  {
        //todo get proper string from event
        String eventLocation = "";
        String streetQuery = "SELECT "+idColumn+" FROM "+streetGraphTable+" " +
                            "WHERE (public.ST_Distance_Sphere(public.ST_GeomFromText('"+eventLocation+"::text', 4326), "+geometryColumn+")<=500)";

        List<Integer> result = new ArrayList<>();

        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(streetQuery)) {
            while(rs.next()) {
                result.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(2);
        }
        return result;
    }

    public Collection<Street> getAllStreets() {
        return streets.values();
    }

}

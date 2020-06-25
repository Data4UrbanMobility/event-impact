package de.l3s.event_impact.road_network;

import de.l3s.event_impact.util.configuration.Configurable;
import de.l3s.event_impact.util.configuration.Configuration;
import de.l3s.event_impact.util.configuration.ConfigurationParser;
import de.l3s.event_impact.util.db.PostgreDB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

public class TrafficSnapshot extends Configurable {

    private Map<Integer, TrafficRecord> data;
    private double th_affected;

    private String trafficTable, lossColumn, outlierColumn, idColumn, timeColumn;

    public static void addConfigurationEntries(ConfigurationParser cp) {
        cp.addStringOption("tt", "trafficTable", "Table that holds traffic information");
        cp.addStringOption("lc", "lossColumn", "Traffic speed loss in trafficTable");
        cp.addStringOption("oc", "outlierColumn", "Outlier flag column in trafficTable");
        cp.addStringOption("tc", "timeColumn", "Time column in trafficTable");
        cp.addDoubleOption("tha", "th_affected", "Threshold when a unit is considered to be affected");
    }

    public TrafficSnapshot(Configuration config, Date t)  {
        super(config);
        data = new HashMap<>();
        th_affected = config.getDoubleOption("th_affected");
        trafficTable = config.getStringOption("trafficTable");
        lossColumn = config.getStringOption("lossColumn");
        outlierColumn = config.getStringOption("outlierColumn");
        idColumn = config.getStringOption("idColumn");
        timeColumn = config.getStringOption("timeColumn");

        PostgreDB db = new PostgreDB(config);
        fetchSnapshot(db, t);
    }

    private void fetchSnapshot(PostgreDB db, Date t) {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String query= "SELECT "+idColumn+", "+lossColumn+","+outlierColumn+" from "+trafficTable+" " +
                " where "+timeColumn+"='"+formatter.format(t)+"';";

        try(Connection con = db.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt(1);
                data.put(id, new TrafficRecord(rs.getDouble(2), rs.getBoolean(6)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    public boolean isAffected(int id) {
        if (!data.containsKey(id)) return false;
        return (data.get(id).unitLoad >= th_affected);

    }

    public boolean isIQROutlier(int id) {
        if (!data.containsKey(id)) return false;
        return data.get(id).iqrOut;
    }

    private class TrafficRecord {
        public double unitLoad;
        public boolean iqrOut;

        public TrafficRecord(double maxloss, boolean iqrOut) {
            this.unitLoad =maxloss;
            this.iqrOut=iqrOut;
        }
    }
}



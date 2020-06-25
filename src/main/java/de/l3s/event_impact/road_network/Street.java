package de.l3s.event_impact.road_network;

import com.bbn.openmap.proj.coords.LatLonPoint;

import java.util.ArrayList;
import java.util.List;

public class Street {
    private int id, source, target;
    private List<LatLonPoint> points;


    public Street(int id, int source, int target, String geom) {
        this.id = id;
        this.source = source;
        this.target = target;

        geom = geom.substring(11)
                .substring(0, geom.length()-11-1);

        this.points = new ArrayList<>();
        String[] pointsArray = geom.split(",");
        for (String p: pointsArray) {
            String[] xy = p.split(" ");
            this.points.add(new LatLonPoint.Double(Double.parseDouble(xy[0]),
                    Double.parseDouble(xy[1])));
        }
    }

    public List<LatLonPoint> getPoints() {
        return points;
    }

    public int getId() {
        return id;
    }

    public int getSource() {
        return source;
    }

    public int getTarget() {
        return target;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Street.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Street other = (Street) obj;
        return this.id == other.id;
    }


}
package de.l3s.event_impact;

import com.bbn.openmap.proj.coords.LatLonPoint;

import java.util.Date;

public class Event {
    private Date startTime;
    private String type;
    private String venue;
    private LatLonPoint position;

    public Date getStartTime() {
        return startTime;
    }

    public String getType() {
        return type;
    }

    public LatLonPoint getPosition() {
        return position;
    }

    public String getVenue() {
        return venue;
    }
}

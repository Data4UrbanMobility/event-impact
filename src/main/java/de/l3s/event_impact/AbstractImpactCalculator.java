package de.l3s.event_impact;

import de.l3s.event_impact.util.configuration.Configurable;
import de.l3s.event_impact.util.configuration.Configuration;
import de.l3s.event_impact.util.configuration.ConfigurationParser;
import de.l3s.event_impact.util.db.PostgreDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class AbstractImpactCalculator extends Configurable {

    protected PostgreDB db;

    public AbstractImpactCalculator(Configuration config) {
        super(config);
        db = new PostgreDB(config);
    }

    public static void addConfigurationEntries(ConfigurationParser cp) {
    }

    private List<Event> getTargetEvents() {
        //todo
        return null;
    }

    public void run() {
        List<Event> targetEvents = getTargetEvents();
        targetEvents.stream().parallel().forEach(e -> {
            processEvent(e);
        });
    }

    protected abstract void processEvent(Event e);

    protected List<Date> getRelevantTimes(Event e) {
        Date startTime = e.getStartTime();

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startTime);

        //round start time to 15 minute granularity
        int min = startCal.get(Calendar.MINUTE);
        int diff = min % 15;
        if (diff != 0) {
            if (diff > 7) {
                min +=(15-diff);
            } else {
                min -=diff;
            }
            startCal.set(Calendar.MINUTE, min);
            startTime = startCal.getTime();
        }


        int startOffset = 4 * 4;
        int endOffset = 4 * 5;

        List<Date> result = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        for (int i = 0; i < startOffset; ++i) {
            //cal.add(Calendar.MINUTE, -15);
            cal.add(Calendar.MINUTE, -15);
            result.add(cal.getTime());
        }

        result.add(startTime);
        cal = Calendar.getInstance();
        cal.setTime(startTime);
        for (int i = 0; i < endOffset; ++i) {
            cal.add(Calendar.MINUTE, 15);
            result.add(cal.getTime());
        }
        return result;
    }
}

package at.htl.mirrorhome.user.calendar;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CalendarFetcher {
    private static Logger log = LoggerFactory.getLogger(CalendarFetcher.class);

    public static List<Appointment> fetch(List<CalendarSource> calendarSource){
        log.info("Starting to fetch Calendar");
        LinkedList<Appointment> appointments = new LinkedList<>();


        calendarSource.forEach(cal -> {
            log.info(cal.getIcalUrl());
            try{
                URL u = new URL(cal.getIcalUrl());
                CalendarBuilder builder = new CalendarBuilder();
                Calendar c4jCalendar = builder.build(u.openStream());

                log.info("calendar size: " + c4jCalendar.getComponents().size());
                for (Iterator i = c4jCalendar.getComponents().iterator(); i.hasNext();) {
                    Component component = (Component) i.next();
                    Appointment a = new Appointment();
                    for (Iterator j = component.getProperties().iterator(); j.hasNext();) {
                        Property property = (Property) j.next();

                        switch(property.getName()){
                            case "DTSTART":
                                a.setDateStart(property.getValue());
                                break;
                            case "DTEND":
                                a.setDateEnd(property.getValue());
                                break;
                            case "LOCATION":
                                a.setLocation(property.getValue());
                                break;
                            case "DESCRIPTION":
                                a.setDescription(property.getValue());
                                break;
                            case "SUMMARY":
                                a.setSummary(property.getValue());
                                break;
                        }
                    }
                    appointments.add(a);
                }
            }
            catch(Exception e){
                log.error(e.getMessage());
                e.printStackTrace();
            }

        });
        return appointments.stream().filter(a -> isTodaysDate(a.getDateStart())).collect(Collectors.toList());
    }

    private static boolean isTodaysDate(Date d){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("DD.MM.YYYY");

        return format.format(d).equals(format.format(today));
    }
}

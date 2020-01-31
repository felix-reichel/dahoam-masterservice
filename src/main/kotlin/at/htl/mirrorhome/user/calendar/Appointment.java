package at.htl.mirrorhome.user.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Appointment {
    private String summary;
    private String location;
    private String description;
    private Date dateStart;
    private Date dateEnd;

    private static SimpleDateFormat dateWithTimeFormatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
    private static SimpleDateFormat dateWithoutTimeFormatter = new SimpleDateFormat("yyyyMMdd");


    public Appointment(){
        dateWithTimeFormatter.setTimeZone(TimeZone.getTimeZone("MEZ"));
        dateWithoutTimeFormatter.setTimeZone(TimeZone.getTimeZone("MEZ"));

    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public void setDateStart(String dateString) throws ParseException {
        this.dateStart = Appointment.toDate(dateString);
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public void setDateEnd(String dateString) throws ParseException {
        this.dateEnd = Appointment.toDate(dateString);
    }

    public static Date toDate(String dateString) throws ParseException {
        Date finalDate;
        try{
            finalDate = dateWithTimeFormatter.parse(dateString);
        }
        catch(ParseException e){
            finalDate = dateWithoutTimeFormatter.parse(dateString);
        }
        return finalDate;
    }
}

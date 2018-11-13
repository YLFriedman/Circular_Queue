package ca.uottawa.seg2105.project.cqondemand.domain;

import android.support.annotation.NonNull;

public class Availability {

    public enum Day {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;
        public String toString() {
            return this.name().substring(0,1) + this.name().substring(1).toLowerCase();
        }
    }

    protected Day day;
    protected int startTime;
    protected int endTime;

    public Availability(@NonNull Day day, int startTime, int endTime) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Day getDay() {
        return day;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public static Availability.Day parseDay(@NonNull String input) {
        switch (input.toUpperCase()) {
            case "SUNDAY": return Day.SUNDAY;
            case "MONDAY": return Day.MONDAY;
            case "TUESDAY": return Day.TUESDAY;
            case "WEDNESDAY": return Day.WEDNESDAY;
            case "THURSDAY": return Day.THURSDAY;
            case "FRIDAY": return Day.FRIDAY;
            case "SATURDAY": return Day.SATURDAY;
            default: throw new IllegalArgumentException("'" + input + "' is not a valid day. ");
        }
    }

}

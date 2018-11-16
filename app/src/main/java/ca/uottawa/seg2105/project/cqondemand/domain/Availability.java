package ca.uottawa.seg2105.project.cqondemand.domain;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

public class Availability {

    public enum Day {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;
        @Override
        public String toString() {
            return this.name().substring(0,1) + this.name().substring(1).toLowerCase();
        }
        public int toInt() {
            switch (this) {
                case SUNDAY: return 0;
                case MONDAY: return 1;
                case TUESDAY: return 2;
                case WEDNESDAY: return 3;
                case THURSDAY: return 4;
                case FRIDAY: return 5;
                case SATURDAY: return 6;
                default: throw new IllegalArgumentException("'" + this.toString() + "' is not a valid day. ");
            }
        }
    }

    protected String key;
    protected Day day;
    protected int startTime;
    protected int endTime;

    public Availability(@NonNull Day day, int startTime, int endTime) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Availability(@NonNull String key, @NonNull Day day, int startTime, int endTime) {
        this.key = key;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getKey() {
        return key;
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

    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof Availability)) { return false; }
        if (this == otherObj) { return true; }
        Availability other = (Availability) otherObj;
        if (null != key && null != other.key) { return key.equals(other.key); }
        if ((null == day) != (null == other.day) || (null != day && !day.equals(other.day))) { return false; }
        return startTime == other.startTime && endTime == other.endTime;
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

    public static Availability.Day parseDay(@NonNull int input) {
        switch (input) {
            case 0: return Day.SUNDAY;
            case 1: return Day.MONDAY;
            case 2: return Day.TUESDAY;
            case 3: return Day.WEDNESDAY;
            case 4: return Day.THURSDAY;
            case 5: return Day.FRIDAY;
            case 6: return Day.SATURDAY;
            default: throw new IllegalArgumentException("'" + input + "' is not a valid day. ");
        }
    }

    public static boolean[][] toArrays(@NonNull List<Availability> list) {
        boolean[][] output = new boolean[7][24];
        for (Availability item: list) {
            int day = item.getDay().toInt();
            for (int i = item.getStartTime(); i < item.getEndTime(); i++) { output[day][i] = true; }
        }
        return output;
    }

    public static List<Availability> toList(@NonNull boolean[][] timeslots) {
        if (timeslots.length != 7) { throw new IllegalArgumentException("Invalid input array.  Must be 7 x 24: boolean[7][24]"); }
        List<Availability> output = new LinkedList<Availability>();
        int start = -1;
        int end = -1;
        boolean found = false;
        for (int day = 0; day < 7; day++) {
            if (timeslots[day].length != 24) { throw new IllegalArgumentException("Invalid input array.  Must be 7 x 24: boolean[7][24]"); }
            for (int time = 0; time < 24; time++) {
                if (timeslots[day][time]) {
                    if (!found) {
                        found = true;
                        start = end = time;
                    } else {
                        end++;
                    } // TODO: create availability it spans to the end of the day
                } else if (found) {
                    found = false;
                    end++;
                    output.add(new Availability(parseDay(day), start, end));
                }
            }
        }
        return output;
    }

}

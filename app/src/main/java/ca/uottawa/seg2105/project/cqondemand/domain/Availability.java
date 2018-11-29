package ca.uottawa.seg2105.project.cqondemand.domain;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

/**
 * Class to represent a ServiceProvider's availability
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class Availability implements Serializable {

    /**
     * This enum represents days of the week
     */
    public enum Day {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;
        @Override
        /**
         *Returns a string representation of the day ("Monday", "Tuesday", etc.)
         */
        public String toString() {
            return this.name().substring(0,1) + this.name().substring(1).toLowerCase();
        }

        /**
         * Return an integer representation of the day (0 - 6)
         * @return int representing the day
         */
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

        /**
         * Convert an int into a Day
         * @param day int representing the day
         * @return the corresponding Day
         */
        public static Day parse (int day) {
            switch (day) {
                case 0: return Day.SUNDAY;
                case 1: return Day.MONDAY;
                case 2: return Day.TUESDAY;
                case 3: return Day.WEDNESDAY;
                case 4: return Day.THURSDAY;
                case 5: return Day.FRIDAY;
                case 6: return Day.SATURDAY;
                default: throw new IllegalArgumentException("'" + day + "' is not a valid day. ");
            }
        }

        /**
         * Convert a string into a Day
         * @param day string representing the day
         * @return the corresponding Day
         */
        public static Day parse (String day) {
            switch (day.toUpperCase()) {
                case "SUNDAY": return Day.SUNDAY;
                case "MONDAY": return Day.MONDAY;
                case "TUESDAY": return Day.TUESDAY;
                case "WEDNESDAY": return Day.WEDNESDAY;
                case "THURSDAY": return Day.THURSDAY;
                case "FRIDAY": return Day.FRIDAY;
                case "SATURDAY": return Day.SATURDAY;
                default: throw new IllegalArgumentException("'" + day + "' is not a valid day. ");
            }
        }
    }

    private static final long serialVersionUID = 1;
    /**
     * Stores the day of the week
     */
    protected Day day;
    /**
     * Stores the availability start
     */
    protected int startTime;
    /**
     * Stores the availability end
     */
    protected int endTime;

    /**
     * Constructor for availability. Each availability specifies a day of the week, a start time, and an end time.
     * Start time and end time must be in integer form, within valid ranges (0 - 23 for start time, 1 - 24 for end time).
     * @param day the day of the week
     * @param startTime the hour the availability starts
     * @param endTime the hour the availability ends
     */
    public Availability(Day day, int startTime, int endTime) {
        if (null == day) { throw new InvalidDataException("A day is required"); }
        if (startTime < 0 || startTime > 23) { throw new InvalidDataException("Invalid startTime. Must be between 0 and 23 (midnight and 11 pm). "); }
        if (endTime < 1 || endTime > 24) { throw new InvalidDataException("Invalid endTime. Must be between 1 and 24 (1 am and midnight). "); }
        if (startTime >= endTime) { throw new InvalidDataException("Invalid startTime / endTime. The endTime must be greater than the startTime. "); }
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Getter for a particular Availabilities' Day
     *
     * @return the Day associated with this Availability
     */
    public Day getDay() {
        return day;
    }

    /**
     * Getter for a particular Availabilities start time
     *
     * @return the start time, in integer form, associated with this Availability
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * Getter for a particular Availabilities end time
     *
     * @return the end time, in integer form, associated with this Availability
     */
    public int getEndTime() {
        return endTime;
    }

    /**
     * Method to compare a particular Availability to another Object
     * @param otherObj the object to be compared to
     * @return whether or not this Availability is the same as the passed Object
     */
    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof Availability)) { return false; }
        if (this == otherObj) { return true; }
        Availability other = (Availability) otherObj;
        if ((null == day) != (null == other.day) || (null != day && !day.equals(other.day))) { return false; }
        return startTime == other.startTime && endTime == other.endTime;
    }

    /**
     * Method for transforming a List of Availabilities to a 2D array of booleans. Used for transforming
     * database information into a conventional weekly calendar type grid.
     *
     * @param list A list of availabilities
     * @return A 2D array of booleans representing the passed List.
     */
    public static boolean[][] toArrays(@NonNull List<Availability> list) {
        boolean[][] output = new boolean[7][24];
        for (Availability item: list) {
            int day = item.getDay().toInt();
            for (int i = item.getStartTime(); i < item.getEndTime(); i++) { output[day][i] = true; }
        }
        return output;
    }

    /**
     * Method for transforming a 2D array of booleans to a List of Availabilities. The array must be of size
     * 7*24(i.e. a representation of a single week). Used for converting information created in the UI to a format
     * more easily stored in the database
     * @param timeslots A 2D boolean array representing a weekly schedule
     * @return a List of Availabilities generated from that array
     */
    public static List<Availability> toList(@NonNull boolean[][] timeslots) {
        if (timeslots.length != 7) { throw new IllegalArgumentException("Invalid input array.  Must be 7 x 24: boolean[7][24]"); }
        List<Availability> output = new LinkedList<Availability>();
        int start = -1;
        int end = -1;
        boolean found = false;
        for (int day = 0; day < 7; day++) { // Check each day
            if (timeslots[day].length != 24) { throw new IllegalArgumentException("Invalid input array.  Must be 7 x 24: boolean[7][24]"); }
            for (int time = 0; time < 24; time++) { // Check each timeslot in each day
                if (timeslots[day][time]) { // If a timeslot is true it is considered to be an availability
                    if (!found) { // If we are not in an availability range, start a range
                        found = true;
                        start = end = time;
                    } else { // If we are already in a range, increase the end time of the range
                        end++;
                    }
                } else if (found) { // If the timeslot is false and we were in a range, this indicates the end of the range
                    // End the range, set the end time and create the availability
                    found = false;
                    end++;
                    output.add(new Availability(Day.parse(day), start, end));
                }
            }
            // If we are at the end of the day and we are in a range, end the range and create the availability
            if (found) {
                found = false;
                end++;
                output.add(new Availability(Day.parse(day), start, end));
            }
        }
        return output;
    }

}

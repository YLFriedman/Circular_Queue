package ca.uottawa.seg2105.project.cqondemand.database;

import androidx.annotation.NonNull;

import ca.uottawa.seg2105.project.cqondemand.domain.Availability;

/**
 * The class <b> DbAvailability </b> is a class used to take information from an Availability object, and
 * put it into a form more easily stored in the database. Methods to go back and forth between Availability
 * and DbAvailability are also provided
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class DbAvailability extends DbItem<Availability> {

    /**
     * The name of the day associated with a DbAvailability
     */
    public String day;

    /**
     * The start time associated with a DbAvailability
     */
    public Integer start_time;

    /**
     * The end time associated with a DbAvailability
     */
    public Integer end_time;

    /**
     * Empty constructor used by FireBase to create new DbAvailability objects
     */
    public DbAvailability() {}

    /**
     * Constructor that takes an Availability and creates a DbAvailability based on it
     * @param item the Availability that this DbAvailability will be based on
     */
    public DbAvailability(Availability item) {
        day = item.getDay().toString();
        start_time = item.getStartTime();
        end_time = item.getEndTime();
    }

    /**
     * Creates an Availability object based on this DbAvailability
     * @return an Availability based on this DbAvailability
     */
    @NonNull
    public Availability toDomainObj() { return new Availability(Availability.Day.parse(day), start_time, end_time); }

}

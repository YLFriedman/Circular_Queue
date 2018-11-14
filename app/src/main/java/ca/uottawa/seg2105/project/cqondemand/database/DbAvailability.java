package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;

import ca.uottawa.seg2105.project.cqondemand.domain.Availability;

public class DbAvailability extends DbItem<Availability> {

    public String key;
    public String day;
    public int start_time;
    public int end_time;

    public DbAvailability() {}

    DbAvailability(Availability availability) {
        this.day = availability.getDay().toString();
        this.start_time = availability.getStartTime();
        this.end_time = availability.getEndTime();
    }

    @NonNull
    public Availability toDomainObj() { return new Availability(key, Availability.parseDay(day), start_time, end_time); }

}

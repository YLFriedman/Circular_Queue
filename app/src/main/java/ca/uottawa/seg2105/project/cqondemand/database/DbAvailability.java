package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;

import ca.uottawa.seg2105.project.cqondemand.domain.Availability;

public class DbAvailability extends DbItem<Availability> {

    public String day;
    public int start_time;
    public int end_time;

    public DbAvailability() {}

    DbAvailability(Availability item) {
        super(item.getKey());
        storeKey(item.getKey());
        this.day = item.getDay().toString();
        this.start_time = item.getStartTime();
        this.end_time = item.getEndTime();
    }

    @NonNull
    public Availability toDomainObj() { return new Availability(retrieveKey(), Availability.parseDay(day), start_time, end_time); }

}

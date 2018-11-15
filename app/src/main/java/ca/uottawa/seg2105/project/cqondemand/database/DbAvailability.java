package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;

import ca.uottawa.seg2105.project.cqondemand.domain.Availability;

public class DbAvailability extends DbItem<Availability> {

    protected String day;
    protected int start_time;
    protected int end_time;

    public DbAvailability() {}

    public DbAvailability(Availability availability) {
        this.day = availability.getDay().toString();
        this.start_time = availability.getStartTime();
        this.end_time = availability.getEndTime();
    }

    @NonNull
    public Availability toDomainObj() { return new Availability(Availability.parseDay(day), start_time, end_time); }

    @NonNull
    public String generateKey() { return DbUtil.getSanitizedKey(""); }

}

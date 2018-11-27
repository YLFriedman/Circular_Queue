package ca.uottawa.seg2105.project.cqondemand.database;

import androidx.annotation.NonNull;

import ca.uottawa.seg2105.project.cqondemand.domain.Availability;

public class DbAvailability extends DbItem<Availability> {

    public String day;
    public Integer start_time;
    public Integer end_time;

    public DbAvailability() {}

    public DbAvailability(Availability item) {
        day = item.getDay().toString();
        start_time = item.getStartTime();
        end_time = item.getEndTime();
    }

    @NonNull
    public Availability toDomainObj() { return new Availability(Availability.Day.parse(day), start_time, end_time); }

}

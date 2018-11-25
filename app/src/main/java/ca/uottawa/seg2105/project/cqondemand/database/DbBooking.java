package ca.uottawa.seg2105.project.cqondemand.database;

import androidx.annotation.NonNull;

import java.util.Date;

import ca.uottawa.seg2105.project.cqondemand.domain.Booking;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

public class DbBooking extends DbItem<Booking> {

    public String status;
    public Date start_time;
    public Date end_time;
    public Date date_created;
    public Date date_cancelled_approved;
    public String cancelled_reason;
    public DbUser service_provider;
    public String service_provider_key;
    public DbUser homeowner;
    public String homeowner_key;

    public DbBooking() {}

    public DbBooking(Booking item) {
        super(item.getKey());
        status = item.getStatus().toString();
        start_time = item.getStartTime();
        end_time = item.getEndTime();
        date_created = item.getDateCreated();
        date_cancelled_approved = item.getDateCancelledOrApproved();
        service_provider = new DbUser(item.getServiceProvider());
        service_provider_key = item.getServiceProviderKey();
        homeowner = new DbUser(item.getHomeowner());
        homeowner_key = item.getHomeownerKey();
        cancelled_reason = item.getCancelledReason();
    }

    @NonNull
    public Booking toDomainObj() {
        if (null != service_provider) {
            return new Booking(getKey(), start_time, end_time, date_created, date_cancelled_approved, service_provider.toDomainObj(), homeowner_key, Booking.Status.parse(status), cancelled_reason);
        } else if (null != homeowner) {
            return new Booking(getKey(), start_time, end_time, date_created, date_cancelled_approved, homeowner.toDomainObj(), service_provider_key, Booking.Status.parse(status), cancelled_reason);
        } else {
            throw new InvalidDataException("");
        }
    }


}

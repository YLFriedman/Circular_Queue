package ca.uottawa.seg2105.project.cqondemand.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.Nullable;
import ca.uottawa.seg2105.project.cqondemand.domain.Booking;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

public class DbBooking extends DbItem<Booking> {

    public String status;
    public String service_name;

    public Integer service_rate;
    public Long start_time;
    public Long end_time;
    public Long date_created;
    public Long date_cancelled_approved;

    public String cancelled_reason;
    public DbUser service_provider;
    public String service_provider_key;
    public DbUser homeowner;
    public String homeowner_key;

    public DbBooking() {}

    public DbBooking(Booking item) {
        super(item.getKey());
        status = item.getStatus().toString();
        start_time = item.getStartTime().getTime();
        end_time = item.getEndTime().getTime();
        date_created = item.getDateCreated().getTime();
        service_name = item.getServiceName();
        service_rate = item.getServiceRate();
        cancelled_reason = item.getCancelledReason();
        homeowner_key = item.getHomeownerKey();
        service_provider_key = item.getServiceProviderKey();
        if (item.getDateCancelledOrApproved() != null) {
            date_cancelled_approved = item.getDateCancelledOrApproved().getTime();
        }
        if (item.getServiceProvider() != null) {
            service_provider = new DbUser(item.getServiceProvider());
        } else {
            homeowner = new DbUser(item.getHomeowner());
        }
    }

    @NonNull
    public Booking toDomainObj() {
        if (null != service_provider) {
            service_provider.setKey(service_provider_key);
            return new Booking(key, new Date(start_time),  new Date(end_time),  new Date(date_created), date_cancelled_approved == null ? null : new Date(date_cancelled_approved),
                    service_provider.toDomainObj(), service_provider_key, homeowner_key, Booking.Status.parse(status), service_name, service_rate, cancelled_reason, true);
        } else if (null != homeowner) {
            homeowner.setKey(homeowner_key);
            return new Booking(key, new Date(start_time), new Date(end_time),  new Date(date_created), date_cancelled_approved == null ? null : new Date(date_cancelled_approved),
                    homeowner.toDomainObj(), service_provider_key, homeowner_key, Booking.Status.parse(status), service_name, service_rate, cancelled_reason, false);
        } else {
            throw new InvalidDataException("A booking must have either a ServiceProvider or a Homeowner");
        }
    }

    public static void createBooking(@NonNull Booking withServiceProvider, @NonNull Booking withHomeowner, @Nullable AsyncActionEventListener listener){
        DatabaseReference keyMaker = FirebaseDatabase.getInstance().getReference().push();
        String bookingKey = keyMaker.getKey();
        String homeownerKey = withServiceProvider.getHomeownerKey();
        String serviceProviderKey = withHomeowner.getServiceProviderKey();
        DbBooking withServiceProviderDB = new DbBooking(withServiceProvider);
        DbBooking withHomeownerDB = new DbBooking(withHomeowner);
        String homeownerPath = String.format("user_bookings/%s/%s", homeownerKey, bookingKey);
        String serviceProviderPath = String.format("user_bookings/%s/%s", serviceProviderKey, bookingKey);
        String homeownerLookupPath = String.format("user_bookings_lookup/%s/%s/%s", homeownerKey, serviceProviderKey, bookingKey);
        String serviceProviderLookupPath = String.format("user_bookings_lookup/%s/%s/%s", serviceProviderKey, homeownerKey, bookingKey);
        HashMap<String, Object> map = new HashMap<>();
        map.put(homeownerPath, withServiceProviderDB);
        map.put(serviceProviderPath, withHomeownerDB);
        map.put(homeownerLookupPath, true);
        map.put(serviceProviderLookupPath, true);
        DbUtilRelational.multiPathUpdate(map, listener);
    }

    public static void getBookings(@NonNull User user, @NonNull AsyncValueEventListener<Booking> listener) {
        String userKey = user.getKey();
        DbUtilRelational.getItemsRelational(DbUtilRelational.RelationType.USER_BOOKINGS, userKey, listener);
    }

    public static DbListenerHandle<?> getBookingsLive(@NonNull User user, @NonNull AsyncValueEventListener<Booking> listener) {
        String userKey = user.getKey();
        return DbUtilRelational.getItemsRelationalLive(DbUtilRelational.RelationType.USER_BOOKINGS, userKey, listener);
    }

    public static void setBookingStatus(@NonNull Booking booking, @NonNull Booking.Status status, @Nullable String cancelledReason, @Nullable AsyncActionEventListener listener) {
        if (booking.getKey() == null) { throw new IllegalArgumentException("Booking key required to perform update"); }
        Date dateCancelledApproved = new Date(System.currentTimeMillis());
        String homeownerKey = booking.getHomeownerKey();
        String serviceProviderKey = booking.getServiceProviderKey();
        String bookingKey = booking.getKey();
        String homeownerPathStatus = String.format("user_bookings/%s/%s/status", homeownerKey, bookingKey);
        String homeownerPathReason = String.format("user_bookings/%s/%s/cancelled_reason", homeownerKey, bookingKey);
        String homeownerPathDate = String.format("user_bookings/%s/%s/date_cancelled_approved", homeownerKey, bookingKey);
        String providerPathStatus = String.format("user_bookings/%s/%s/status", serviceProviderKey, bookingKey);
        String providerPathReason = String.format("user_bookings/%s/%s/cancelled_reason", serviceProviderKey, bookingKey);
        String providerPathDate = String.format("user_bookings/%s/%s/date_cancelled_approved",serviceProviderKey, bookingKey);
        HashMap<String, Object> updateMap = new HashMap<>();
        if (cancelledReason != null) {
            updateMap.put(homeownerPathReason, cancelledReason);
            updateMap.put(providerPathReason, cancelledReason);
        }
        updateMap.put(homeownerPathStatus, status.toString());
        updateMap.put(providerPathStatus, status.toString());
        updateMap.put(providerPathDate, dateCancelledApproved.getTime());
        updateMap.put(homeownerPathDate, dateCancelledApproved.getTime());

        DbUtilRelational.multiPathUpdate(updateMap, listener);
    }

    public static void deleteBooking(@NonNull Booking booking, @Nullable AsyncActionEventListener listener) {
        if (booking.getKey() == null) { throw new IllegalArgumentException("Booking key required to perform deletion"); }
        String homeownerKey = booking.getHomeownerKey();
        String providerKey = booking.getServiceProviderKey();
        String bookingKey = booking.getKey();
        String homeownerDeletionPath = String.format("user_bookings/%s/%s", homeownerKey, bookingKey);
        String providerDeletionPath = String.format("user_bookings/%s/%s", providerKey, bookingKey);
        String lookupHomeownerPath = String.format("user_bookings_lookup/%s/%s/%s", homeownerKey, providerKey, bookingKey);
        String lookupProviderPath = String.format("user_bookings_lookup/%s/%s/%s",  providerKey, homeownerKey, bookingKey);
        HashMap<String, Object> deletionMap = new HashMap<>();
        deletionMap.put(homeownerDeletionPath, null);
        deletionMap.put(providerDeletionPath, null);
        deletionMap.put(lookupHomeownerPath, null);
        deletionMap.put(lookupProviderPath, null);
        DbUtilRelational.multiPathUpdate(deletionMap, listener);
    }

    public static void getBookingsAfterTime(@NonNull ServiceProvider provider, @NonNull Date dateTime, @NonNull AsyncValueEventListener<Booking> listener) {
        listener.onSuccess(new ArrayList<Booking>());
    }

}

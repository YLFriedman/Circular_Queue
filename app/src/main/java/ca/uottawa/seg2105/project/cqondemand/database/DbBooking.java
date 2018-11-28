package ca.uottawa.seg2105.project.cqondemand.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.Nullable;
import ca.uottawa.seg2105.project.cqondemand.domain.Booking;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class DbBooking extends DbItem<Booking> {

    public String status;
    public String service_name;
    public Integer service_rate;
    public Long start_time;
    public Long end_time;
    public Long date_created;
    public Long date_cancelled_approved;
    public String cancelled_reason;
    public String cancelled_by;
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
        cancelled_by = item.getCancelledBy();
        homeowner_key = item.getHomeownerKey();
        service_provider_key = item.getServiceProviderKey();
        if (item.getDateCancelledOrApproved() != null) {
            date_cancelled_approved = item.getDateCancelledOrApproved().getTime();
        }
        if (item.getServiceProvider() != null) { service_provider = new DbUser(item.getServiceProvider()); }
        if (item.getHomeowner() != null) { homeowner = new DbUser(item.getHomeowner()); }
    }

    @NonNull
    public Booking toDomainObj() {
        if (null == start_time || null == end_time || null == date_created) {
            throw new InvalidDataException("A booking must have a valid start time, end time and date created.");
        }
        if (null != service_provider) {
            service_provider.setKey(service_provider_key);
            return new Booking(key, new Date(start_time), new Date(end_time), new Date(date_created), date_cancelled_approved == null ? null : new Date(date_cancelled_approved),
                    (ServiceProvider) service_provider.toDomainObj(), homeowner_key, Booking.Status.parse(status), service_name, service_rate, cancelled_reason, cancelled_by);
        } else if (null != homeowner) {
            homeowner.setKey(homeowner_key);
            return new Booking(key, new Date(start_time), new Date(end_time), new Date(date_created), date_cancelled_approved == null ? null : new Date(date_cancelled_approved),
                    homeowner.toDomainObj(), service_provider_key, Booking.Status.parse(status), service_name, service_rate, cancelled_reason, cancelled_by);
        } else {
            throw new InvalidDataException("A booking must have a ServiceProvider or a Homeowner");
        }
    }

    public static void createBooking(@NonNull Booking booking, @Nullable AsyncActionEventListener listener) {
        if (null == booking.getHomeowner() || null == booking.getServiceProvider() ||
                null == booking.getHomeowner().getKey() || booking.getHomeowner().getKey().isEmpty() ||
                null == booking.getServiceProvider().getKey() || booking.getServiceProvider().getKey().isEmpty() ) {
            throw new IllegalArgumentException("A booking with a homeowner and service provider which both have keys is required.");
        }
        // Get the keys required for the database paths
        String bookingKey = DbUtil.generateKey();
        String homeownerKey = booking.getHomeownerKey();
        String serviceProviderKey = booking.getServiceProviderKey();
        // Create two DbBooking objects, one with a service provider, one with a homeowner
        DbBooking withServiceProviderDB = new DbBooking(booking);
        withServiceProviderDB.homeowner = null;
        DbBooking withHomeownerDB = new DbBooking(booking);
        withHomeownerDB.service_provider = null;
        HashMap<String, Object> map = new HashMap<>();
        // Add the homeowner path using the DbBooking containing a service provider object
        map.put(String.format("%s/%s/%s", DbUtilRelational.RelationType.USER_BOOKINGS, homeownerKey, bookingKey), withServiceProviderDB);
        // Add the service provider path using the DbBooking containing a homeowner object
        map.put(String.format("%s/%s/%s", DbUtilRelational.RelationType.USER_BOOKINGS, serviceProviderKey, bookingKey), withHomeownerDB);
        // Add the lookup paths for both the homeowner and service provider
        map.put(String.format("%s/%s/%s/%s", DbUtilRelational.LookupType.USER_BOOKINGS, homeownerKey, serviceProviderKey, bookingKey), true);
        map.put(String.format("%s/%s/%s/%s", DbUtilRelational.LookupType.USER_BOOKINGS, serviceProviderKey, homeownerKey, bookingKey), true);
        // Send the updates to the databse
        DbUtilRelational.multiPathUpdate(map, listener);
    }

    public static void getBookings(@NonNull User user, @NonNull AsyncValueEventListener<Booking> listener) {
        DbQuery query = DbQuery.createChildValueQuery("start_time");
        DbUtilRelational.getItemsRelational(DbUtilRelational.RelationType.USER_BOOKINGS, user.getKey(), query, listener);
    }

    public static DbListenerHandle<?> getBookingsLive(@NonNull User user, @NonNull AsyncValueEventListener<Booking> listener) {
        DbQuery query = DbQuery.createChildValueQuery("start_time");
        return DbUtilRelational.getItemsRelationalLive(DbUtilRelational.RelationType.USER_BOOKINGS, user.getKey(), query, listener);
    }

    public static void approveBooking(@NonNull Booking booking, @Nullable AsyncActionEventListener listener) {
        if (null == booking.getKey() || null == booking.getServiceProviderKey()) { throw new IllegalArgumentException("Booking key and provider key required to perform update"); }
        if (Booking.Status.REQUESTED != booking.getStatus()) { throw new IllegalArgumentException("Only a 'Requested' booking can be approved."); }
        //booking.approveBooking(updateTime);
        final Date updateTime = new Date(System.currentTimeMillis());
        setBookingStatus(booking, Booking.Status.APPROVED, updateTime, null, null, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                booking.approveBooking(updateTime);
                if (null != listener) { listener.onSuccess(); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                if (null != listener) { listener.onFailure(reason); }
            }
        });
    }

    public static void cancelBooking(@NonNull Booking booking, @Nullable String cancelReason, @Nullable AsyncActionEventListener listener) {
        if (null == booking.getKey() || null == booking.getServiceProviderKey()) { throw new IllegalArgumentException("Booking key and provider key required to perform update"); }
        if (Booking.Status.REQUESTED != booking.getStatus() && Booking.Status.APPROVED != booking.getStatus()) { throw new IllegalArgumentException("Only a 'Requested' or 'Approved' booking can be cancelled."); }
        final Date updateTime = new Date(System.currentTimeMillis());
        final User signedInUser = State.getInstance().getSignedInUser();
        if (null == signedInUser) {
            if (null != listener) { listener.onFailure(AsyncEventFailureReason.INVALID_DATA); }
            return;
        }
        setBookingStatus(booking, Booking.Status.CANCELLED, updateTime, cancelReason, signedInUser.getFullName(), new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                booking.cancelBooking(updateTime, cancelReason, signedInUser.getFullName());
                if (null != listener) { listener.onSuccess(); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                if (null != listener) { listener.onFailure(reason); }
            }
        });
    }

    private static void setBookingStatus(@NonNull Booking booking, @NonNull Booking.Status status, Date updateTime, @Nullable String cancelledReason, @Nullable String cancelledBy, @Nullable AsyncActionEventListener listener) {
        if (null == booking.getKey() || null == booking.getServiceProviderKey()) { throw new IllegalArgumentException("Booking key and provider key required to perform update"); }
        String homeownerKey = booking.getHomeownerKey();
        String serviceProviderKey = booking.getServiceProviderKey();
        String bookingKey = booking.getKey();
        String base = DbUtilRelational.RelationType.USER_BOOKINGS.toString();
        String homeownerPathStatus = String.format("%s/%s/%s/status", base, homeownerKey, bookingKey);
        String homeownerPathReason = String.format("%s/%s/%s/cancelled_reason", base, homeownerKey, bookingKey);
        String homeownerPathCancelledBy = String.format("%s/%s/%s/cancelled_by", base, homeownerKey, bookingKey);
        String homeownerPathDate = String.format("%s/%s/%s/date_cancelled_approved", base, homeownerKey, bookingKey);
        String providerPathStatus = String.format("%s/%s/%s/status", base, serviceProviderKey, bookingKey);
        String providerPathReason = String.format("%s/%s/%s/cancelled_reason", base, serviceProviderKey, bookingKey);
        String providerPathCancelledBy = String.format("%s/%s/%s/cancelled_by", base, serviceProviderKey, bookingKey);
        String providerPathDate = String.format("%s/%s/%s/date_cancelled_approved", base,serviceProviderKey, bookingKey);
        HashMap<String, Object> updateMap = new HashMap<>();
        if (cancelledReason != null && !cancelledReason.isEmpty()) {
            updateMap.put(homeownerPathReason, cancelledReason);
            updateMap.put(providerPathReason, cancelledReason);
        }
        if (cancelledBy != null && !cancelledBy.isEmpty()) {
            updateMap.put(homeownerPathCancelledBy, cancelledBy);
            updateMap.put(providerPathCancelledBy, cancelledBy);
        }
        updateMap.put(homeownerPathStatus, status.toString());
        updateMap.put(providerPathStatus, status.toString());
        updateMap.put(providerPathDate, updateTime.getTime());
        updateMap.put(homeownerPathDate, updateTime.getTime());

        DbUtilRelational.multiPathUpdate(updateMap, listener);
    }

    public static void deleteBooking(@NonNull Booking booking, @Nullable AsyncActionEventListener listener) {
        if (booking.getKey() == null) { throw new IllegalArgumentException("Booking key required to perform deletion"); }
        String homeownerKey = booking.getHomeownerKey();
        String providerKey = booking.getServiceProviderKey();
        String bookingKey = booking.getKey();
        String homeownerDeletionPath = String.format("%s/%s/%s", DbUtilRelational.RelationType.USER_BOOKINGS, homeownerKey, bookingKey);
        String providerDeletionPath = String.format("%s/%s/%s", DbUtilRelational.RelationType.USER_BOOKINGS, providerKey, bookingKey);
        String lookupHomeownerPath = String.format("%s/%s/%s/%s", DbUtilRelational.LookupType.USER_BOOKINGS, homeownerKey, providerKey, bookingKey);
        String lookupProviderPath = String.format("%s/%s/%s/%s", DbUtilRelational.LookupType.USER_BOOKINGS,  providerKey, homeownerKey, bookingKey);
        HashMap<String, Object> deletionMap = new HashMap<>();
        deletionMap.put(homeownerDeletionPath, null);
        deletionMap.put(providerDeletionPath, null);
        deletionMap.put(lookupHomeownerPath, null);
        deletionMap.put(lookupProviderPath, null);
        DbUtilRelational.multiPathUpdate(deletionMap, listener);
    }

    public static void getBookingsAfterTime(@NonNull ServiceProvider provider, @NonNull Date dateTime, @NonNull AsyncValueEventListener<Booking> listener) {
        DatabaseReference dbRef = DbUtil.getRef(String.format("%s/%s", DbUtilRelational.RelationType.USER_BOOKINGS, provider.getKey()));
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Booking> returnValue = new ArrayList<>();
                for(DataSnapshot candidate : dataSnapshot.getChildren()){
                    if(candidate.child("end_time").getValue(Long.class) > dateTime.getTime()){
                        DbBooking dbVersion = candidate.getValue(DbBooking.class);
                        dbVersion.setKey(candidate.getKey());
                        Booking current = dbVersion.toDomainObj();
                        returnValue.add(current);
                    }
                }
                listener.onSuccess(returnValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
            }
        });
    }

}

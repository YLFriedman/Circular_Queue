package ca.uottawa.seg2105.project.cqondemand.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import java.io.Serializable;
import java.util.Date;

/**
 * Class to represent a Booking between a Homeowner and a ServiceProvider. Constructors are given for two
 * different types of object - one that contains the Homeowner and the key of the service provider, and one that
 * contains the service provider and the key of the homeowner. This is due to a database storage strategy wherein both
 * versions are stored and accessible via associated keys.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class Booking implements Serializable {

    /**
     * Stores the class version for serialization and de-serialization
     */
    private static final long serialVersionUID = 1;
    /**
     * Stores the database key associated with this booking
     */
    protected String key;
    /**
     * Stores the booking status
     */
    protected Status status;
    /**
     * Stores the booking start time
     */
    protected Date startTime;
    /**
     * Stores the booking end time
     */
    protected Date endTime;
    /**
     * Stores the date the booking was created
     */
    protected Date dateCreated;
    /**
     * Stores the date the booking was cancelled or approved
     */
    protected Date dateCancelledOrApproved;
    /**
     * Stores the reason the booking was cancelled
     */
    protected String cancelledReason;
    /**
     * Stores the name of the user who cancelled the booking
     */
    protected String cancelledBy;
    /**
     * Stores the service provider associated with the booking
     */
    protected ServiceProvider serviceProvider;
    /**
     * Stores the service provider key associated with the booking
     */
    protected String serviceProviderKey;
    /**
     * Stores the homeowner object associated with the booking
     */
    protected User homeowner;
    /**
     * Stores the homeowner key associated with the booking
     */
    protected String homeownerKey;
    /**
     * Stores the name of the service associated with the booking
     */
    protected String serviceName;
    /**
     * Stores the hourly service rate associated with the booking
     */
    protected int serviceRate;

    /**
     * Enum to represent the status of a booking
     */
    public enum Status {
        CANCELLED, APPROVED, REQUESTED, EXPIRED, COMPLETED;
        @Override
        public String toString() {
            switch (this) {
                case CANCELLED: return "Cancelled";
                case APPROVED: return "Approved";
                case REQUESTED: return "Requested";
                case EXPIRED: return "Expired";
                case COMPLETED: return "Completed";
                default: throw new IllegalArgumentException("Invalid Status");
            }
        }
        public static Status parse(String status) {
            for (Status s: Status.values()) { if (s.toString().toLowerCase().equals(status.toLowerCase())) { return s; } }
            throw new IllegalArgumentException("Invalid Status");
        }
    }

    /**
     * Public constructor for a new Booking object. Does not take a key as a parameter. Intended to be used
     * before the booking has been stored in the database (i.e. before a key has been generated)
     *
     * @param startTime Date representing the starting time of this booking
     * @param endTime Date representing the end time of this booking
     * @param homeowner homeowner associated with this booking
     * @param provider service provider associated with this booking
     * @param service the service associated with this booking.
     */
    public Booking(@NonNull Date startTime, @NonNull Date endTime, @NonNull User homeowner, @NonNull ServiceProvider provider, @NonNull Service service) {
        this("temp", startTime, endTime, new Date(), null, homeowner, homeowner.getKey(), provider, provider.getKey(),
                Status.REQUESTED, service.getName(), service.getRate(), null, null);
        this.key = null;
        if (startTime.compareTo(new Date()) <= 0) { throw new InvalidDataException("Booking must be in the future.");}
    }

    /**
     * Constructor requiring a key. Requires a homeowner object and the key of the associated service provider.
     * @param key the key associated with this booking
     * @param startTime Date representing the start time of this booking
     * @param endTime Date representing the end time of this booking
     * @param dateCreated Date this booking was created
     * @param dateCancelledOrApproved Date this booking was cancelled/approved
     * @param homeowner the homeowner associated with this booking
     * @param serviceProviderKey the key of the ServiceProvider associated with this booking
     * @param status the current Status of this booking
     * @param serviceName the name of the service associated with this booking
     * @param serviceRate the hourly rate of the service associated with this booking
     * @param cancelledReason the reason this booking was cancelled. Null if booking has not been cancelled
     * @param cancelledBy The name of the user who cancelled this booking (i.e either the service provider or the homeowner)
     */
    public Booking(@NonNull String key, @NonNull Date startTime, @NonNull Date endTime, @NonNull Date dateCreated, @Nullable Date dateCancelledOrApproved,
                   @NonNull User homeowner, @NonNull String serviceProviderKey, @NonNull Status status, @NonNull String serviceName, int serviceRate,
                   @Nullable String cancelledReason, @Nullable String cancelledBy) {
        this(key, startTime, endTime, dateCreated, dateCancelledOrApproved, homeowner, homeowner.getKey(), null, serviceProviderKey, status, serviceName, serviceRate, cancelledReason, cancelledBy);
    }

    /**
     * Constructor requiring a key. Requires a homeowner object and the key of the associated service provider.
     * @param key the key associated with this booking
     * @param startTime Date representing the start time of this booking
     * @param endTime Date representing the end time of this booking
     * @param dateCreated Date this booking was created
     * @param dateCancelledOrApproved Date this booking was cancelled/approved
     * @param provider the service provider associated with this booking
     * @param homeownerKey the key of the homeowner associated with this booking
     * @param status the current Status of this booking
     * @param serviceName the name of the service associated with this booking
     * @param serviceRate the hourly rate of the service associated with this booking
     * @param cancelledReason the reason this booking was cancelled. Null if booking has not been cancelled
     * @param cancelledBy The name of the user who cancelled this booking (i.e either the service provider or the homeowner)
     */
    public Booking(@NonNull String key, @NonNull Date startTime, @NonNull Date endTime, @NonNull Date dateCreated, @Nullable Date dateCancelledOrApproved,
                   @NonNull ServiceProvider provider, @NonNull String homeownerKey, @NonNull Status status, @NonNull String serviceName, int serviceRate,
                   @Nullable String cancelledReason, @Nullable String cancelledBy) {
        this(key, startTime, endTime, dateCreated, dateCancelledOrApproved, null, homeownerKey, provider, provider.getKey(), status, serviceName, serviceRate, cancelledReason, cancelledBy);
    }

    /**
     *Private constructor used by the first public constructor. (i.e. the one intended to be used before the booking has been
     * stored in the database)
     *
     */
    private Booking (String key, Date startTime, Date endTime, Date dateCreated, Date dateCancelledOrApproved, User homeowner, String homeownerKey,
                     ServiceProvider provider, String providerKey, Status status, String serviceName, int serviceRate, String cancelledReason, String cancelledBy) {
        if (null == key || key.isEmpty()) { throw new InvalidDataException("The key cannot be null or empty."); }
        Date now = new Date();
        if (startTime.compareTo(endTime) >= 0) { throw new InvalidDataException("The end time must be after the start time.");}
        this.key = key;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dateCreated = dateCreated;
        this.dateCancelledOrApproved = dateCancelledOrApproved;
        this.homeowner = homeowner;
        this.homeownerKey = homeownerKey;
        this.serviceProvider = provider;
        this.serviceProviderKey = providerKey;
        this.status = status;
        this.serviceName = serviceName;
        this.serviceRate = serviceRate;
        this.cancelledReason = cancelledReason;
        this.cancelledBy = cancelledBy;
    }

    /**
     * Gets the key associated with a particular booking
     * @return the associated key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the status associated with a particular booking
     * @return the associated status
     */
    public Status getStatus() {
        // If the booking end time is in the past
        if (endTime.getTime() < System.currentTimeMillis()) {
            if (Status.REQUESTED == status) { return Status.EXPIRED; }
            if (Status.APPROVED == status) { return Status.COMPLETED; }
        }
        return status;
    }

    /**
     * Gets the start time associated with a particular booking
     * @return the associated start time
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Gets the end time associated with a particular booking
     * @return the associated end time
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * Gets the service name associated with a particular booking
     * @return the associated service name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Gets the hourly service rate associated with a particular booking
     * @return the associated rate
     */
    public int getServiceRate() {
        return serviceRate;
    }

    /**
     * Gets the creation date associated with a particular booking
     * @return the date this booking was created
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * Gets the date a particular booking was cancelled/approved
     * @return the cancellation/approval date
     */
    public Date getDateCancelledOrApproved() {
        return dateCancelledOrApproved;
    }

    /**
     * Gets the reason a booking was cancelled. Will return Null if booking has not been cancelled.
     * @return the reason for cancellation.
     */
    public String getCancelledReason() {
        return cancelledReason;
    }

    /**
     * Gets the name of the user who cancelled this booking. Will return Null if not cancelled.
     * @return the name of the user who cancelled this booking.
     */
    public String getCancelledBy() {
        return cancelledBy;
    }

    /**
     * Gets the Service Provider associated with a particular booking
     * @return the associated Service Provider
     */
    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    /**
     * Gets the key of the Service Provider associated with a particular booking
     * @return the associated service provider key
     */
    public String getServiceProviderKey() {
        return serviceProviderKey;
    }

    /**
     * Gets the homeowner associated with a particular booking
     * @return the associated homeowner
     */
    public User getHomeowner() {
        return homeowner;
    }

    /**
     * Gets the key of the homeowner associated with a particular booking
     * @return the associated homeowner key
     */
    public String getHomeownerKey() {
        return homeownerKey;
    }

    /**
     * Set a booking's status to approved.
     *
     * @param updateTime the Date of approval
     */
    public void approveBooking(@NonNull Date updateTime) {
        status = Status.APPROVED;
        dateCancelledOrApproved = updateTime;
    }

    /**
     * Set a booking's status to cancelled
     * @param updateTime the date of cancellation
     * @param cancelledReason the reason for cancellation
     * @param cancelledBy the name of the user who cancelled this Booking
     */
    public void cancelBooking(@NonNull Date updateTime, @Nullable String cancelledReason, @Nullable String cancelledBy) {
        status = Status.CANCELLED;
        this.cancelledReason = cancelledReason;
        this.cancelledBy = cancelledBy;
        dateCancelledOrApproved = updateTime;
    }

}

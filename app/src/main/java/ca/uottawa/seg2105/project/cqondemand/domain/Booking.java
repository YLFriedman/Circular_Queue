package ca.uottawa.seg2105.project.cqondemand.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import java.io.Serializable;
import java.util.Date;

public class Booking implements Serializable {

    private static final long serialVersionUID = 1;
    protected String key;
    protected Status status;
    protected Date startTime;
    protected Date endTime;
    protected Date dateCreated;
    protected Date dateCancelledOrApproved;
    protected String cancelledReason;
    protected String cancelledBy;
    protected ServiceProvider serviceProvider;
    protected String serviceProviderKey;
    protected User homeowner;
    protected String homeownerKey;
    protected String serviceName;
    protected int serviceRate;

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

    public Booking(@NonNull Date startTime, @NonNull Date endTime, @NonNull User homeowner, @NonNull ServiceProvider provider, @NonNull Service service) {
        this("temp", startTime, endTime, new Date(), null, homeowner, homeowner.getKey(), provider, provider.getKey(),
                Status.REQUESTED, service.getName(), service.getRate(), null, null);
        this.key = null;
        if (startTime.compareTo(new Date()) <= 0) { throw new InvalidDataException("Booking must be in the future.");}
    }

    public Booking(@NonNull String key, @NonNull Date startTime, @NonNull Date endTime, @NonNull Date dateCreated, @Nullable Date dateCancelledOrApproved,
                   @NonNull User homeowner, @NonNull String serviceProviderKey, @NonNull Status status, @NonNull String serviceName, int serviceRate,
                   @Nullable String cancelledReason, @Nullable String cancelledBy) {
        this(key, startTime, endTime, dateCreated, dateCancelledOrApproved, homeowner, homeowner.getKey(), null, serviceProviderKey, status, serviceName, serviceRate, cancelledReason, cancelledBy);
    }

    public Booking(@NonNull String key, @NonNull Date startTime, @NonNull Date endTime, @NonNull Date dateCreated, @Nullable Date dateCancelledOrApproved,
                   @NonNull ServiceProvider provider, @NonNull String homeownerKey, @NonNull Status status, @NonNull String serviceName, int serviceRate,
                   @Nullable String cancelledReason, @Nullable String cancelledBy) {
        this(key, startTime, endTime, dateCreated, dateCancelledOrApproved, null, homeownerKey, provider, provider.getKey(), status, serviceName, serviceRate, cancelledReason, cancelledBy);
    }

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

    public String getKey() {
        return key;
    }

    public Status getStatus() {
        // If the booking end time is in the past
        if (endTime.getTime() < System.currentTimeMillis()) {
            if (Status.REQUESTED == status) { return Status.EXPIRED; }
            if (Status.APPROVED == status) { return Status.COMPLETED; }
        }
        return status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getServiceRate() {
        return serviceRate;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getDateCancelledOrApproved() {
        return dateCancelledOrApproved;
    }

    public String getCancelledReason() {
        return cancelledReason;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public String getServiceProviderKey() {
        return serviceProviderKey;
    }

    public User getHomeowner() {
        return homeowner;
    }

    public String getHomeownerKey() {
        return homeownerKey;
    }

    public void approveBooking(@NonNull Date updateTime) {
        status = Status.APPROVED;
        dateCancelledOrApproved = updateTime;
    }

    public void cancelBooking(@NonNull Date updateTime, @Nullable String cancelledReason, @Nullable String cancelledBy) {
        status = Status.CANCELLED;
        this.cancelledReason = cancelledReason;
        this.cancelledBy = cancelledBy;
        dateCancelledOrApproved = updateTime;
    }

}

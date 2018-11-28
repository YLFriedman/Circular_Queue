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
    protected ServiceProvider serviceProvider;
    protected String serviceProviderKey;
    protected User homeowner;
    protected String homeownerKey;
    protected String serviceName;
    protected int serviceRate;

    public enum Status {
        CANCELLED, APPROVED, REQUESTED;
        @Override
        public String toString() {
            switch (this) {
                case CANCELLED: return "Cancelled";
                case APPROVED: return "Approved";
                case REQUESTED: return "Requested";
                default: throw new IllegalArgumentException("Invalid Status");
            }
        }
        public static Status parse(String status) {
            switch (status.toUpperCase()) {
                case "CANCELLED": return Status.CANCELLED;
                case "APPROVED": return Status.APPROVED;
                case "REQUESTED": return Status.REQUESTED;
                default: throw new IllegalArgumentException("Invalid Status");
            }
        }
    }

    public Booking(@NonNull Date startTime, @NonNull Date endTime, @NonNull User homeowner, @NonNull ServiceProvider provider, @NonNull Service service) {
        Date now = new Date();
        if (startTime.compareTo(endTime) >= 0) { throw new InvalidDataException("The end time must be after the start time.");}
        if (startTime.compareTo(now) >= 0) { throw new InvalidDataException("Booking must be in the future.");}
        this.startTime = startTime;
        this.endTime = endTime;
        this.homeownerKey = homeowner.getKey();
        this.status = Status.REQUESTED;
        this.dateCreated = now;
        this.serviceName = service.getName();
        this.serviceRate = service.getRate();
        this.serviceProviderKey = provider.getKey();
        this.homeowner = homeowner;
        this.serviceProvider = provider;
    }

    private Booking (String key, Date startTime, Date endTime, Date dateCreated, Date dateCancelledOrApproved, User homeowner, String homeownerKey,
                     ServiceProvider provider, String providerKey, Status status, String serviceName, int serviceRate, String cancelledReason) {
        if (null == key || key.isEmpty()) { throw new InvalidDataException("The key cannot be null or empty."); }
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
    }

    public Booking(@NonNull String key, @NonNull Date startTime, @NonNull Date endTime, @NonNull Date dateCreated, @Nullable Date dateCancelledOrApproved,
                   @NonNull User homeowner, @NonNull String serviceProviderKey, @NonNull Status status, @NonNull String serviceName, int serviceRate, @Nullable String cancelledReason) {
        this(key, startTime, endTime, dateCreated, dateCancelledOrApproved, homeowner, homeowner.getKey(), null, serviceProviderKey, status, serviceName, serviceRate, cancelledReason);
    }

    public Booking(@NonNull String key, @NonNull Date startTime, @NonNull Date endTime, @NonNull Date dateCreated, @Nullable Date dateCancelledOrApproved,
                   @NonNull ServiceProvider provider, @NonNull String homeownerKey, @NonNull Status status, @NonNull String serviceName, int serviceRate, @Nullable String cancelledReason) {
        this(key, startTime, endTime, dateCreated, dateCancelledOrApproved, null, homeownerKey, provider, provider.getKey(), status, serviceName, serviceRate, cancelledReason);
    }

    public String getKey() {
        return key;
    }

    public Status getStatus() {
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

}

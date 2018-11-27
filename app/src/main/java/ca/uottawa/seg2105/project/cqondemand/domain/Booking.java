package ca.uottawa.seg2105.project.cqondemand.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    public Booking(@NonNull Date startTime, @NonNull Date endTime, @NonNull User providerOrHomeowner, @NonNull String serviceProviderKey, @NonNull String homeownerKey, @NonNull Service service, boolean containsProvider) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.homeownerKey = homeownerKey;
        this.status = Status.REQUESTED;
        this.dateCreated = new Date();
        this.serviceName = service.getName();
        this.serviceRate = service.getRate();
        this.serviceProviderKey = serviceProviderKey;
        if (containsProvider) {
            this.serviceProvider = (ServiceProvider) providerOrHomeowner;
            this.homeowner = null;
        } else {
            this.homeowner = providerOrHomeowner;
            this.serviceProvider = null;
        }

    }

    public Booking(@NonNull String key, @NonNull Date startTime, @NonNull Date endTime, @NonNull Date dateCreated, @Nullable Date dateCancelledOrApproved, User providerOrHomeowner, @NonNull String serviceProviderKey,
                   @NonNull String homeownerKey, @NonNull Status status, @NonNull String serviceName, int serviceRate, @Nullable String cancelledReason, boolean containsProvider) {
        this(startTime, endTime, providerOrHomeowner, serviceProviderKey, homeownerKey, new Service(serviceName, serviceRate, ""), containsProvider);
        this.key = key;
        this.dateCreated = dateCreated;
        this.status = status;
        this.cancelledReason = cancelledReason;
        this.dateCancelledOrApproved = dateCancelledOrApproved;
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

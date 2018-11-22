package ca.uottawa.seg2105.project.cqondemand.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public class Booking {


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

    public enum Status {
        CANCELLED, APPROVED, REQUESTED;
        @Override
        public String toString() {
            switch (this) {
                case CANCELLED: return "Canceled";
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

    public Booking(@NonNull String key, Date startTime, Date endTime, Date dateCreated, @Nullable Date dateCancelledOrApproved, ServiceProvider provider,
                   String homeownerKey, Status status, @Nullable String cancelledReason) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.serviceProvider = provider;
        this.homeownerKey = homeownerKey;
        this.status = status;
        this.cancelledReason = cancelledReason;
        this.key = key;
        this.dateCreated = dateCreated;
        this.dateCancelledOrApproved = dateCancelledOrApproved;
    }

    public Booking(@NonNull String key, Date startTime, Date endTime, Date dateCreated, @Nullable Date dateCancelledOrApproved, User homeowner,
                   String serviceProviderKey, Status status, @Nullable String cancelledReason) {

        this.startTime = startTime;
        this.endTime = endTime;
        this.homeowner = homeowner;
        this.serviceProviderKey = serviceProviderKey;
        this.status = status;
        this.cancelledReason = cancelledReason;
        this.key = key;
        this.dateCreated = dateCreated;
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

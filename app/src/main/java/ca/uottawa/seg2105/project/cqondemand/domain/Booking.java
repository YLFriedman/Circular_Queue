package ca.uottawa.seg2105.project.cqondemand.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.sql.Timestamp;

public class Booking {

    protected String key;
    protected Status status;
    protected Timestamp startTime;
    protected Timestamp endTime;
    protected Timestamp dateCreated;
    protected Timestamp dateCancelledOrApproved;
    protected String cancelledReason;
    protected ServiceProvider serviceProvider;
    protected String serviceProviderKey;
    protected User homeowner;
    protected String homeownerKey;
    protected String serviceName;
    protected Integer serviceRate;

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

    public Booking(Timestamp startTime, Timestamp endTime, Timestamp dateCreated, @Nullable Timestamp dateCancelledOrApproved, User providerOrHomeowner, String serviceProviderKey,
                   String homeownerKey, Status status, String serviceName, Integer serviceRate, @Nullable String cancelledReason, boolean containsProvider) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.homeownerKey = homeownerKey;
        this.status = status;
        this.cancelledReason = cancelledReason;
        this.dateCreated = dateCreated;
        this.dateCancelledOrApproved = dateCancelledOrApproved;
        this.serviceName = serviceName;
        this.serviceRate = serviceRate;
        this.serviceProviderKey = serviceProviderKey;
        if(containsProvider){
            this.serviceProvider = (ServiceProvider) providerOrHomeowner;
            this.homeowner = null;
        }
        else{
            this.homeowner = providerOrHomeowner;
            this.serviceProvider = null;
        }

    }


    public void setKey(String key) { this.key = key; }

    public String getBookingKey() { return this.key; }

    public Status getStatus() {
        return status;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public String getServiceName() { return serviceName; }

    public Integer getServiceRate() { return serviceRate; }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public Timestamp getDateCancelledOrApproved() {
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

package ca.uottawa.seg2105.project.cqondemand.domain;

import android.support.annotation.Nullable;

import java.util.Date;

public class Booking {



    protected Date startTime;
    protected Date endTime;
    protected Date dateCreated;
    protected Date dateCancelledOrApproved;
    protected ServiceProvider provider;
    protected User homeOwner;
    protected String cancelledReason;
    protected Status status;
    protected String homeOwnerKey;
    protected String serviceProviderKey;
    protected String bookingKey;

    private enum Status{
        CANCELLED, APPROVED, REQUESTED;
    }

    public Booking(Date startTime, Date endTime, Date dateCreated, @Nullable Date dateCancelledOrApproved, ServiceProvider provider,
                   String homeOwnerKey, Status status, @Nullable String cancelledReason, String bookingKey) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.provider = provider;
        this.homeOwnerKey = homeOwnerKey;
        this.status = status;
        this.cancelledReason = cancelledReason;
        this.bookingKey = bookingKey;
        this.dateCreated = dateCreated;
        this.dateCancelledOrApproved = dateCancelledOrApproved;
    }

    public Booking(Date startTime, Date endTime, Date dateCreated, @Nullable Date dateCancelledOrApproved, User homeOwner,
                   String serviceProviderKey, Status status, @Nullable String cancelledReason, String bookingKey) {

        this.startTime = startTime;
        this.endTime = endTime;
        this.homeOwner = homeOwner;
        this.serviceProviderKey = serviceProviderKey;
        this.status = status;
        this.cancelledReason = cancelledReason;
        this.bookingKey = bookingKey;
        this.dateCreated = dateCreated;
        this.dateCancelledOrApproved = dateCancelledOrApproved;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getDateCancelledOrApproved() {
        return dateCancelledOrApproved;
    }

    public ServiceProvider getProvider() {
        return provider;
    }

    public String getCancelledReason() {
        return cancelledReason;
    }

    public Status getStatus() {
        return status;
    }

    public String getHomeOwnerKey() {
        return homeOwnerKey;
    }

    public String getServiceProviderKey() {
        return serviceProviderKey;
    }

    public String getBookingKey() {
        return bookingKey;
    }

}

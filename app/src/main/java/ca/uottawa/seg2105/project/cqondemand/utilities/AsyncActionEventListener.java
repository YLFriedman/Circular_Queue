package ca.uottawa.seg2105.project.cqondemand.utilities;

import androidx.annotation.NonNull;

/**
 * Classes implementing this interface can be used to receive events and take
 * action according to the success or failure condition of the event.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public interface AsyncActionEventListener {

    /**
     * The action that is triggered on the successful completion of the event
     */
    public void onSuccess();

    /**
     * The action that is triggered on the unsuccessful completion of the event
     * @param reason the reason that the event was unsuccessful
     */
    public void onFailure(@NonNull AsyncEventFailureReason reason);

}

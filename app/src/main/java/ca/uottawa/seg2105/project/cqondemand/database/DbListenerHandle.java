package ca.uottawa.seg2105.project.cqondemand.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * This class serves as a wrapper for FireBase listeners, and allows for the UI to manipulate and remove
 * the listener when appropriate.
 *
 * @param <T> the wrapped listener
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class DbListenerHandle<T> {

    /**
     * The database location the listener will be attached to
     */
    private DatabaseReference reference;

    /**
     * The wrapped listener, either of type ValueEventListener or ChildEventListener
     */
    private T listener;

    /**
     * Constructor for DbListenerHandle. Requires a listener and a a reference to the location in the
     * database that the listener will be attached to.
     *
     * @param reference the database location to be listened to
     * @param listener the listener that will be attached to the location
     */
    public DbListenerHandle(@NonNull DatabaseReference reference, @Nullable T listener) {
        this.reference = reference;
        this.listener = listener;
    }

    /**
     * Returns the reference location that the internal listener is attached to
     *
     * @return the DatabaseReference associated with this DbListenerHandle
     */
    @NonNull
    public DatabaseReference getReference() {
        return reference;
    }

    /**
     * Returns the wrapped listener itself
     *
     * @return the attached listener
     */
    @Nullable
    public T getListener() {
        return listener;
    }

    /**
     * Removes the attached listener
     */
    public void removeListener() {
        if (null == listener) { return; }
        if (listener instanceof ValueEventListener) {
            reference.removeEventListener((ValueEventListener) listener);
        } else if (listener instanceof ChildEventListener) {
            reference.removeEventListener((ChildEventListener) listener);
        }
    }

}

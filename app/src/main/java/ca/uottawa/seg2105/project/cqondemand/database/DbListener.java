package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DbListener<T> {

    private DatabaseReference reference;
    private T listener;

    public DbListener(@NonNull DatabaseReference reference, @Nullable T listener) {
        this.reference = reference;
        this.listener = listener;
    }

    @NonNull
    public DatabaseReference getReference() {
        return reference;
    }

    @Nullable
    public T getListener() {
        return listener;
    }

    public void removeListener() {
        if (null == listener) { return; }
        if (listener instanceof ValueEventListener) {
            reference.removeEventListener((ValueEventListener) listener);
        } else if (listener instanceof ChildEventListener) {
            reference.removeEventListener((ChildEventListener) listener);
        }
    }

}

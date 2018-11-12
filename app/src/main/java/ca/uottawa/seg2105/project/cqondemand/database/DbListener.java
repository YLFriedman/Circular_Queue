package ca.uottawa.seg2105.project.cqondemand.database;

import android.renderscript.Sampler;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DbListener<T> {

    private DatabaseReference reference;
    private T listener;

    public DbListener(DatabaseReference reference, T listener) {
        this.reference = reference;
        this.listener = listener;
    }

    public DatabaseReference getReference() {
        return reference;
    }

    public T getListener() {
        return listener;
    }

    public void removeListener() {
        if (listener instanceof ValueEventListener) {
            reference.removeEventListener((ValueEventListener) listener);
        } else if (listener instanceof ChildEventListener) {
            reference.removeEventListener((ChildEventListener) listener);
        }
    }

}

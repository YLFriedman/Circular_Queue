package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.uottawa.seg2105.project.cqondemand.domain.Availability;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

public class DbAvailability extends DbItem<Availability> {

    public String day;
    public int start_time;
    public int end_time;

    public DbAvailability() {}

    DbAvailability(Availability item) {
        this.day = item.getDay().toString();
        this.start_time = item.getStartTime();
        this.end_time = item.getEndTime();
    }

    @NonNull
    public Availability toDomainObj() { return new Availability(Availability.parseDay(day), start_time, end_time); }

    public static void setAvailabilities(@NonNull ServiceProvider provider, @NonNull List<Availability> items, @Nullable final AsyncActionEventListener listener) {
        if (null == provider.getKey() || provider.getKey().isEmpty()) { throw new IllegalArgumentException("A service provider object with a key is required. Unable to update the database without the key."); }
        ArrayList<DbAvailability> dbItems = new ArrayList<>();
        String serviceProviderKey = provider.getKey();
        for (Availability item: items) { dbItems.add(new DbAvailability(item)); }
        DbUtil.DataType.AVAILABILITY.getRef().child(serviceProviderKey).setValue(dbItems, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (null != listener) {
                    if (null == databaseError) { listener.onSuccess(); }
                    else { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static void getAvailabilities(@NonNull ServiceProvider provider, @NonNull final AsyncValueEventListener<Availability> listener) {
        if (null == provider.getKey() || provider.getKey().isEmpty()) { throw new IllegalArgumentException("A service provider object with a key is required. Unable to query the database without the key."); }
        final ArrayList<Availability> returnValue = new ArrayList<>();
        DbUtil.DataType.AVAILABILITY.getRef().child(provider.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item: snapshot.getChildren()) {
                    try {
                        DbItem<Availability> dbItem = (DbItem<Availability>) item.getValue(DbUtil.DataType.AVAILABILITY.getDbItemClass());
                        if (null != dbItem && null != item.getKey()) {
                            dbItem.storeKey(item.getKey());
                            Availability domainObjItem = dbItem.toDomainObj();
                            returnValue.add(domainObjItem);
                        }
                    }
                    catch (IllegalArgumentException ignored) { }
                    catch (InvalidDataException ignored) { }
                    catch (ClassCastException ignored) { }
                }
                listener.onSuccess(returnValue);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
            }
        });
    }

}

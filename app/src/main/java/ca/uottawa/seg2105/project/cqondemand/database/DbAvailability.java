package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
        super(item.getKey());
        storeKey(item.getKey());
        this.day = item.getDay().toString();
        this.start_time = item.getStartTime();
        this.end_time = item.getEndTime();
    }

    @NonNull
    public Availability toDomainObj() { return new Availability(retrieveKey(), Availability.parseDay(day), start_time, end_time); }

    public static void setAvailabilities(@NonNull ServiceProvider provider, @NonNull ArrayList<Availability> items, @Nullable final AsyncActionEventListener listener) {
        if (null == provider.getKey() || provider.getKey().isEmpty()) { throw new IllegalArgumentException("A service provider object with a key is required. Unable to update the database without the key."); }
        ArrayList<DbAvailability> dbItems = new ArrayList<>();
        String serviceProviderKey = provider.getKey();
        for (Availability item: items) { dbItems.add(new DbAvailability(item)); }
        DbUtil.getRef(DbUtil.DataType.AVAILABILITY).child(serviceProviderKey).setValue(dbItems, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (null != listener) {
                        if (null == databaseError) { listener.onSuccess(); }
                        else { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
                    }
                }
            }
        );
    }

    @SuppressWarnings("unchecked")
    public static void getAvailabilities(@NonNull ServiceProvider provider, @NonNull final AsyncValueEventListener<Availability> listener) {
        if (null == provider.getKey() || provider.getKey().isEmpty()) { throw new IllegalArgumentException("A service provider object with a key is required. Unable to query the database without the key."); }
        final ArrayList<Availability> returnValue = new ArrayList<>();
        DbUtil.getRef(DbUtil.DataType.AVAILABILITY).child(provider.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap: snapshot.getChildren()) {
                    try {
                        DbItem<Availability> dbItem = (DbItem<Availability>) snapshot.getValue(DbUtil.DataType.AVAILABILITY.getDbItemClass());
                        if (null != dbItem && null != snapshot.getKey()) {
                            dbItem.storeKey(snapshot.getKey());
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

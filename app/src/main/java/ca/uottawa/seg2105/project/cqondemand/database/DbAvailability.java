package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.domain.Availability;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;

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

    public static void createAvailabilities(ServiceProvider provider, ArrayList<Availability> availabilities, final AsyncActionEventListener listener){
        ArrayList<DbAvailability> dbAvail = new ArrayList<>();
        String serviceProviderKey = provider.getKey();
        for(Availability avail : availabilities){
            dbAvail.add(new DbAvailability(avail));
        }
        FirebaseDatabase.getInstance().getReference().child("availabilities").child(serviceProviderKey).setValue(
                dbAvail, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if(databaseError == null){
                            listener.onSuccess();
                        }
                        else{
                            listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
                        }
                    }
                }
        );

    }

    public static void getAvailabilities(ServiceProvider provider, final AsyncValueEventListener<Availability> listener){
        final ArrayList<Availability> returnValue = new ArrayList<>();
        String providerKey = provider.getKey();
        FirebaseDatabase.getInstance().getReference().child("availabilities").child(providerKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    DbAvailability currentDb = snap.getValue(DbAvailability.class);
                    Availability current = currentDb.toDomainObj();
                    returnValue.add(current);
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

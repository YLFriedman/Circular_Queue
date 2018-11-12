package ca.uottawa.seg2105.project.cqondemand.utilities;

import android.support.annotation.NonNull;

import java.util.ArrayList;

public interface AsyncSingleValueEventListener<E> {

    public void onSuccess(@NonNull E item);

    public void onFailure(@NonNull AsyncEventFailureReason reason);

}

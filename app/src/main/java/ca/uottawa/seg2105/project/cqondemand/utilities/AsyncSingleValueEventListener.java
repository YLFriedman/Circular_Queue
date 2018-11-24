package ca.uottawa.seg2105.project.cqondemand.utilities;

import androidx.annotation.NonNull;

public interface AsyncSingleValueEventListener<E> {

    public void onSuccess(@NonNull E item);

    public void onFailure(@NonNull AsyncEventFailureReason reason);

}

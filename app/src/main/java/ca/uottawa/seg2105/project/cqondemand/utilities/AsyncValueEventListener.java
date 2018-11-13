package ca.uottawa.seg2105.project.cqondemand.utilities;

import android.support.annotation.NonNull;

import java.util.ArrayList;

public interface AsyncValueEventListener<E> {

    public void onSuccess(@NonNull ArrayList<E> data);

    public void onFailure(@NonNull AsyncEventFailureReason reason);

}

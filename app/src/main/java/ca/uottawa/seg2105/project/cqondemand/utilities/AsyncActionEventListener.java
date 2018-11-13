package ca.uottawa.seg2105.project.cqondemand.utilities;

import android.support.annotation.NonNull;

public interface AsyncActionEventListener {

    public void onSuccess();

    public void onFailure(@NonNull AsyncEventFailureReason reason);

}

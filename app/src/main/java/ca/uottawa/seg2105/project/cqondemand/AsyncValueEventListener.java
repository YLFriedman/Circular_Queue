package ca.uottawa.seg2105.project.cqondemand;

import java.util.ArrayList;

public interface AsyncValueEventListener<E> {

    public void onSuccess(ArrayList<E> data);

    public void onFailure(AsyncEventFailureReason reason);

}
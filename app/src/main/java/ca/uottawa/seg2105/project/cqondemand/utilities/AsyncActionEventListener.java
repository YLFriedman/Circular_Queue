package ca.uottawa.seg2105.project.cqondemand.utilities;

public interface AsyncActionEventListener {

    public void onSuccess();

    public void onFailure(AsyncEventFailureReason reason);

}

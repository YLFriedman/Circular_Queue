package ca.uottawa.seg2105.project.cqondemand;

public interface AsyncActionEventListener {

    public void onSuccess();

    public void onFailure(AsyncEventFailureReason reason);

}

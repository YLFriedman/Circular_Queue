package ca.uottawa.seg2105.project.cqondemand;

public interface UserEventListener {

    public void onSuccess();

    public void onFailure(DatabaseUtil.CallbackFailure reason);

}

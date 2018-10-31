package ca.uottawa.seg2105.project.cqondemand;

public interface DbActionEventListener {

    public void onSuccess();

    public void onFailure(DbEventFailureReason reason);

}

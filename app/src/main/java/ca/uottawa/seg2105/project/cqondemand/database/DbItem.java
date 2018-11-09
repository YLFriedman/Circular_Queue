package ca.uottawa.seg2105.project.cqondemand.database;

public abstract class DbItem<T> {

    public abstract T toItem();

    public abstract String generateKey();

}

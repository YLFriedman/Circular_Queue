package ca.uottawa.seg2105.project.cqondemand.database;

public abstract class DbItem<T> {

    public abstract T toDomainObj();

    public abstract String generateKey();

}

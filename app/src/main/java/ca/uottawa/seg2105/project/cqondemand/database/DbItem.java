package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;

public abstract class DbItem<T> {

    public abstract @NonNull T toDomainObj();

    public abstract @NonNull String generateKey();

}

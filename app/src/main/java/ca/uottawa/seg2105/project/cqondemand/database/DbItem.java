package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;

public abstract class DbItem<T> {

    protected String key;

    public abstract @NonNull T toDomainObj();

    public @NonNull String generateKey() {
        return key;
    }

    public void storeKey(@NonNull String key) {
        this.key = key;
    }

}

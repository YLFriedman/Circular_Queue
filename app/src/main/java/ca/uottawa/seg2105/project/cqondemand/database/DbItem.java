package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;

abstract class DbItem<T> {

    private String key;

    DbItem() { }

    DbItem(String key) {
        this.key = key;
    }

    abstract @NonNull T toDomainObj();

    void storeKey(@NonNull String key) {
        this.key = key;
    }

    String retrieveKey() {
        return key;
    }

}

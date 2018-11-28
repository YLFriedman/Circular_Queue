package ca.uottawa.seg2105.project.cqondemand.database;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

abstract class DbItem<T> {

    @Exclude
    public String key;

    DbItem() { }

    DbItem(String key) {
        this.key = key;
    }

    abstract @NonNull T toDomainObj();

    @Exclude
    void setKey(@NonNull String key) {
        this.key = key;
    }

    @Exclude
    String getKey() {
        return key;
    }

}

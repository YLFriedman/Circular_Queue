package ca.uottawa.seg2105.project.cqondemand.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class DbQuery {

    private enum OrderBy { CHILD, KEY, VALUE}
    enum Limit { FIRST, LAST }
    private enum Filter { RANGE, START, END, EQUALS }
    private enum FilterType { STRING, DOUBLE, BOOLEAN }

    private OrderBy order;
    private String keyName;
    private Limit limit;
    private int limitNum;
    private Filter filter;
    private FilterType filterType;
    private Object startAt;
    private Object endAt;
    private Object equals;

    private DbQuery() {}

    Query apply(DatabaseReference ref) {
        Query query = null;

        // Set the order
        switch (order) {
            case CHILD: query = ref.orderByChild(keyName); break;
            case VALUE: query = ref.orderByValue(); break;
            case KEY:
            default: query = ref.orderByKey();
        }

        // Set the filters
        if (null != filter) {
            switch (filter) {
                case RANGE:
                    if (FilterType.STRING == filterType) { query = query.startAt((String)startAt).endAt((String)endAt); }
                    else if (FilterType.DOUBLE == filterType) { query = query.startAt((Double)startAt).endAt((Double)endAt); }
                    break;
                case START:
                    if (FilterType.STRING == filterType) { query = query.startAt((String)startAt); }
                    else if (FilterType.DOUBLE == filterType) { query = query.startAt((Double)startAt); }
                    break;
                case END:
                    if (FilterType.STRING == filterType) { query = query.endAt((String)startAt); }
                    else if (FilterType.DOUBLE == filterType) { query = query.endAt((Double)startAt); }
                    break;
                case EQUALS:
                    if (FilterType.STRING == filterType) { query = query.equalTo((String)equals); }
                    else if (FilterType.DOUBLE == filterType) { query = query.equalTo((Double)equals); }
                    else if (FilterType.BOOLEAN == filterType) { query = query.equalTo((Boolean)equals); }
                    break;
            }
        }

        // Set limit of the query
        if (Limit.FIRST == limit) { query = query.limitToFirst(limitNum); }
        else if (Limit.LAST == limit) { query = query.limitToLast(limitNum); }

        return query;
    }


    DbQuery setLimit(Limit limit, int limitNum) {
        this.limit = limit;
        this.limitNum = limitNum;
        return this;
    }


    DbQuery setRangeFilter(String startValue, String endValue) {
        filter = Filter.RANGE;
        filterType = FilterType.STRING;
        this.startAt = startValue;
        this.endAt = endValue;
        return this;
    }

    DbQuery setRangeFilter(double startValue, double endValue) {
        filter = Filter.RANGE;
        filterType = FilterType.DOUBLE;
        this.startAt = startValue;
        this.endAt = endValue;
        return this;
    }

    DbQuery setRangeFilter(int startValue, int endValue) {
        return setRangeFilter((double) startValue, (double) endValue);
    }

    DbQuery setRangeFilter(long startValue, long endValue) {
        return setRangeFilter((double) startValue, (double) endValue);
    }


    DbQuery setStartAtFilter(String value) {
        filter = Filter.START;
        filterType = FilterType.STRING;
        this.startAt = value;
        return this;
    }

    DbQuery setStartAtFilter(double value) {
        filter = Filter.START;
        filterType = FilterType.DOUBLE;
        this.startAt = value;
        return this;
    }

    DbQuery setStartAtFilter(int value) {
        return setStartAtFilter((double) value);
    }

    DbQuery setStartAtFilter(long value) {
        return setStartAtFilter((double) value);
    }


    DbQuery setEndAtFilter(String value) {
        filter = Filter.END;
        this.endAt = value;
        return this;
    }

    DbQuery setEndAtFilter(double value) {
        filter = Filter.END;
        filterType = FilterType.DOUBLE;
        this.endAt = value;
        return this;
    }

    DbQuery setEndAtFilter(int value) {
        return setEndAtFilter((double) value);
    }

    DbQuery setEndAtFilter(long value) {
        return setEndAtFilter((double) value);
    }


    DbQuery setEqualsFilter(String value) {
        filter = Filter.EQUALS;
        filterType = FilterType.STRING;
        this.equals = value;
        return this;
    }

    DbQuery setEqualsFilter(double value) {
        filter = Filter.EQUALS;
        filterType = FilterType.DOUBLE;
        this.equals = value;
        return this;
    }

    DbQuery setEqualsFilter(int value) {
        return setEqualsFilter((double) value);
    }

    DbQuery setEqualsFilter(long value) {
        return setEqualsFilter((double) value);
    }

    DbQuery setEqualsFilter(boolean value) {
        filter = Filter.EQUALS;
        filterType = FilterType.BOOLEAN;
        this.equals = value;
        return this;
    }


    static DbQuery createChildValueQuery(String childKeyName) {
        DbQuery query = new DbQuery();
        query.order = OrderBy.CHILD;
        query.keyName = childKeyName;
        return query;
    }

    static DbQuery createKeyQuery() {
        DbQuery query = new DbQuery();
        query.order = OrderBy.KEY;
        return query;
    }

    static DbQuery createValueQuery() {
        DbQuery query = new DbQuery();
        query.order = OrderBy.VALUE;
        return query;
    }

}

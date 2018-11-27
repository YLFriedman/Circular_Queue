package ca.uottawa.seg2105.project.cqondemand.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class DbQuery {

    protected enum OrderBy { CHILD, KEY, VALUE}
    enum Limit { FIRST, LAST }
    protected enum Filter { RANGE, START, END, EQUALS }
    protected enum FilterType { STRING, DOUBLE, BOOLEAN }

    protected OrderBy order;
    protected String keyName;
    protected Limit limit;
    protected int limitNum;
    protected Filter filter;
    protected FilterType filterType;
    protected Object startAt;
    protected Object endAt;
    protected Object equals;


    protected DbQuery() {}

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


    void setLimit(Limit limit, int limitNum) {
        this.limit = limit;
        this.limitNum = limitNum;
    }


    void setRangeFilter(String startValue, String endValue) {
        filter = Filter.RANGE;
        filterType = FilterType.STRING;
        this.startAt = startValue;
        this.endAt = endValue;
    }

    void setRangeFilter(int startValue, int endValue) {
        filter = Filter.RANGE;
        filterType = FilterType.DOUBLE;
        this.startAt = (double) startValue;
        this.endAt = (double) endValue;
    }

    void setRangeFilter(long startValue, long endValue) {
        filter = Filter.RANGE;
        filterType = FilterType.DOUBLE;
        this.startAt = (double) startValue;
        this.endAt = (double) endValue;
    }

    void setRangeFilter(double startValue, double endValue) {
        filter = Filter.RANGE;
        filterType = FilterType.DOUBLE;
        this.startAt = startValue;
        this.endAt = endValue;
    }


    void setStartAtFilter(String value) {
        filter = Filter.START;
        filterType = FilterType.STRING;
        this.startAt = value;
    }

    void setStartAtFilter(int value) {
        filter = Filter.START;
        filterType = FilterType.DOUBLE;
        this.startAt = (double) value;
    }

    void setStartAtFilter(long value) {
        filter = Filter.START;
        filterType = FilterType.DOUBLE;
        this.startAt = (double) value;
    }

    void setStartAtFilter(double value) {
        filter = Filter.START;
        filterType = FilterType.DOUBLE;
        this.startAt = value;
    }


    void setEndAtFilter(String value) {
        filter = Filter.END;
        this.endAt = value;
    }

    void setEndAtFilter(int value) {
        filter = Filter.END;
        filterType = FilterType.DOUBLE;
        this.endAt = (double) value;
    }

    void setEndAtFilter(long value) {
        filter = Filter.END;
        filterType = FilterType.DOUBLE;
        this.endAt = (double) value;
    }

    void setEndAtFilter(double value) {
        filter = Filter.END;
        filterType = FilterType.DOUBLE;
        this.endAt = value;
    }


    void setEqualsFilter(String value) {
        filter = Filter.EQUALS;
        filterType = FilterType.STRING;
        this.equals = value;
    }

    void setEqualsFilter(int value) {
        filter = Filter.EQUALS;
        filterType = FilterType.DOUBLE;
        this.equals = (double) value;
    }

    void setEqualsFilter(long value) {
        filter = Filter.EQUALS;
        filterType = FilterType.DOUBLE;
        this.equals = (double) value;
    }

    void setEqualsFilter(double value) {
        filter = Filter.EQUALS;
        filterType = FilterType.DOUBLE;
        this.equals = value;
    }

    void setEqualsFilter(boolean value) {
        filter = Filter.EQUALS;
        filterType = FilterType.BOOLEAN;
        this.equals = value;
    }


    static class Factory {

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

        static DbQuery createValueQuery(String keyName) {
            DbQuery query = new DbQuery();
            query.order = OrderBy.VALUE;
            query.keyName = keyName;
            return query;
        }

    }

}

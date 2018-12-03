package ca.uottawa.seg2105.project.cqondemand.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * The class <b> DbQuery </b> is used to filter data from the database based on a set of different
 * filter types
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class DbQuery {

    /**
     * Enum that lists the ways in which Db Information can be ordered by
     */
    private enum OrderBy { CHILD, KEY, VALUE}

    /**
     * Enum that defines the two ways data can be limited (first and last)
     */
    enum Limit { FIRST, LAST }

    /**
     * Enum that defines the different filter types that can belong to a DbQuery
     */
    private enum Filter { RANGE, START, END, EQUALS }

    /**
     * Enum that defines the Type of object that is being filtered
     */
    private enum FilterType { STRING, DOUBLE, BOOLEAN }

    /**
     * The way a DbQuery is ordering data (by child, key, or value)
     */
    private OrderBy order;

    /**
     * The child key name
     */
    private String keyName;

    /**
     * Sets the limit type (i.e. first or last)
     */
    private Limit limit;

    /**
     * Defines how many objects will be returned by this query
     */
    private int limitNum;

    /**
     * The filter mode associated with a DbQuery
     */
    private Filter filter;

    /**
     * The type of the objects that are being filtered
     */
    private FilterType filterType;

    /**
     * The object this query should begin at
     */
    private Object startAt;

    /**The object this query should end at
     *
     */
    private Object endAt;

    /**
     *For filter of type EQUALS, the object which should be equalled
     */
    private Object equals;

    /**
     * Empty private constructor. DbQueries are built by successive method calls
     */
    private DbQuery() {}

    /**
     * Applies a DbQuery to a specific location in the database
     *
     * @param ref the location to be queried
     * @return a Query based on the specifications of this DbQuery
     */
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

    /**
     * Limits the number of objects pulled from the database, and whether you want the first (limitNum)
     * of objects or the last (limitNum) of objects
     *
     * @param limit limit type; first or last
     * @param limitNum the number of objects to limit this DbQuery to
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setLimit(Limit limit, int limitNum) {
        this.limit = limit;
        this.limitNum = limitNum;
        return this;
    }

    /**
     * Sets a DbQuery to have a specified starting point and ending point, based on string values
     * @param startValue the starting point
     * @param endValue the ending point
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setRangeFilter(String startValue, String endValue) {
        filter = Filter.RANGE;
        filterType = FilterType.STRING;
        this.startAt = startValue;
        this.endAt = endValue;
        return this;
    }

    /**
     * Sets a DbQuery to have a specified starting point and ending point, based on double values
     * @param startValue the starting point
     * @param endValue the ending point
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setRangeFilter(double startValue, double endValue) {
        filter = Filter.RANGE;
        filterType = FilterType.DOUBLE;
        this.startAt = startValue;
        this.endAt = endValue;
        return this;
    }

    /**
     * Sets a DbQuery to have a specified starting point and ending point, based on int values
     * @param startValue the starting point
     * @param endValue the ending point
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setRangeFilter(int startValue, int endValue) {
        return setRangeFilter((double) startValue, (double) endValue);
    }

    /**
     * Sets a DbQuery to have a specified starting point and ending point, based on long values
     * @param startValue the starting point
     * @param endValue the ending point
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setRangeFilter(long startValue, long endValue) {
        return setRangeFilter((double) startValue, (double) endValue);
    }

    /**
     * Sets the place a DbQuery should start at, based on a string value
     * @param value the value to start at
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setStartAtFilter(String value) {
        filter = Filter.START;
        filterType = FilterType.STRING;
        this.startAt = value;
        return this;
    }

    /**
     * Sets the place a DbQuery should start at, based on a double value
     * @param value the value to start at
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setStartAtFilter(double value) {
        filter = Filter.START;
        filterType = FilterType.DOUBLE;
        this.startAt = value;
        return this;
    }

    /**
     * Sets the place a DbQuery should start at, based on an int value
     * @param value the value to start at
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setStartAtFilter(int value) {
        return setStartAtFilter((double) value);
    }

    /**
     * Sets the place a DbQuery should start at, based on a long value
     * @param value the value to start at
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setStartAtFilter(long value) {
        return setStartAtFilter((double) value);
    }

    /**
     * Sets the place a DbQuery should end at, based on a string value
     * @param value the value to end at
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setEndAtFilter(String value) {
        filter = Filter.END;
        this.endAt = value;
        return this;
    }

    /**
     * Sets the place a DbQuery should end at, based on a double value
     * @param value the value to end at
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setEndAtFilter(double value) {
        filter = Filter.END;
        filterType = FilterType.DOUBLE;
        this.endAt = value;
        return this;
    }

    /**
     * Sets the place a DbQuery should end at, based on an int value
     * @param value the value to end at
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setEndAtFilter(int value) {
        return setEndAtFilter((double) value);
    }

    /**
     * Sets the place a DbQuery should end at, based on a long value
     * @param value the value to end at
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setEndAtFilter(long value) {
        return setEndAtFilter((double) value);
    }

    /**
     * Sets the filter type to EQUALS, and specifies the value to be searched for. Based on String value
     *
     * @param value the value to be searched for
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setEqualsFilter(String value) {
        filter = Filter.EQUALS;
        filterType = FilterType.STRING;
        this.equals = value;
        return this;
    }

    /**
     * Sets the filter type to EQUALS, and specifies the value to be searched for. Based on double value
     *
     * @param value the value to be searched for
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setEqualsFilter(double value) {
        filter = Filter.EQUALS;
        filterType = FilterType.DOUBLE;
        this.equals = value;
        return this;
    }

    /**
     * Sets the filter type to EQUALS, and specifies the value to be searched for. Based on int value
     *
     * @param value the value to be searched for
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setEqualsFilter(int value) {
        return setEqualsFilter((double) value);
    }

    /**
     * Sets the filter type to EQUALS, and specifies the value to be searched for. Based on long value
     *
     * @param value the value to be searched for
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setEqualsFilter(long value) {
        return setEqualsFilter((double) value);
    }

    /**
     * Sets the filter type to EQUALS, and specifies the value to be searched for. Based on boolean value
     *
     * @param value the value to be searched for
     * @return this DbQuery, now with the specified properties
     */
    DbQuery setEqualsFilter(boolean value) {
        filter = Filter.EQUALS;
        filterType = FilterType.BOOLEAN;
        this.equals = value;
        return this;
    }

    /**
     * Sets the order type to CHILD, and specifies the name of the child key to order by
     * @param childKeyName the name of the child key
     * @return this DbQuery, now with the specified properties
     */
    static DbQuery createChildValueQuery(String childKeyName) {
        DbQuery query = new DbQuery();
        query.order = OrderBy.CHILD;
        query.keyName = childKeyName;
        return query;
    }

    /**
     * Sets the order type to KEY
     *
     * @return this DbQuery, now with the specified properties
     */
    static DbQuery createKeyQuery() {
        DbQuery query = new DbQuery();
        query.order = OrderBy.KEY;
        return query;
    }

    /**
     * Sets the order type to VALUE
     *
     * @return this DbQuery, now with the specified properties
     */
    static DbQuery createValueQuery() {
        DbQuery query = new DbQuery();
        query.order = OrderBy.VALUE;
        return query;
    }

}

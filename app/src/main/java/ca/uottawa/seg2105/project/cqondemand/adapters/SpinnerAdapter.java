package ca.uottawa.seg2105.project.cqondemand.adapters;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ca.uottawa.seg2105.project.cqondemand.R;

public class SpinnerAdapter<T> extends ArrayAdapter<T> {

    protected final LayoutInflater mInflater;
    protected LayoutInflater mDropDownInflater;
    protected final Context mContext;
    protected final int mResource;
    protected int mDropDownResource;
    protected String emptyItemText = "<Select an Item>";

    public SpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull T[] objects) {
        super(context, resource, objects);
        mContext = context;
        mDropDownInflater = mInflater = LayoutInflater.from(context);
        mResource = mDropDownResource = resource;
    }

    public SpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
        mContext = context;
        mDropDownInflater = mInflater = LayoutInflater.from(context);
        mResource = mDropDownResource = resource;
    }

    public SpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull String emptyItemText, @NonNull T[] objects) {
        this(context, resource, objects);
        this.emptyItemText = emptyItemText;
    }

    public SpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull String emptyItemText, @NonNull List<T> objects) {
        this(context, resource, objects);
        this.emptyItemText = emptyItemText;
    }

    @Override
    public boolean isEnabled(int position) {
        return null != getItem(position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = mDropDownInflater == null ? mInflater : mDropDownInflater;
        View view = createViewFromResource(inflater, position, convertView, parent);
        if (!isEnabled(position)) {
            ((TextView) view).setTextColor(getContext().getResources().getColor(R.color.text_secondary_dark));
        }
        return view;
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(mInflater, position, convertView, parent);
    }

    private @NonNull View createViewFromResource(@NonNull LayoutInflater inflater, int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View view;
        final TextView text;
        if (convertView == null) { view = inflater.inflate(mResource, parent, false); }
        else { view = convertView;  }
        // Assume the whole resource is a TextView
        text = (TextView) view;

        final T item = getItem(position);
        if (null == item) {
            text.setText(emptyItemText);
        } else if (item instanceof CharSequence) {
            text.setText((CharSequence) item);
        } else {
            text.setText(item.toString());
        }
        return view;
    }

    public void setEmptyItemText(String text) {
        emptyItemText = text;
    }

}

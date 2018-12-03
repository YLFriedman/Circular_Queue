package ca.uottawa.seg2105.project.cqondemand.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.R;

/**
 * An adapter class for the RecyclerView that contains a list of categories
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

    /**
     * The application context that the adapter is being used in
     */
    private Context context;
    /**
     * Stores the list of Category objects data
     */
    private ArrayList<Category> data;
    /**
     * Stores callback when a view is clicked
     */
    private View.OnClickListener clickListener;

    /**
     * Constructor for the BookingListAdapter, does not have an OnClickListener
     * @param context   Collection of relevant application data for the Category list adapter
     * @param data      ArrayList of category data to be adapted
     */
    public CategoryListAdapter(Context context, ArrayList<Category> data) {
        this.context = context;
        this.data = data;
    }

    /**
     * Constructor for the BookingListAdapter
     * @param context       Collection of relevant application data for the Category list adapter
     * @param data          ArrayList of category data to be adapted
     * @param clickListener Callback invoked when a view is clicked
     */
    public CategoryListAdapter(Context context, ArrayList<Category> data, View.OnClickListener clickListener) {
        this(context, data);
        this.clickListener = clickListener;
    }

    /**
     * Creates a new viewHolder when there are no existing viewHolders avaliable that the recycler can reuse
     * @param parent    The ViewGroup into which the new View will be added after it is bound to an adapter position
     * @param viewType  For different types of cells in list - not needed here
     * @return  new ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_list_title, parent, false);
        if (null != clickListener) { view.setOnClickListener(clickListener); }
        return new ViewHolder(view);
    }

    /**
     * For each recycler holder, populate recycler with data and update the viewholder
     * @param viewHolder    viewHolder to be updated
     * @param i Position index from where the data is to be received
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Category item = data.get(i);
        // Set the current item as the view's tag so it can be retrieve easily in the onClick handler
        viewHolder.itemView.setTag(item);
        if (null != item) {
            viewHolder.txt_title.setText(item.getName());
        }
        viewHolder.img_nav.setImageResource(R.drawable.ic_chevron_right_med_30);
    }

    /**
     * Get the total data size associated with the category list
     * @return size of data
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * ViewHolder class to be used with the recycler view
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_title;
        ImageView img_nav;

        /**
         * ViewHolder constructor
         * @param itemView  itemView data to be used
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
            img_nav = itemView.findViewById(R.id.img_nav);
        }

    }

}

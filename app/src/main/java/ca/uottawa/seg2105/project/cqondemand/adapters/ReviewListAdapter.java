package ca.uottawa.seg2105.project.cqondemand.adapters;

import android.view.ViewGroup;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;

/**
 * An adapter class for the RecyclerView that contains a list of reviews
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {

    /**
     * The application context that the adapter is being used in
     */
    private Context context;
    /**
     * Stores the list of Review objects data
     */
    private ArrayList<Review> data;
    /**
     * Stores callback when a view is clicked
     */
    private View.OnClickListener clickListener;

    /**
     * Constructor for the ReviewListAdapter, does not have an OnClickListener
     * @param context   Collection of relevant application data for the Review list adapter
     * @param data      ArrayList of category data to be adapted
     */
    public ReviewListAdapter(Context context, ArrayList<Review> data) {
        this.context = context;
        this.data = data;
    }

    /**
     * Constructor for the ReviewListAdapter
     * @param context       Collection of relevant application data for the Review list adapter
     * @param data          ArrayList of category data to be adapted
     * @param clickListener Callback invoked when a view is clicked
     */
    public ReviewListAdapter(Context context, ArrayList<Review> data, View.OnClickListener clickListener) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_list_service_provider_picker, parent, false);
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
        Review item = data.get(i);
        // Set the current item as the view's tag so it can be retrieve easily in the onClick handler
        viewHolder.itemView.setTag(item);
        if (null != item) {
            viewHolder.txt_title.setText(item.getServiceName());
            viewHolder.rating_stars.setRating(item.getRating());
            viewHolder.txt_subtitle.setText(String.format(context.getString(R.string.review_by_template), item.getReviewerName()));
        }
        viewHolder.img_nav.setImageResource(R.drawable.ic_chevron_right_med_30);
        viewHolder.img_item_image.setVisibility(View.GONE);
    }

    /**
     * Get the total data size associated with the review list
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
        TextView txt_subtitle;
        ImageView img_nav;
        ImageView img_item_image;
        RatingBar rating_stars;

        /**
         * ViewHolder constructor
         * @param itemView  itemView data to be used
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_subtitle = itemView.findViewById(R.id.txt_subtitle);
            txt_title = itemView.findViewById(R.id.txt_title);
            rating_stars = itemView.findViewById(R.id.rating_stars);
            img_nav = itemView.findViewById(R.id.img_nav);
            img_item_image = itemView.findViewById(R.id.img_item_image);
        }

    }

}

package ca.uottawa.seg2105.project.cqondemand.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;

/**
 * An adapter class for the RecyclerView contained in the service provider picker activity. Defines the layouts that
 * will be contained in the RecyclerView.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */

public class ServiceProviderPickerListAdapter extends RecyclerView.Adapter<ServiceProviderPickerListAdapter.ViewHolder> {

    /**
     * The application context that the adapter is being used in
     */
    private Context context;
    /**
     * Stores the list of Service provider objects data
     */
    private ArrayList<ServiceProvider> data;
    /**
     * Stores callback when a view is clicked
     */
    private View.OnClickListener clickListener;

    /**
     * Constructor for the ServiceProviderPickerListAdapter
     * @param context       Collection of relevant application data for the Service Provider Picker list adapter
     * @param data          ArrayList of Service Provider Picker data to be adapted
     */
    public ServiceProviderPickerListAdapter(Context context, ArrayList<ServiceProvider> data) {
        this.context = context;
        this.data = data;
    }

    /**
     * Constructor for the ServiceProviderPickerListAdapter
     * @param context       Collection of relevant application data for the Service Provider Picker list adapter
     * @param data          ArrayList of Service Provider Picker data to be adapted
     * @param clickListener Callback invoked when a view is clicked
     */
    public ServiceProviderPickerListAdapter(Context context, ArrayList<ServiceProvider> data, View.OnClickListener clickListener) {
        this.context = context;
        this.data = data;
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
        ServiceProvider item = data.get(i);
        // Set the current item as the view's tag so it can be retrieve easily in the onClick handler
        viewHolder.itemView.setTag(item);
        if (null != item) {
            viewHolder.txt_title.setText(item.getCompanyName());
            String numRatings;
            if (0 == item.getNumRatings()) { numRatings = context.getString(R.string.rating_template_none); }
            else if (1 == item.getNumRatings()) { numRatings = context.getString(R.string.rating_template_single); }
            else { numRatings = String.format(Locale.CANADA, context.getString(R.string.rating_template), item.getNumRatings()); }
            viewHolder.txt_subtitle.setText(numRatings);
            viewHolder.rating_stars.setRating((float)item.getRating() / 100);
        }
        //viewHolder.img_item_image.setImageResource(R.drawable.ic_account_circle_med_40);
        viewHolder.img_item_image.setVisibility(View.GONE);
        viewHolder.img_nav.setImageResource(R.drawable.ic_chevron_right_med_30);
    }

    /**
     * Get the total data size associated with the service provider picker list
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
        ImageView img_item_image;
        ImageView img_nav;
        RatingBar rating_stars;

        /**
         * ViewHolder constructor
         * @param itemView  itemView data to be used
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_subtitle = itemView.findViewById(R.id.txt_subtitle);
            img_item_image = itemView.findViewById(R.id.img_item_image);
            img_nav = itemView.findViewById(R.id.img_nav);
            rating_stars = itemView.findViewById(R.id.rating_stars);
        }

    }

}

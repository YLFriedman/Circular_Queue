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
import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

/**
 * An adapter class for the RecyclerView contained in the user home activity. Defines the layouts that
 * will be contained in the RecyclerView.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    /**
     * The application context that the adapter is being used in
     */
    private Context context;
    /**
     * Stores the list of User objects data
     */
    private ArrayList<User> data;
    /**
     * Stores callback when a view is clicked
     */
    private View.OnClickListener clickListener;

    /**
     * Constructor for the UserListAdapter
     * @param context       Collection of relevant application data for the User list adapter
     * @param data          ArrayList of User list  data to be adapted
     */
    public UserListAdapter(Context context, ArrayList<User> data) {
        this.context = context;
        this.data = data;
    }

    /**
     * Constructor for the UserListAdapter
     * @param context       Collection of relevant application data for the User list adapter
     * @param data          ArrayList of User list  data to be adapted
     * @param clickListener Callback invoked when a view is clicked
     */
    public UserListAdapter(Context context, ArrayList<User> data, View.OnClickListener clickListener) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_list_title_subtitle_icon, parent, false);
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
        User item = data.get(i);
        // Set the current item as the view's tag so it can be retrieve easily in the onClick handler
        viewHolder.itemView.setTag(item);
        if (null != item) {
            viewHolder.txt_title.setText(item.getFullName());
            viewHolder.txt_subtitle.setText(String.format(Locale.CANADA, "%s, %s", item.getUsername(), item.getType().toString()));
        }
        viewHolder.img_item_image.setImageResource(R.drawable.ic_account_circle_med_40);
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
        }

    }

}

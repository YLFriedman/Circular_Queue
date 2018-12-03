package ca.uottawa.seg2105.project.cqondemand.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Booking;

/**
 * An adapter class for the RecyclerView that contains a list of categories
 */

public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.ViewHolder> {

    protected Context context;
    protected ArrayList<Booking> data;
    protected View.OnClickListener clickListener;
    protected SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM d, yyyy", Locale.CANADA);
    protected SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h a", Locale.CANADA);

    public BookingListAdapter(Context context, ArrayList<Booking> data) {
        this.context = context;
        this.data = data;
    }

    public BookingListAdapter(Context context, ArrayList<Booking> data, View.OnClickListener clickListener) {
        this.context = context;
        this.data = data;
        this.clickListener = clickListener;
    }

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
        Booking item = data.get(i);
        // Set the current item as the view's tag so it can be retrieve easily in the onClick handler
        viewHolder.itemView.setTag(item);
        if (null != item) {
            viewHolder.txt_title.setText(item.getServiceName());

            DATE_FORMAT.format(new Date(item.getStartTime().getTime()));
            viewHolder.txt_subtitle.setText(String.format(
                    "%s  %s to %s",
                    DATE_FORMAT.format(new Date(item.getStartTime().getTime())),
                    TIME_FORMAT.format(new Date(item.getStartTime().getTime())),
                    TIME_FORMAT.format(new Date(item.getEndTime().getTime()))
            ));
        }
        switch (item.getStatus()) {
            case CANCELLED: viewHolder.img_item_image.setImageResource(R.drawable.ic_delete_med_24); break;
            case APPROVED: viewHolder.img_item_image.setImageResource(R.drawable.ic_event_available_green_24); break;
            case REQUESTED: viewHolder.img_item_image.setImageResource(R.drawable.ic_event_orange_24); break;
            case EXPIRED: viewHolder.img_item_image.setImageResource(R.drawable.ic_event_busy_red_24); break;
            case COMPLETED: viewHolder.img_item_image.setImageResource(R.drawable.ic_check_circle_green_24); break;
            default: viewHolder.img_item_image.setVisibility(View.GONE);
        }
        viewHolder.img_nav.setImageResource(R.drawable.ic_chevron_right_med_30);
    }

    /**
     * Get the total data size associated with the booking list
     * @return size of data
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * ViewHolder constructor to be used with the recycler view
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_title;
        TextView txt_subtitle;
        ImageView img_item_image;
        ImageView img_nav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_subtitle = itemView.findViewById(R.id.txt_subtitle);
            img_item_image = itemView.findViewById(R.id.img_item_image);
            img_nav = itemView.findViewById(R.id.img_nav);
        }

    }

}

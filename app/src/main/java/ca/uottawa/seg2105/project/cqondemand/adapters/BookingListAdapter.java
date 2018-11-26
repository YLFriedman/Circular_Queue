package ca.uottawa.seg2105.project.cqondemand.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    protected SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM d, yyyy");
    protected SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h a");

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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Booking item = data.get(i);
        // Set the current item as the view's tag so it can be retrieve easily in the onClick handler
        viewHolder.itemView.setTag(item);
        if (null != item) {
            viewHolder.txt_title.setText(item.getServiceName());

            DATE_FORMAT.format(new Date(item.getStartTime().getTime()));
            viewHolder.txt_subtitle.setText(String.format(
                    "%s  %s to $s",
                    DATE_FORMAT.format(new Date(item.getStartTime().getTime())),
                    TIME_FORMAT.format(new Date(item.getStartTime().getTime())),
                    TIME_FORMAT.format(new Date(item.getEndTime().getTime()))
            ));
        }
        //viewHolder.img_item_image.setImageResource(R.drawable.ic_account_circle_med_40);
        viewHolder.img_nav.setImageResource(R.drawable.ic_chevron_right_med_30);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

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

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
 * work in progress
 */

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Review> data;
    private View.OnClickListener clickListener;

    public ReviewListAdapter(Context context, ArrayList<Review> data) {
        this.context = context;
        this.data = data;
    }

    public ReviewListAdapter(Context context, ArrayList<Review> data, View.OnClickListener clickListener) {
        this(context, data);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_list_service_provider_picker, parent, false);
        if (null != clickListener) { view.setOnClickListener(clickListener); }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Review item = data.get(i);
        // Set the current item as the view's tag so it can be retrieve easily in the onClick handler
        viewHolder.itemView.setTag(item);
        if (null != item) {
            viewHolder.txt_title.setText(item.getReviewerName());
            viewHolder.rating_stars.setRating(item.getRating());
        }
        viewHolder.txt_subtitle.setVisibility(View.GONE);
        viewHolder.img_nav.setImageResource(R.drawable.ic_chevron_right_med_30);
        viewHolder.img_item_image.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_title;
        TextView txt_subtitle;
        ImageView img_nav;
        ImageView img_item_image;
        RatingBar rating_stars;

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

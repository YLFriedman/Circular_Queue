package ca.uottawa.seg2105.project.cqondemand.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;

/**
 * An adapter class for the RecyclerView contained in the user home activity. Defines the layouts that
 * will be contained in the RecyclerView.
 */

public class ServiceProviderPickerListAdapter extends RecyclerView.Adapter<ServiceProviderPickerListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ServiceProvider> data;
    private View.OnClickListener clickListener;

    public ServiceProviderPickerListAdapter(Context context, ArrayList<ServiceProvider> data) {
        this.context = context;
        this.data = data;
    }

    public ServiceProviderPickerListAdapter(Context context, ArrayList<ServiceProvider> data, View.OnClickListener clickListener) {
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
        ServiceProvider item = data.get(i);
        // Set the current item as the view's tag so it can be retrieve easily in the onClick handler
        viewHolder.itemView.setTag(item);
        if (null != item) {
            viewHolder.txt_title.setText(String.format(Locale.CANADA, context.getString(R.string.full_name_template), item.getFirstName(), item.getLastName()));
            viewHolder.txt_subtitle.setText(String.format(Locale.CANADA, "%s, %s", item.getUsername(), item.getType().toString()));
            viewHolder.txt_subtitle.setContentDescription(item.getUsername());
        }
        viewHolder.img_item_image.setImageResource(R.drawable.ic_account_circle_med_40);
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

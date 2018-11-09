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

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;

/**
 * An adapter class for the RecyclerView that contains a list of services
 */

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Service> data;
    private View.OnClickListener clickListener;

    public ServiceListAdapter(Context context, ArrayList<Service> data) {
        this.context = context;
        this.data = data;
    }

    public ServiceListAdapter(Context context, ArrayList<Service> data, View.OnClickListener clickListener) {
        this.context = context;
        this.data = data;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_list_title_subtitle, parent, false);
        if (null != clickListener) { view.setOnClickListener(clickListener); }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Service item = data.get(i);
        if (null != item) {
            viewHolder.txt_title.setText(item.getName());
            viewHolder.txt_subtitle.setText(String.format(context.getString(R.string.service_rate_template), item.getRate()));
        }
        viewHolder.img_nav.setImageResource(R.drawable.ic_chevron_right_med_30);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_title;
        TextView txt_subtitle;
        ImageView img_nav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_subtitle = itemView.findViewById(R.id.txt_subtitle);
            img_nav = itemView.findViewById(R.id.img_nav);
        }

    }

}

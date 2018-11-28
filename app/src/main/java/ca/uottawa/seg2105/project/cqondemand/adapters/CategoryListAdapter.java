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
 */

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Category> data;
    private View.OnClickListener clickListener;

    public CategoryListAdapter(Context context, ArrayList<Category> data) {
        this.context = context;
        this.data = data;
    }

    public CategoryListAdapter(Context context, ArrayList<Category> data, View.OnClickListener clickListener) {
        this(context, data);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_list_title, parent, false);
        if (null != clickListener) { view.setOnClickListener(clickListener); }
        return new ViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_title;
        ImageView img_nav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
            img_nav = itemView.findViewById(R.id.img_nav);
        }

    }

}

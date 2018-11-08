package ca.uottawa.seg2105.project.cqondemand.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        this.context = context;
        this.data = data;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_list_title, parent, false);
        if (null != clickListener) { view.setOnClickListener(clickListener); }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Category category = data.get(i);

        String categoryName = String.format("%s", category.getName());
        viewHolder.txt_title.setText(categoryName);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
        }

    }

}

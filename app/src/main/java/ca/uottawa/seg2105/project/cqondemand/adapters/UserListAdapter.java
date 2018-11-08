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
import ca.uottawa.seg2105.project.cqondemand.domain.User;

/**
 * An adapter class for the RecyclerView contained in the user home activity. Defines the layouts that
 * will be contained in the RecyclerView.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> data;
    private View.OnClickListener clickListener;

    public UserListAdapter(Context context, ArrayList<User> data) {
        this.context = context;
        this.data = data;
    }

    public UserListAdapter(Context context, ArrayList<User> data, View.OnClickListener clickListener) {
        this.context = context;
        this.data = data;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_user_list, parent, false);
        if (null != clickListener) { view.setOnClickListener(clickListener); }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        User user = data.get(i);
        String firstLine = String.format("%s %s", user.getFirstName(), user.getLastName());
        String secondLine = String.format("%s, %s", user.getUserName(), user.getType().toString());
        viewHolder.txt_name.setText(firstLine);
        viewHolder.txt_username_and_type.setText(secondLine);
        viewHolder.txt_username_and_type.setContentDescription(user.getUserName());
        viewHolder.img_avatar.setImageResource(R.drawable.ic_account_circle_med_40);
        viewHolder.img_nav.setImageResource(R.drawable.ic_chevron_right_med_30);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_name;
        TextView txt_username_and_type;
        ImageView img_avatar;
        ImageView img_nav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_username_and_type = itemView.findViewById(R.id.txt_username_and_type);
            img_avatar = itemView.findViewById(R.id.img_avatar);
            img_nav = itemView.findViewById(R.id.img_nav);
        }

    }

}

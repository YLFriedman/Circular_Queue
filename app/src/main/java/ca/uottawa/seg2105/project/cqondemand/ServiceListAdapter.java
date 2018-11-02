package ca.uottawa.seg2105.project.cqondemand;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

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
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_service_list, parent, false);
        if (null != clickListener) { view.setOnClickListener(clickListener); }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Service service = data.get(i);
        /*String firstLine = String.format("%s %s", user.getFirstName(), user.getLastName());
        String secondLine = String.format("%s, %s", user.getUserName(), user.getType().toString());
        userViewHolder.txt_name.setText(firstLine);
        userViewHolder.txt_username_and_type.setText(secondLine);
        userViewHolder.txt_username_and_type.setContentDescription(user.getUserName());
        userViewHolder.img_avatar.setImageResource(R.drawable.ic_account_circle_med_40);
        userViewHolder.img_nav.setImageResource(R.drawable.ic_chevron_right_med_30);*/
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        /*TextView txt_name;
        TextView txt_username_and_type;
        ImageView img_avatar;
        ImageView img_nav;*/

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            /*txt_name = itemView.findViewById(R.id.txt_name);
            txt_username_and_type = itemView.findViewById(R.id.txt_username_and_type);
            img_avatar = itemView.findViewById(R.id.img_avatar);
            img_nav = itemView.findViewById(R.id.img_nav);*/
        }

    }

}

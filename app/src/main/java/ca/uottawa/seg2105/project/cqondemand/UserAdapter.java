package ca.uottawa.seg2105.project.cqondemand;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * An adapter class for the RecyclerView contained in the user home activity. Defines the layouts that
 * will be contained in the RecyclerView.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context myContext;
    private ArrayList<User> userList;
    private View.OnClickListener clickListener = null;

    public UserAdapter(Context myContext, ArrayList<User> userList) {
        this.myContext = myContext;
        this.userList = userList;
    }

    public UserAdapter(Context myContext, ArrayList<User> userList, View.OnClickListener clickListener) {
        this.myContext = myContext;
        this.userList = userList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.userlist_layout, parent, false);
        if (null != clickListener) { view.setOnClickListener(clickListener); }
        UserViewHolder holder = new UserViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        User user = userList.get(i);
        String firstLine = String.format("%s %s", user.getFirstName(), user.getLastName());
        String secondLine = String.format("%s, %s", user.getUserName(), user.getType().toString());
        userViewHolder.txt_name.setText(firstLine);
        userViewHolder.txt_username_and_type.setText(secondLine);
        userViewHolder.txt_username_and_type.setContentDescription(user.getUserName());
        userViewHolder.img_avatar.setImageResource(R.drawable.ic_account_circle_gray_40);
        userViewHolder.img_nav.setImageResource(R.drawable.ic_chevron_right_gray_30);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        TextView txt_name;
        TextView txt_username_and_type;
        ImageView img_avatar;
        ImageView img_nav;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_username_and_type = itemView.findViewById(R.id.txt_username_and_type);
            img_avatar = itemView.findViewById(R.id.img_avatar);
            img_nav = itemView.findViewById(R.id.img_nav);
        }

    }

}

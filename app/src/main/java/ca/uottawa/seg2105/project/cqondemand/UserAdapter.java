package ca.uottawa.seg2105.project.cqondemand;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * An adapter class for the RecyclerView contained in the user home activity. Defines the layouts that
 * will be contained in the RecyclerView.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context myContext;
    private ArrayList<User> userList;

    public UserAdapter(Context myContext, ArrayList<User> userList) {
        this.myContext = myContext;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(myContext);
        View view = inflater.inflate(R.layout.userlist_layout, null);
        UserViewHolder holder =  new UserViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        User user = userList.get(i);
        String firstLine = String.format("%s %s", user.getFirstName(), user.getLastName());
        String secondLine = String.format("%s, %s", user.getUserName(), user.getType().toString());
        userViewHolder.fullname.setText(firstLine);
        userViewHolder.usernameAndType.setText(secondLine);


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        TextView fullname;
        TextView usernameAndType;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            fullname = itemView.findViewById(R.id.fullname);
            usernameAndType = itemView.findViewById(R.id.usernameAndType);
        }
    }


}
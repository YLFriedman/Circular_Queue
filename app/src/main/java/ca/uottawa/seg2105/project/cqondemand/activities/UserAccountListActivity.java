package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.adapters.UserListAdapter;

public class UserAccountListActivity extends AppCompatActivity {

    private RecyclerView user_list;
    private UserListAdapter user_list_adapter;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_list);
        user_list = findViewById(R.id.user_list);
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser = State.getState().getCurrentUser();
        if (null == currentUser) {
            finish();
        } else {
            if (currentUser.getType() == User.Types.ADMIN) {
                user_list.setHasFixedSize(true);
                user_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                User.getUsers(new AsyncValueEventListener<User>() {
                    @Override
                    public void onSuccess(ArrayList<User> data) {
                        if (null != data && data.size() > 0) {
                            user_list_adapter = new UserListAdapter(getApplicationContext(), data, new View.OnClickListener() {
                                public void onClick(final View view) {
                                    TextView field = view.findViewById(R.id.txt_username_and_type);
                                    Intent intent = new Intent(getApplicationContext(), UserAccountViewActivity.class);
                                    intent.putExtra("username", field.getContentDescription());
                                    startActivity(intent);
                                }
                            });
                            user_list.setAdapter(user_list_adapter);
                        }
                    }
                    @Override
                    public void onFailure(AsyncEventFailureReason reason) {

                    }
                });
            }
        }
    }

}

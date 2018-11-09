package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.adapters.UserListAdapter;

public class UserAccountListActivity extends AppCompatActivity {

    private RecyclerView recycler_list;
    private UserListAdapter user_list_adapter;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_list);
        recycler_list = findViewById(R.id.recycler_list);
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser = State.getState().getSignedInUser();
        if (null == currentUser) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            if (currentUser.getType() == User.Types.ADMIN) {
                recycler_list.setHasFixedSize(true);
                recycler_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                User.getUsers(new AsyncValueEventListener<User>() {
                    @Override
                    public void onSuccess(ArrayList<User> data) {
                        if (null != data) {
                            user_list_adapter = new UserListAdapter(getApplicationContext(), data, new View.OnClickListener() {
                                public void onClick(final View view) {
                                    TextView field = view.findViewById(R.id.txt_subtitle);
                                    Intent intent = new Intent(getApplicationContext(), UserAccountViewActivity.class);
                                    intent.putExtra("username", field.getContentDescription());
                                    startActivity(intent);
                                }
                            });
                            recycler_list.setAdapter(user_list_adapter);
                        }
                    }
                    @Override
                    public void onFailure(AsyncEventFailureReason reason) {
                        Toast.makeText(getApplicationContext(), "There was an error getting the users from the database. Please try again later.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

}

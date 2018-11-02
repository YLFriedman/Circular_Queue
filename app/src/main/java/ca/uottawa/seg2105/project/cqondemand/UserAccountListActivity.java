package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class UserAccountListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_list);
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser = State.getState().getCurrentUser();
        if (null == currentUser) {
            finish();
        } else {
            if (currentUser.getType() == User.Types.ADMIN) {
                recyclerView = (RecyclerView) findViewById(R.id.user_list);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                DbUtil.getUsers(new DbValueEventListener<User>() {
                    @Override
                    public void onSuccess(ArrayList<User> data) {
                        if (null != data && data.size() > 0) {
                            adapter = new UserAdapter(getApplicationContext(), data, new View.OnClickListener() {
                                public void onClick(final View view) {
                                    TextView field = view.findViewById(R.id.txt_username_and_type);
                                    Intent intent = new Intent(getApplicationContext(), UserAccountViewActivity.class);
                                    intent.putExtra("username", field.getContentDescription());
                                    startActivity(intent);
                                }
                            });
                            recyclerView.setAdapter(adapter);
                        }
                    }
                    @Override
                    public void onFailure(DbEventFailureReason reason) {

                    }
                });
            }
        }
    }

}

package ca.uottawa.seg2105.project.cqondemand;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
        currentUser = DbUtil.getCurrentUser();
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
                            adapter = new UserAdapter(getApplicationContext(), data);
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

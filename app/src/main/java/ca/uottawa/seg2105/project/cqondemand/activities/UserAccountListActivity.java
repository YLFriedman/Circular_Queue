package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.database.DbListener;
import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.adapters.UserListAdapter;

public class UserAccountListActivity extends SignedInActivity {

    private RecyclerView recycler_list;
    DbListener<?> listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_list);
        recycler_list = findViewById(R.id.recycler_list);
        if (!State.getState().getSignedInUser().isAdmin()) { finish(); }
        recycler_list.setHasFixedSize(true);
        recycler_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listener = DbUser.getUsersLive(new AsyncValueEventListener<User>() {
            @Override
            public void onSuccess(@NonNull ArrayList<User> data) {
                //user_list_adapter.notifyItemRangeRemoved(0, user_list_adapter.getItemCount());
                recycler_list.setAdapter(new UserListAdapter(getApplicationContext(), data, new View.OnClickListener() {
                    public void onClick(final View view) {
                        TextView field = view.findViewById(R.id.txt_subtitle);
                        Intent intent = new Intent(getApplicationContext(), UserAccountViewActivity.class);
                        intent.putExtra("username", field.getContentDescription());
                        startActivity(intent);
                    }
                }));
            }
            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                Toast.makeText(getApplicationContext(), R.string.user_list_db_error, Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup the data listener for the users list
        if (null != listener) { listener.removeListener(); }
    }

}

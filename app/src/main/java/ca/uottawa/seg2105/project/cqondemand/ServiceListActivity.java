package ca.uottawa.seg2105.project.cqondemand;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class ServiceListActivity extends AppCompatActivity {

    private User currentUser;

    private RecyclerView service_list;
    private ServiceListAdapter service_list_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        service_list = findViewById(R.id.service_list);
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser = State.getState().getCurrentUser();
        if (null == currentUser) {
            finish();
        } else {
            service_list.setHasFixedSize(true);
            service_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        }
    }

}

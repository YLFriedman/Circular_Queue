package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.adapters.ServiceListAdapter;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class ServiceListActivity extends AppCompatActivity {

    private User currentUser;

    private RecyclerView service_list;
    private ServiceListAdapter service_list_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        service_list = findViewById(R.id.recycler_list);
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser = State.getState().getSignedInUser();
        if (null == currentUser) {
            finish();
        } else {
            service_list.setHasFixedSize(true);
            service_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        }
    }

    public void onCreateCategoryClick() {
        startActivity(new Intent(getApplicationContext(), CategoryCreateActivity.class));
    }

    public void onCreateServiceClick() {
        startActivity(new Intent(getApplicationContext(), ServiceCreateActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.service_list_options, menu);
        if (currentUser.getType() != User.Types.ADMIN) {
            menu.setGroupVisible(R.id.grp_category_create_controls, false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_category_create: onCreateCategoryClick(); return true;
            case R.id.menu_item_service_create: onCreateServiceClick(); return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

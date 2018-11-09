package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.adapters.ServiceListAdapter;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class ServiceListActivity extends AppCompatActivity {

    private RecyclerView recycler_list;
    private ServiceListAdapter service_list_adapter;
    private TextView txt_category_name;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);
        recycler_list = findViewById(R.id.recycler_list);
        txt_category_name = findViewById(R.id.txt_category_name);
        Intent intent = getIntent();
        categoryName = intent.getStringExtra("category_name");
        if (null != categoryName) {
            txt_category_name.setText(String.format(getString(R.string.category_template), categoryName));
        } else {
            txt_category_name.setVisibility(View.GONE);
            findViewById(R.id.divider_category_name).setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null == State.getState().getSignedInUser()) {
            finish();
        } else {
            recycler_list.setHasFixedSize(true);
            recycler_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            AsyncValueEventListener<Service> listener = new AsyncValueEventListener<Service>() {
                @Override
                public void onSuccess(ArrayList<Service> data) {
                    if (null != data && data.size() > 0) {
                        service_list_adapter = new ServiceListAdapter(getApplicationContext(), data, new View.OnClickListener() {
                            public void onClick(final View view) {
                                TextView field = view.findViewById(R.id.txt_title);
                                Intent intent = new Intent(getApplicationContext(), ServiceViewActivity.class);
                                intent.putExtra("service_name", field.getText().toString());
                                startActivity(intent);
                            }
                        });
                        recycler_list.setAdapter(service_list_adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "No services found.", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(AsyncEventFailureReason reason) {
                    Toast.makeText(getApplicationContext(), "There was an error getting the services from the database. Please try again later.", Toast.LENGTH_LONG).show();
                }
            };
            if (categoryName == null) {
                Service.getServices(listener);
            } else {
                Service.getServices(categoryName, listener);
            }
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
        if (State.getState().getSignedInUser().getType() != User.Types.ADMIN) {
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

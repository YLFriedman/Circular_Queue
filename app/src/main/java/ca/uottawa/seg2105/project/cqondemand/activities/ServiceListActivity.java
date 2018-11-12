package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.adapters.ServiceListAdapter;
import ca.uottawa.seg2105.project.cqondemand.database.DbCategory;
import ca.uottawa.seg2105.project.cqondemand.database.DbService;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class ServiceListActivity extends SignedInActivity {

    private RecyclerView recycler_list;
    private ServiceListAdapter service_list_adapter;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);
        recycler_list = findViewById(R.id.recycler_list);
        TextView txt_category_name = findViewById(R.id.txt_category_name);
        Intent intent = getIntent();
        categoryName = intent.getStringExtra("category_name");
        if (null != categoryName) {
            txt_category_name.setText(String.format(Locale.CANADA, getString(R.string.category_title_template), categoryName));
        } else {
            txt_category_name.setVisibility(View.GONE);
            findViewById(R.id.divider_category_name).setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFinishing()) { return; }
        recycler_list.setHasFixedSize(true);
        recycler_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        AsyncValueEventListener<Service> listener = new AsyncValueEventListener<Service>() {
            @Override
            public void onSuccess(ArrayList<Service> data) {
                if (null != data) {
                    service_list_adapter = new ServiceListAdapter(getApplicationContext(), data, new View.OnClickListener() {
                        public void onClick(final View view) {
                            TextView field = view.findViewById(R.id.txt_title);
                            Intent intent = new Intent(getApplicationContext(), ServiceViewActivity.class);
                            intent.putExtra("service_name", field.getText().toString());
                            startActivity(intent);
                        }
                    });
                    recycler_list.setAdapter(service_list_adapter);
                }
            }
            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                Toast.makeText(getApplicationContext(), "There was an error getting the services from the database. Please try again later.", Toast.LENGTH_LONG).show();
            }
        };
        if (categoryName == null) {
            DbService.getServices(listener);
        } else {
            DbService.getServices(categoryName, listener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        User user = State.getState().getSignedInUser();
        if (null != user && user.getType() == User.Types.ADMIN) {
            getMenuInflater().inflate(R.menu.service_list_options, menu);
            if (categoryName == null) {
                menu.setGroupVisible(R.id.grp_category_controls, false);
            }
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_category_create: onCreateCategoryClick(); return true;
            case R.id.menu_item_service_create: onCreateServiceClick(); return true;
            case R.id.menu_item_category_delete: onDeleteCategoryClick(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDeleteCategoryClick() {
        if (categoryName != null) {
            final Category category = new Category(categoryName);
            new AlertDialog.Builder(this)
                    .setTitle("Delete Category")
                    .setMessage("Are you sure you want to delete the '" + categoryName + "' category?  \r\nThis CANNOT be undone!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DbService.getServices(categoryName, new AsyncValueEventListener<Service>() {
                                @Override
                                public void onSuccess(ArrayList<Service> data) {
                                    if (null == data) {
                                        Toast.makeText(getApplicationContext(), "Unable to delete the '" + categoryName + "' category at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                                    } else if (data.size() > 0) {
                                        Toast.makeText(getApplicationContext(), "Unable to delete the '" + categoryName + "' category because it has services assigned to it. Please re-assign the services, then delete.", Toast.LENGTH_LONG).show();
                                    } else {
                                        DbCategory.deleteCategory(category, new AsyncActionEventListener() {
                                            @Override
                                            public void onSuccess() {
                                                Toast.makeText(getApplicationContext(), "The the '" + categoryName + "' category has been successfully deleted.", Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                            @Override
                                            public void onFailure(AsyncEventFailureReason reason) {
                                                Toast.makeText(getApplicationContext(), "Unable to delete the '" + categoryName + "' category at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onFailure(AsyncEventFailureReason reason) {
                                    Toast.makeText(getApplicationContext(), "Unable to delete the '" + categoryName + "' category at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton(R.string.cancel, null).show();
        }
    }

    public void onCreateCategoryClick() {
        startActivity(new Intent(getApplicationContext(), CategoryCreateActivity.class));
    }

    public void onCreateServiceClick() {
        Intent intent = new Intent(getApplicationContext(), ServiceCreateActivity.class);
        if (null != categoryName) { intent.putExtra("category_name", categoryName); }
        startActivity(intent);
    }

}

package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import ca.uottawa.seg2105.project.cqondemand.database.DbListener;
import ca.uottawa.seg2105.project.cqondemand.database.DbService;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class ServiceListActivity extends SignedInActivity {

    protected RecyclerView recycler_list;
    protected ServiceListAdapter service_list_adapter;
    protected Category currentCategory;
    protected DbListener<?> dbListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);
        recycler_list = findViewById(R.id.recycler_list);
        recycler_list.setHasFixedSize(true);
        recycler_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        TextView txt_category_name = findViewById(R.id.txt_category_name);
        Intent intent = getIntent();
        String categoryName = intent.getStringExtra("category_name");
        currentCategory = State.getState().getCurrentCategory();
        State.getState().setCurrentCategory(null);
        if (null == currentCategory && null != categoryName) { currentCategory = new Category(categoryName); }
        AsyncValueEventListener<Service> listener = new AsyncValueEventListener<Service>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Service> data) {
                service_list_adapter = new ServiceListAdapter(getApplicationContext(), data, new View.OnClickListener() {
                    public void onClick(final View view) {
                        State.getState().setCurrentService((Service) view.getTag());
                        Intent intent = new Intent(getApplicationContext(), ServiceViewActivity.class);
                        startActivity(intent);
                    }
                });
                recycler_list.setAdapter(service_list_adapter);
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                Toast.makeText(getApplicationContext(), R.string.service_list_db_error, Toast.LENGTH_LONG).show();
            }
        };
        if (null != currentCategory) {
            txt_category_name.setText(String.format(Locale.CANADA, getString(R.string.category_title_template), currentCategory.getName()));
            dbListener = DbService.getServicesLive(currentCategory, listener);
        } else {
            txt_category_name.setVisibility(View.GONE);
            findViewById(R.id.divider_category_name).setVisibility(View.GONE);
            dbListener = DbService.getServicesLive(listener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup the data listener for the services list
        if (null != dbListener) { dbListener.removeListener(); }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        User user = State.getState().getSignedInUser();
        if (null != user && user.getType() == User.Types.ADMIN) {
            getMenuInflater().inflate(R.menu.service_list_options, menu);
            if (currentCategory == null) {
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
        if (currentCategory != null) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_category)
                    .setMessage(String.format(getString(R.string.delete_confirm_dialog_template), currentCategory.getName(), getString(R.string.category)))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DbService.getServices(currentCategory, new AsyncValueEventListener<Service>() {
                                @Override
                                public void onSuccess(@NonNull ArrayList<Service> data) {
                                    if (null == data) {
                                        Toast.makeText(getApplicationContext(), String.format(getString(R.string.category_delete_db_error), currentCategory.getName()), Toast.LENGTH_LONG).show();
                                    } else if (data.size() > 0) {
                                        Toast.makeText(getApplicationContext(), String.format(getString(R.string.category_delete_has_services_error), currentCategory.getName()), Toast.LENGTH_LONG).show();
                                    } else {
                                        DbCategory.deleteCategory(currentCategory, new AsyncActionEventListener() {
                                            @Override
                                            public void onSuccess() {
                                                Toast.makeText(getApplicationContext(), String.format(getString(R.string.category_delete_success), currentCategory.getName()), Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                            @Override
                                            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                                                Toast.makeText(getApplicationContext(), String.format(getString(R.string.category_delete_db_error), currentCategory.getName()), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onFailure(@NonNull AsyncEventFailureReason reason) {
                                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.category_delete_db_error), currentCategory.getName()), Toast.LENGTH_LONG).show();
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
        if (null != currentCategory.getName()) { intent.putExtra("category_name", currentCategory.getName()); }
        startActivity(intent);
    }

}

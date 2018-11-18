package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
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

    protected enum Mode { STANDARD, MANAGE_SERVICES, PICK_PROVIDER_SERVICES, EDIT_PROVIDER_SERVICES, VIEW_PROVIDER_SERVICES }
    protected Mode mode;
    protected boolean useCategory;
    TextView txt_sub_title;
    View divider_txt_sub_title;
    protected RecyclerView recycler_list;
    protected ServiceListAdapter service_list_adapter;
    protected Category currentCategory;
    protected User currentUser;
    protected DbListener<?> dbListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);
        txt_sub_title = findViewById(R.id.txt_sub_title);
        divider_txt_sub_title = findViewById(R.id.divider_txt_sub_title);
        recycler_list = findViewById(R.id.recycler_list);
        recycler_list.setHasFixedSize(true);
        recycler_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        // Initialize the category components as hidden
        txt_sub_title.setVisibility(View.GONE);
        divider_txt_sub_title.setVisibility(View.GONE);
        // Get the current category and current user
        currentCategory = State.getState().getCurrentCategory();
        State.getState().setCurrentCategory(null);
        currentUser = State.getState().getCurrentUser();
        State.getState().setCurrentUser(null);
        ActionBar actionBar = getSupportActionBar();
        // Determine and initialize the mode
        if (null != currentUser && User.Type.SERVICE_PROVIDER == currentUser.getType()) {
            if (State.getState().getSignedInUser().equals(currentUser)) {
                mode = Mode.EDIT_PROVIDER_SERVICES;
                if (null != actionBar) { actionBar.setTitle(getString(R.string.my_services)); }
            } else {
                mode = Mode.VIEW_PROVIDER_SERVICES;
            }
        } else if (State.getState().getSignedInUser().isAdmin()) {
            mode = Mode.MANAGE_SERVICES;
            useCategory = null != currentCategory;
        } else if (User.Type.SERVICE_PROVIDER == State.getState().getSignedInUser().getType()) {
            mode = Mode.PICK_PROVIDER_SERVICES;
            useCategory = null != currentCategory;
            if (null != actionBar) { actionBar.setTitle(getString(R.string.pick_service)); }
        }  else {
            mode = Mode.STANDARD;
            useCategory = null != currentCategory;
        }

        AsyncValueEventListener<Service> listener = new AsyncValueEventListener<Service>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Service> data) {
                service_list_adapter = new ServiceListAdapter(getApplicationContext(), data, Mode.PICK_PROVIDER_SERVICES == mode, new View.OnClickListener() {
                    public void onClick(final View view) {
                        if (Mode.PICK_PROVIDER_SERVICES == mode) {
                            // TODO: Create relationship between service provider and service
                            Intent intent = new Intent();
                            intent.putExtra("finish", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            State.getState().setCurrentService((Service) view.getTag());
                            Intent intent = new Intent(getApplicationContext(), ServiceViewActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                recycler_list.setAdapter(service_list_adapter);
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                Toast.makeText(getApplicationContext(), R.string.service_list_db_error, Toast.LENGTH_LONG).show();
            }
        };

        if (Mode.EDIT_PROVIDER_SERVICES == mode || Mode.VIEW_PROVIDER_SERVICES == mode) {
            // TODO: Load the services for the specified provider (Loading all services for now)
            dbListener = DbService.getServicesLive(listener);
        } else if (useCategory) {
            setSubTitle(String.format(Locale.CANADA, getString(R.string.category_title_template), currentCategory.getName()));
            dbListener = DbService.getServicesByCategoryLive(currentCategory, listener);
        } else {
            dbListener = DbService.getServicesLive(listener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup the data listener for the services list
        if (null != dbListener) { dbListener.removeListener(); }
    }

    private void setSubTitle(@NonNull String subTitle) {
        txt_sub_title.setVisibility(View.VISIBLE);
        divider_txt_sub_title.setVisibility(View.VISIBLE);
        txt_sub_title.setText(subTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Mode.MANAGE_SERVICES == mode) {
            getMenuInflater().inflate(R.menu.service_list_manage_options, menu);
            if (!useCategory) { menu.setGroupVisible(R.id.grp_category_controls, false); }
            return true;
        } else if (Mode.EDIT_PROVIDER_SERVICES == mode) {
            getMenuInflater().inflate(R.menu.service_list_options, menu);
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
            case R.id.menu_item_service_add: onAddServiceClick(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAddServiceClick() {
        startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
    }

    public void onCreateCategoryClick() {
        startActivity(new Intent(getApplicationContext(), CategoryCreateActivity.class));
    }

    public void onCreateServiceClick() {
        Intent intent = new Intent(getApplicationContext(), ServiceCreateActivity.class);
        if (null != currentCategory) { intent.putExtra("category_name", currentCategory.getName()); }
        startActivity(intent);
    }

    public void onDeleteCategoryClick() {
        if (useCategory) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_category)
                    .setMessage(String.format(getString(R.string.delete_confirm_dialog_template), currentCategory.getName(), getString(R.string.category).toLowerCase()))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DbService.getServicesByCategory(currentCategory, new AsyncValueEventListener<Service>() {
                                @Override
                                public void onSuccess(@NonNull ArrayList<Service> data) {
                                    if (data.size() > 0) {
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

}

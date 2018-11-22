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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.adapters.ServiceListAdapter;
import ca.uottawa.seg2105.project.cqondemand.database.DbCategory;
import ca.uottawa.seg2105.project.cqondemand.database.DbListenerHandle;
import ca.uottawa.seg2105.project.cqondemand.database.DbService;
import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.database.DbUtilRelational;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class ServiceListActivity extends SignedInActivity {

    protected enum Mode { LIST_SERVICES, MANAGE_SERVICES, ADD_PROVIDER_SERVICES, REMOVE_PROVIDER_SERVICES, LIST_PROVIDER_SERVICES }
    protected boolean itemClickEnabled = true;
    protected Mode mode;
    protected boolean useCategory;
    TextView txt_sub_title;
    View divider_txt_sub_title;
    protected RecyclerView recycler_list;
    protected ServiceListAdapter service_list_adapter;
    protected Category currentCategory;
    protected User currentUser;
    protected ServiceProvider currentProvider;
    protected DbListenerHandle<?> dbListenerHandle;
    int itemActionIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);
        if (null == State.getInstance(getApplicationContext()).getSignedInUser()) { return; }
        txt_sub_title = findViewById(R.id.txt_sub_title);
        divider_txt_sub_title = findViewById(R.id.divider_txt_sub_title);
        recycler_list = findViewById(R.id.recycler_list);
        recycler_list.setHasFixedSize(true);
        recycler_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        // Initialize the category components as hidden
        txt_sub_title.setVisibility(View.GONE);
        divider_txt_sub_title.setVisibility(View.GONE);
        Intent intent = getIntent();
        // Get the current category and current user
        currentCategory = (Category) intent.getSerializableExtra("category");
        currentUser = (User) intent.getSerializableExtra("user");
        // Get the action bar
        ActionBar actionBar = getSupportActionBar();
        // Set the default item itemActionIcon
        itemActionIcon = R.drawable.ic_chevron_right_med_30;
        // Determine and initialize the mode
        if (currentUser instanceof ServiceProvider) {
            currentProvider = (ServiceProvider) currentUser;
            if (State.getInstance(getApplicationContext()).getSignedInUser().equals(currentUser)) {
                mode = Mode.REMOVE_PROVIDER_SERVICES;
                if (null != actionBar) { actionBar.setTitle(getString(R.string.my_services)); }
                itemActionIcon = R.drawable.ic_remove_circle_outline_med_30;
            } else {
                mode = Mode.LIST_PROVIDER_SERVICES;
            }
        } else if (State.getInstance(getApplicationContext()).getSignedInUser().isAdmin()) {
            mode = Mode.MANAGE_SERVICES;
            useCategory = null != currentCategory;
        } else if (State.getInstance(getApplicationContext()).getSignedInUser() instanceof ServiceProvider) {
            currentProvider = (ServiceProvider) State.getInstance(getApplicationContext()).getSignedInUser();
            mode = Mode.ADD_PROVIDER_SERVICES;
            itemActionIcon = R.drawable.ic_add_med_30;
            useCategory = null != currentCategory;
            if (null != actionBar) { actionBar.setTitle(getString(R.string.select_a_service)); }
        }  else {
            mode = Mode.LIST_SERVICES;
            useCategory = null != currentCategory;
        }

        AsyncValueEventListener<Service> listener = new AsyncValueEventListener<Service>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Service> data) {
                service_list_adapter = new ServiceListAdapter(getApplicationContext(), data, itemActionIcon, getItemClickListener());
                recycler_list.setAdapter(service_list_adapter);
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                Toast.makeText(getApplicationContext(), R.string.service_list_db_error, Toast.LENGTH_LONG).show();
            }
        };

        // Decide which database request to make
        switch (mode) {
            case LIST_PROVIDER_SERVICES:
            case REMOVE_PROVIDER_SERVICES: dbListenerHandle = DbUser.getServicesProvidedLive(currentProvider, listener); break;
            case ADD_PROVIDER_SERVICES:
            case MANAGE_SERVICES:
            case LIST_SERVICES:
            default:
                if (useCategory) {
                    setSubTitle(String.format(Locale.CANADA, getString(R.string.category_title_template), currentCategory.getName()));
                    dbListenerHandle = DbService.getServicesByCategoryLive(currentCategory, listener);
                } else {
                    dbListenerHandle = DbService.getServicesLive(listener);
                }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup the data listener for the services list
        if (null != dbListenerHandle) { dbListenerHandle.removeListener(); }
    }

    @Override
    public void onResume() {
        super.onResume();
        itemClickEnabled = true;
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
            if (!useCategory) { menu.setGroupVisible(R.id.grp_category_delete_controls, false); }
            return true;
        } else if (Mode.REMOVE_PROVIDER_SERVICES == mode) {
            getMenuInflater().inflate(R.menu.service_list_options, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    private View.OnClickListener getItemClickListener() {
        if (Mode.ADD_PROVIDER_SERVICES == mode) {
            return new View.OnClickListener() {
                public void onClick(final View view) {
                    if (!itemClickEnabled) { return; }
                    itemClickEnabled = false;
                    final Service service = (Service) view.getTag();
                    DbUtilRelational.linkServiceAndProvider(service, currentProvider, new AsyncActionEventListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getApplicationContext(), String.format(getString(R.string.service_added_to_provider_success_template), service.getName()), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.putExtra("finish", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        @Override
                        public void onFailure(@NonNull AsyncEventFailureReason reason) {
                            itemClickEnabled = true;
                            Toast.makeText(getApplicationContext(), String.format(getString(R.string.service_added_to_provider_fail_template), service.getName()), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            };
        } else if (Mode.REMOVE_PROVIDER_SERVICES == mode) {
            return new View.OnClickListener() {
                public void onClick(final View view) {
                    if (!itemClickEnabled) { return; }
                    itemClickEnabled = false;
                    final Service service = (Service) view.getTag();
                    AlertDialog dialog = new AlertDialog.Builder(ServiceListActivity.this)
                            .setTitle(R.string.remove_service)
                            .setMessage(String.format(getString(R.string.remove_confirm_dialog_template), service.getName(), getString(R.string.service).toLowerCase()))
                            .setIcon(R.drawable.ic_report_red_30)
                            .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    DbUtilRelational.unlinkServiceAndProvider(service, currentProvider, new AsyncActionEventListener() {
                                        @Override
                                        public void onSuccess() {
                                            Toast.makeText(getApplicationContext(), String.format(getString(R.string.service_removed_from_provider_success_template), service.getName()), Toast.LENGTH_LONG).show();
                                        }
                                        @Override
                                        public void onFailure(@NonNull AsyncEventFailureReason reason) {
                                            Toast.makeText(getApplicationContext(), String.format(getString(R.string.item_remove_db_error_template), service.getName(), getString(R.string.service).toLowerCase()), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            })
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) { itemClickEnabled = true; }
                            })
                            .setNegativeButton(R.string.cancel, null).show();
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.text_primary_dark));
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dialog_red));
                }
            };
        } else {
            return new View.OnClickListener() {
                public void onClick(final View view) {
                    if (!itemClickEnabled) { return; }
                    itemClickEnabled = false;
                    Intent intent = new Intent(getApplicationContext(), ServiceViewActivity.class);
                    intent.putExtra("service", (Serializable) view.getTag());
                    startActivity(intent);
                }
            };
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_category_create: onCreateCategoryClick(); return true;
            case R.id.menu_item_service_create: onCreateServiceClick(); return true;
            case R.id.menu_item_category_delete: onDeleteCategoryClick(); return true;
            case R.id.menu_item_service_add: onLinkServiceToProviderClick(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onLinkServiceToProviderClick() {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
    }

    public void onCreateCategoryClick() {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        startActivity(new Intent(getApplicationContext(), CategoryCreateActivity.class));
    }

    public void onCreateServiceClick() {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), ServiceCreateActivity.class);
        if (null != currentCategory) { intent.putExtra("category", currentCategory); }
        startActivity(intent);
    }

    public void onDeleteCategoryClick() {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        if (useCategory) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_category)
                    .setMessage(String.format(getString(R.string.delete_confirm_dialog_template), currentCategory.getName(), getString(R.string.category).toLowerCase()))
                    .setIcon(R.drawable.ic_report_red_30)
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
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) { itemClickEnabled = true; }
                    })
                    .setNegativeButton(R.string.cancel, null).show();
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.text_primary_dark));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dialog_red));
        }
    }

}

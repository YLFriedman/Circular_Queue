package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

/**
 * The class <b>ServiceListActivity</b> is a UI class that allows a user to see and select from list of services.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class ServiceListActivity extends SignedInActivity {

    /**
     * An enum defining the view various modes of this activity
     */
    private enum Mode { LIST_SERVICES, MANAGE_SERVICES, ADD_PROVIDER_SERVICES, REMOVE_PROVIDER_SERVICES, LIST_PROVIDER_SERVICES, PICK_FOR_BOOKING }

    /**
     * The currently active mode
     */
    private Mode mode;

    /**
     * Whether or not relevant onClick actions are enabled for within this activity
     */
    private boolean onClickEnabled = true;

    /**
     * Whether or not the category should be used to filter the services
     */
    private boolean useCategory;

    /**
     * A view that displays the category name
     */
    TextView txt_sub_title;

    /**
     * A view that divides the category name from the service list
     */
    View divider_txt_sub_title;

    /**
     * The view that displays the list of services
     */
    private RecyclerView recycler_list;

    /**
     * The category that is being used to filter the services
     */
    private Category currentCategory;

    /**
     * The user that is being used to filter the services
     */
    private User currentUser;

    /**
     * The service provider that is being used to filter the services
     */
    private ServiceProvider currentProvider;

    /**
     * Stores the handle to the database callback so that it can be cleaned up when the activity ends
     */
    private DbListenerHandle<?> dbListenerHandle;

    /**
     * The resource id of the icon to use for the action icon of each service
     */
    int itemActionIcon;

    /**
     * Sets up the activity. This is run during the creation phase of the activity lifecycle.
     * @param savedInstanceState a bundle containing the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);
        User signedInUser = State.getInstance().getSignedInUser();
        if (null == signedInUser) { return; }
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
        try {
            currentCategory = (Category) intent.getSerializableExtra("category");
            currentUser = (User) intent.getSerializableExtra("user");
        } catch (ClassCastException e) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Get the action bar
        ActionBar actionBar = getSupportActionBar();
        // Set the default item itemActionIcon
        itemActionIcon = R.drawable.ic_chevron_right_med_30;
        // Determine and initialize the mode
        if (currentUser instanceof ServiceProvider) {
            currentProvider = (ServiceProvider) currentUser;
            if (signedInUser.equals(currentUser)) {
                mode = Mode.REMOVE_PROVIDER_SERVICES;
                if (null != actionBar) { actionBar.setTitle(getString(R.string.my_services)); }
                itemActionIcon = R.drawable.ic_remove_circle_outline_med_30;
            } else {
                mode = Mode.LIST_PROVIDER_SERVICES;
            }
        } else {
            switch (signedInUser.getType()) {
                case ADMIN:
                    mode = Mode.MANAGE_SERVICES;
                    useCategory = null != currentCategory;
                    break;
                case SERVICE_PROVIDER:
                    currentProvider = (ServiceProvider) signedInUser;
                    mode = Mode.ADD_PROVIDER_SERVICES;
                    itemActionIcon = R.drawable.ic_add_med_30;
                    useCategory = null != currentCategory;
                    if (null != actionBar) { actionBar.setTitle(getString(R.string.select_a_service)); }
                    break;
                case HOMEOWNER:
                    mode = Mode.PICK_FOR_BOOKING;
                    useCategory = null != currentCategory;
                    break;
                default:
                    mode = Mode.LIST_SERVICES;
                    useCategory = null != currentCategory;
            }
        }

        AsyncValueEventListener<Service> listener = new AsyncValueEventListener<Service>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Service> data) {
                ServiceListAdapter service_list_adapter = new ServiceListAdapter(getApplicationContext(), data, itemActionIcon, getItemClickListener());
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
            case PICK_FOR_BOOKING:
            default:
                if (useCategory) {
                    setSubTitle(String.format(Locale.CANADA, getString(R.string.category_title_template), currentCategory.getName()));
                    dbListenerHandle = DbService.getServicesByCategoryLive(currentCategory, listener);
                } else {
                    dbListenerHandle = DbService.getServicesLive(listener);
                }
        }
    }

    /**
     * Removes the listener for data from the database.
     * This is run during the destroy phase of the activity lifecycle.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup the data listener
        if (null != dbListenerHandle) { dbListenerHandle.removeListener(); }
    }

    /**
     * Enables the relevant onClick actions within this activity.
     * This is run during the resume phase of the activity lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        onClickEnabled = true;
    }

    /**
     * Makes the subtitle views visible and sets the subtitle text
     * @param subTitle the subtitle to be set in the sub_title view
     */
    private void setSubTitle(@NonNull String subTitle) {
        txt_sub_title.setVisibility(View.VISIBLE);
        divider_txt_sub_title.setVisibility(View.VISIBLE);
        txt_sub_title.setText(subTitle);
    }

    /**
     * Creates the View.OnClickListener used for the onClick of each item in the service list
     * @return a View.OnClickListener object
     */
    private View.OnClickListener getItemClickListener() {
        if (Mode.ADD_PROVIDER_SERVICES == mode) {
            return new View.OnClickListener() {
                public void onClick(final View view) {
                    if (!onClickEnabled) { return; }
                    onClickEnabled = false;
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
                            onClickEnabled = true;
                            Toast.makeText(getApplicationContext(), String.format(getString(R.string.service_added_to_provider_fail_template), service.getName()), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            };
        } else if (Mode.REMOVE_PROVIDER_SERVICES == mode) {
            return new View.OnClickListener() {
                public void onClick(final View view) {
                    if (!onClickEnabled) { return; }
                    onClickEnabled = false;
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
                                public void onDismiss(DialogInterface dialog) { onClickEnabled = true; }
                            })
                            .setNegativeButton(R.string.cancel, null).show();
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.text_primary_dark));
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dialog_red));
                }
            };
        } else if (Mode.PICK_FOR_BOOKING == mode) {
            return new View.OnClickListener() {
                public void onClick(final View view) {
                    if (!onClickEnabled) { return; }
                    onClickEnabled = false;
                    Intent intent = new Intent(getApplicationContext(), ServiceProviderPickerActivity.class);
                    intent.putExtra("service", (Serializable) view.getTag());
                    startActivity(intent);
                }
            };
        } else {
            return new View.OnClickListener() {
                public void onClick(final View view) {
                    if (!onClickEnabled) { return; }
                    onClickEnabled = false;
                    Intent intent = new Intent(getApplicationContext(), ServiceViewActivity.class);
                    intent.putExtra("service", (Serializable) view.getTag());
                    startActivity(intent);
                }
            };
        }
    }

    /**
     * Sets the menu to be used in the action bar
     * @return true if the options menu is created, false otherwise
     */
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

    /**
     * The onClick handler for the action bar menu items
     * @param item the menu item that was clicked
     * @return true if the menu item onClick was handled, the result of the super class method otherwise
     */
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

    /**
     * The on-click handler to launch the category list activity
     */
    public void onLinkServiceToProviderClick() {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
    }

    /**
     * The on-click handler to launch the category creation activity
     */
    public void onCreateCategoryClick() {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        startActivity(new Intent(getApplicationContext(), CategoryCreateActivity.class));
    }

    /**
     * The on-click handler to launch the service creation activity
     */
    public void onCreateServiceClick() {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), ServiceCreateActivity.class);
        if (null != currentCategory) { intent.putExtra("category", currentCategory); }
        startActivity(intent);
    }

    /**
     * Prompts the user with the delete category confirmation screen and triggers the deletion process if confirmed.
     */
    public void onDeleteCategoryClick() {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
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
                        public void onDismiss(DialogInterface dialog) { onClickEnabled = true; }
                    })
                    .setNegativeButton(R.string.cancel, null).show();
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.text_primary_dark));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dialog_red));
        }
    }

}

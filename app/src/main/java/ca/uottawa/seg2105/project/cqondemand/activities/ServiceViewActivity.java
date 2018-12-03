package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.database.DbService;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

/**
 * The class <b>ServiceViewActivity</b> is a UI class that allows a user to see the details of an individual service.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class ServiceViewActivity extends SignedInActivity {

    /**
     * Whether or not relevant onClick actions are enabled for within this activity
     */
    private boolean onClickEnabled = true;

    /**
     * A view that displays the service name
     */
    private TextView txt_name;

    /**
     * A view that displays the service rate
     */
    private TextView txt_rate;

    /**
     * A view that displays the service category
     */
    private TextView txt_category;

    /**
     * The category that the service is under
     */
    private Category currentCategory;

    /**
     * The service that is being displayed
     */
    private Service currentService;

    /**
     * Sets up the activity. This is run during the creation phase of the activity lifecycle.
     * @param savedInstanceState a bundle containing the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_view);
        txt_name = findViewById(R.id.txt_name);
        txt_rate = findViewById(R.id.txt_rate);
        txt_category = findViewById(R.id.txt_category);

        Intent intent = getIntent();
        try {
            currentService = (Service) intent.getSerializableExtra("service");
            currentCategory = (Category) intent.getSerializableExtra("category");
        } catch (ClassCastException e) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (null != currentService) {
            configureViews();
        }  else {
            Toast.makeText(getApplicationContext(), R.string.current_service_empty, Toast.LENGTH_LONG).show();
            finish();
        }
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
     * Refreshes the activity when a new intent is received
     * @param intent the intent containing the new information used to update the activity
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        currentService = (Service) intent.getSerializableExtra("service");
        currentCategory = (Category) intent.getSerializableExtra("category");
        if (null != currentService) {
            configureViews();
        }  else {
            Toast.makeText(getApplicationContext(), R.string.current_service_empty, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Configures the view items within this activity
     */
    private void configureViews() {
        txt_name.setText("");
        txt_rate.setText("");
        txt_category.setText("");
        if (null != currentService) {
            txt_name.setText(currentService.getName());
            if (0 == currentService.getRate()) {
                txt_rate.setText(getString(R.string.zero_value_service));
            } else {
                txt_rate.setText(String.format(Locale.CANADA, getString(R.string.service_rate_template), currentService.getRate()));
            }
            // If given a category, load it first to speed up category display when the service doesnt have it pre-loaded
            if (null != currentCategory) { txt_category.setText(String.format(Locale.CANADA, getString(R.string.category_template), currentCategory.getName())); }
            currentService.getCategory(new AsyncSingleValueEventListener<Category>() {
                @Override
                public void onSuccess(@NonNull Category item) {
                    currentCategory = item;
                    txt_category.setText(String.format(Locale.CANADA, getString(R.string.category_template), item.getName()));
                }
                @Override
                public void onFailure(@NonNull AsyncEventFailureReason reason) { }
            });
        }
    }

    /**
     * Sets the menu to be used in the action bar
     * @return true if the options menu is created, false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        User user = State.getInstance().getSignedInUser();
        if (null != user && User.Type.ADMIN == user.getType()) {
            getMenuInflater().inflate(R.menu.service_options, menu);
            menu.setGroupVisible(R.id.grp_category_controls, false);
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
            case R.id.menu_item_service_create: onCreateServiceClick(); return true;
            case R.id.menu_item_service_edit: onEditServiceClick(); return true;
            case R.id.menu_item_service_delete: onDeleteServiceClick(); return true;
        }
        return super.onOptionsItemSelected(item);
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
     * The on-click handler to launch the service edit activity
     */
    public void onEditServiceClick() {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), ServiceEditActivity.class);
        intent.putExtra("service", currentService);
        startActivity(intent);
    }

    /**
     * Prompts the user with the delete service confirmation screen and triggers the deletion process if confirmed.
     */
    public void onDeleteServiceClick() {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        if (null != currentService) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_service)
                    .setMessage(String.format(getString(R.string.delete_confirm_dialog_template), currentService.getName(), getString(R.string.service).toLowerCase()))
                    .setIcon(R.drawable.ic_report_red_30)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DbService.deleteService(currentService, new AsyncActionEventListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.service_delete_success), currentService.getName()), Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                @Override
                                public void onFailure(@NonNull AsyncEventFailureReason reason) {
                                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.service_delete_db_error), currentService.getName()), Toast.LENGTH_LONG).show();
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

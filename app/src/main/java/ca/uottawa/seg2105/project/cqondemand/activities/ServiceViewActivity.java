package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class ServiceViewActivity extends SignedInActivity {

    protected TextView txt_name;
    protected TextView txt_rate;
    protected TextView txt_category;
    protected String categoryName;
    protected Service currentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_view);
        txt_name = findViewById(R.id.txt_name);
        txt_rate = findViewById(R.id.txt_rate);
        txt_category = findViewById(R.id.txt_category);
        txt_name.setText("");
        txt_rate.setText("");
        txt_category.setText("");
    }

    public void onResume() {
        super.onResume();
        if (isFinishing()) { return; }
        if (null != State.getState().getCurrentService()) {
            currentService = State.getState().getCurrentService();
            State.getState().setCurrentService(null);
            setupFields();
        } else if (null != currentService) {
            setupFields();
        }  else {
            Toast.makeText(getApplicationContext(), R.string.current_service_empty, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupFields() {
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
            currentService.getCategory(new AsyncSingleValueEventListener<Category>() {
                @Override
                public void onSuccess(@NonNull Category item) {
                    categoryName = item.getName();
                    txt_category.setText(String.format(Locale.CANADA, getString(R.string.category_template), item.getName()));
                }
                @Override
                public void onFailure(@NonNull AsyncEventFailureReason reason) { }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        User user = State.getState().getSignedInUser();
        if (null != user && user.isAdmin()) {
            getMenuInflater().inflate(R.menu.service_options, menu);
            menu.setGroupVisible(R.id.grp_category_controls, false);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_service_create: onCreateServiceClick(); return true;
            case R.id.menu_item_service_edit: onEditServiceClick(); return true;
            case R.id.menu_item_service_delete: onDeleteServiceClick(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCreateServiceClick() {
        Intent intent = new Intent(getApplicationContext(), ServiceCreateActivity.class);
        if (null != categoryName) { intent.putExtra("category_name", categoryName); }
        startActivity(intent);
    }

    public void onEditServiceClick() {
        State.getState().setCurrentService(currentService);
        Intent intent = new Intent(getApplicationContext(), ServiceEditActivity.class);
        startActivity(intent);
    }

    public void onDeleteServiceClick() {
        if (null != currentService) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_service)
                    .setMessage(String.format(getString(R.string.delete_confirm_dialog_template), currentService.getName(), getString(R.string.service)))
                    .setIcon(android.R.drawable.ic_dialog_alert)
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
                    .setNegativeButton(R.string.cancel, null).show();
        }
    }

}

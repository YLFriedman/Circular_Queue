package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class ServiceViewActivity extends AppCompatActivity {

    TextView txt_name;
    TextView txt_rate;
    TextView txt_category;
    String service_name;
    Service service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_view);
        txt_name = findViewById(R.id.txt_name);
        txt_rate = findViewById(R.id.txt_rate);
        txt_category = findViewById(R.id.txt_category);

        txt_name.setVisibility(View.GONE);
        txt_rate.setVisibility(View.GONE);
        txt_category.setVisibility(View.GONE);

        Intent intent = getIntent();
        service_name = intent.getStringExtra("service_name");

    }

    public void onResume() {
        super.onResume();
        if (null == State.getState().getSignedInUser()) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (null != service_name) {
            txt_name.setText(service_name);
            txt_name.setVisibility(View.VISIBLE);
            Service.getService(service_name, new AsyncSingleValueEventListener<Service>() {
                @Override
                public void onSuccess(@NonNull Service item) {
                    service = item;
                    txt_rate.setText(String.format(getString(R.string.service_rate_template), item.getRate()));
                    txt_rate.setVisibility(View.VISIBLE);
                    Category.getCategory(item.getCategoryID(), new AsyncSingleValueEventListener<Category>() {
                        @Override
                        public void onSuccess(@NonNull Category item) {
                            txt_category.setText(String.format(getString(R.string.category_template), item.getName()));
                            txt_category.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onFailure(AsyncEventFailureReason reason) { }
                    });
                }
                @Override
                public void onFailure(AsyncEventFailureReason reason) {
                    Toast.makeText(getApplicationContext(), "There was an error getting the service details from the database. Please try again later.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        User user = State.getState().getSignedInUser();
        if (null != user && user.getType() == User.Types.ADMIN) {
            getMenuInflater().inflate(R.menu.service_options, menu);
            menu.setGroupVisible(R.id.grp_category_controls, false);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_service_edit: onEditServiceClick(); return true;
            case R.id.menu_item_service_delete: onDeleteServiceClick(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onEditServiceClick() {
        startActivity(new Intent(getApplicationContext(), ServiceEditActivity.class));
    }
    public void onDeleteServiceClick() {
        if (null != service) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete the '" + service_name + "' service?  \r\nThis CANNOT be undone!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            service.delete(new AsyncActionEventListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(getApplicationContext(), "The the '" + service_name + "' service has been successfully deleted.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                @Override
                                public void onFailure(AsyncEventFailureReason reason) {
                                    Toast.makeText(getApplicationContext(), "Unable to delete the '" + service_name + "' service at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton(R.string.cancel, null).show();
        }
    }
}

package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class UserAccountViewActivity extends SignedInActivity {

    protected boolean itemClickEnabled = true;
    protected TextView txt_account_type;
    protected TextView txt_username;
    protected TextView txt_full_name;
    protected TextView txt_email;
    protected TextView txt_company_name;
    protected TextView txt_phone;
    protected TextView txt_description;
    protected TextView txt_licensed;
    protected TextView txt_address;
    protected LinearLayout service_provider_info;
    protected User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_view);
        // Set references to the UI objects
        txt_account_type = findViewById(R.id.txt_account_type);
        txt_username = findViewById(R.id.txt_username);
        txt_full_name = findViewById(R.id.txt_full_name);
        txt_email = findViewById(R.id.txt_email);
        service_provider_info = findViewById(R.id.service_provider_info);
        txt_company_name = findViewById(R.id.txt_company_name);
        txt_phone = findViewById(R.id.txt_phone);
        txt_description = findViewById(R.id.txt_description);
        txt_licensed = findViewById(R.id.txt_licensed);
        txt_address = findViewById(R.id.txt_address);

        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("user");
        if (null != currentUser) {
            setupFields();
        } else {
            currentUser = State.getInstance(getApplicationContext()).getSignedInUser();
            setupFields();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFinishing()) { return; }
        itemClickEnabled = true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        currentUser = (User) intent.getSerializableExtra("user");
        if (null != currentUser) { setupFields(); }
    }

    private void setupFields() {
        service_provider_info.setVisibility(View.GONE);
        if (null == currentUser) {
            txt_account_type.setText("");
            txt_username.setText("");
            txt_full_name.setText("");
            txt_email.setText("");
        } else {
            txt_account_type.setText(currentUser.getType().toString());
            txt_username.setText(currentUser.getUsername());
            txt_full_name.setText(String.format(getString(R.string.full_name_template), currentUser.getFirstName(), currentUser.getLastName()));
            txt_email.setText(currentUser.getEmail());
            if (currentUser instanceof ServiceProvider) {
                ServiceProvider provider = (ServiceProvider) currentUser;
                service_provider_info.setVisibility(View.VISIBLE);
                txt_company_name.setText(provider.getCompanyName());
                txt_phone.setText(provider.getPhoneNumber());
                txt_description.setText(provider.getDescription());
                txt_licensed.setText(provider.isLicensed() ? getText(R.string.yes) : getText(R.string.no));
                txt_address.setText(provider.getAddress().toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        User user = State.getInstance(getApplicationContext()).getSignedInUser();
        getMenuInflater().inflate(R.menu.user_options, menu);
        if (null != user && user.getType() == User.Type.ADMIN) {
            menu.setGroupVisible(R.id.grp_user_edit_controls, false);
        }
        if (!State.getInstance(getApplicationContext()).getSignedInUser().equals(currentUser)) {
            menu.setGroupVisible(R.id.grp_user_password_controls, false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_user_edit: onEditAccountClick(); return true;
            case R.id.menu_item_user_change_password: onChangePasswordClick(); return true;
            case R.id.menu_item_user_delete: onDeleteAccountClick(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onEditAccountClick() {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), UserAccountEditActivity.class);
        intent.putExtra("user", currentUser);
        startActivity(intent);
    }

    public void onChangePasswordClick() {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), UserAccountChangePasswordActivity.class);
        intent.putExtra("user", currentUser);
        startActivity(intent);
    }

    public void onDeleteAccountClick() {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        if (null != currentUser) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_account)
                    .setMessage(String.format(getString(R.string.delete_confirm_dialog_template), currentUser.getUsername(), getString(R.string.account).toLowerCase()))
                    .setIcon(R.drawable.ic_report_red_30)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DbUser.deleteUser(currentUser, new AsyncActionEventListener() {
                                public void onSuccess() {
                                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.account_delete_success), currentUser.getUsername()), Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                public void onFailure(@NonNull AsyncEventFailureReason reason) {
                                    Toast.makeText(getApplicationContext(), R.string.account_delete_db_error, Toast.LENGTH_LONG).show();
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

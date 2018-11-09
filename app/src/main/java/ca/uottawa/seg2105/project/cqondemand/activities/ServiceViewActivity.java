package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class ServiceViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_view);
    }

    public void onResume() {
        super.onResume();
        if (null == State.getState().getSignedInUser()) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (State.getState().getSignedInUser().getType() != User.Types.ADMIN) {
            getMenuInflater().inflate(R.menu.service_options, menu);
            menu.setGroupVisible(R.id.grp_category_create_controls, false);
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
        startActivity(new Intent(getApplicationContext(), ServiceCreateActivity.class));
    }
    public void onDeleteServiceClick() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete the '" + "" + "' service?  \r\nThis CANNOT be undone!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /*currentUser.delete(new AsyncActionEventListener(){
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "The user account '" + currentUser.getUserName() + "' has been successfully deleted.", Toast.LENGTH_LONG).show();
                                if (null != State.getState().getSignedInUser() && State.getState().getSignedInUser().equals(currentUser)) { State.getState().getSignedInUser(null); }
                                currentUser = null;
                                finish();
                            }
                            public void onFailure(AsyncEventFailureReason reason) {
                                Toast.makeText(getApplicationContext(), "Unable to delete your account at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                            }
                        });*/
                    }})
                .setNegativeButton(R.string.cancel, null).show();
    }
}

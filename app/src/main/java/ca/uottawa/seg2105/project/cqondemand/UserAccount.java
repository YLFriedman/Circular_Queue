package ca.uottawa.seg2105.project.cqondemand;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class UserAccount extends AppCompatActivity {

    private enum Views { USER_VIEW, USER_EDIT, CHANGE_PASSWORD }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
    }

    public void onChangePasswordClick(View view){
        setView(Views.CHANGE_PASSWORD);
    }

    public void onDeleteAccountClick(View view){
        //TODO: implement a method which deletes a users account from the db. Presumably the app then navigates to the login screen

    }

    public void onEditUserClick(View view){
        setView(Views.USER_EDIT);
    }

    public void onSaveUserClick(View view){
        //TODO: implement a method that saves the users choice in a db
    }

    public void onSavePasswordClick(View view){
        //TODO: implement a method that saves the users choice in a db. Should validate the passwords as well.
    }

    @Override
    public void onBackPressed() {
        LinearLayout layout_user_view = findViewById(R.id.layout_user_view);

        if (layout_user_view.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else {
            setView(Views.USER_VIEW);
        }
    }

    private void setView(Views view) {
        LinearLayout layout_user_view = findViewById(R.id.layout_user_view);
        LinearLayout layout_user_edit = findViewById(R.id.layout_user_edit);
        LinearLayout layout_change_password = findViewById(R.id.layout_change_password);

        layout_user_view.setVisibility(View.GONE);
        layout_user_edit.setVisibility(View.GONE);
        layout_change_password.setVisibility(View.GONE);

        switch (view) {
            case USER_EDIT: layout_user_edit.setVisibility(View.VISIBLE); return;
            case CHANGE_PASSWORD: layout_change_password.setVisibility(View.VISIBLE); return;
            default: layout_user_view.setVisibility(View.VISIBLE);
        }
    }

}

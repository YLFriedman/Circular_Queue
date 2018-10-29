package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainDrawerActivity extends AppCompatActivity {

    protected DrawerLayout drawer_layout;
    protected boolean firstTimeSetup = true;

    protected void setupDrawer() {

        drawer_layout = findViewById(R.id.drawer_layout);
        NavigationView nav_view = findViewById(R.id.nav_view);
        View header = nav_view.getHeaderView(0);
        Menu menu = nav_view.getMenu();
        if (firstTimeSetup) {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white);

            nav_view.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            Intent intent;
                            switch (menuItem.getItemId()) {
                                case R.id.nav_item_my_account:
                                    intent = new Intent(getApplicationContext(), UserAccountView.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    break;
                                case R.id.nav_item_sign_out:
                                    DatabaseUtil.setCurrentUser(null);
                                case R.id.nav_item_home:
                                    intent = new Intent(getApplicationContext(), Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    break;
                            }

                            // set item as selected to persist highlight
                            menuItem.setChecked(true);
                            // close drawer when item is tapped
                            drawer_layout.closeDrawers();

                            return true;
                        }
                    }
            );

            // Set the user's name in the drawer title
            User currentUser = DatabaseUtil.getCurrentUser();
            TextView nav_header_user_name = header.findViewById(R.id.nav_header_user_name);
            nav_header_user_name.setText(String.format(getString(R.string.user_full_name_template), currentUser.getFirstName(), currentUser.getLastName()));

        }

        // Highlight a menu item if it is the current activity
        MenuItem menuItem = null;
        switch (this.getClass().getSimpleName()) {
            case "Home": menuItem = menu.findItem(R.id.nav_item_home); break;
            case "UserAccountView": menuItem = menu.findItem(R.id.nav_item_my_account); break;
        }
        if (null != menuItem) { menuItem.setChecked(true); }

        firstTimeSetup = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupDrawer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer_layout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}

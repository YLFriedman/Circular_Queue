package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.adapters.CategoryListAdapter;
import ca.uottawa.seg2105.project.cqondemand.database.DbCategory;
import ca.uottawa.seg2105.project.cqondemand.database.DbListener;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class CategoryListActivity extends SignedInActivity {

    private RecyclerView recycler_list;
    DbListener<?> dbListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        recycler_list = findViewById(R.id.recycler_list);
        recycler_list.setHasFixedSize(true);
        recycler_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        dbListener = DbCategory.getCategoriesLive(new AsyncValueEventListener<Category>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Category> data) {
                recycler_list.setAdapter(new CategoryListAdapter(getApplicationContext(), data, new View.OnClickListener() {
                    public void onClick(final View view) {
                        TextView field = view.findViewById(R.id.txt_title);
                        Intent intent = new Intent(getApplicationContext(), ServiceListActivity.class);
                        intent.putExtra("category_name", field.getText().toString());
                        startActivity(intent);
                    }
                }));
            }
            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                Toast.makeText(getApplicationContext(), getString(R.string.category_list_db_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup the data listener for the categories list
        if (null != dbListener) { dbListener.removeListener(); }
    }

    public void onCreateCategoryClick() {
        startActivity(new Intent(getApplicationContext(), CategoryCreateActivity.class));
    }

    public void onCreateServiceClick() {
        startActivity(new Intent(getApplicationContext(), ServiceCreateActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        User user = State.getState().getSignedInUser();
        if (null != user && user.getType() == User.Types.ADMIN) {
            getMenuInflater().inflate(R.menu.category_list_options, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_category_create: onCreateCategoryClick(); return true;
            case R.id.menu_item_service_create: onCreateServiceClick(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

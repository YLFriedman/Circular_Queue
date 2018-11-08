package ca.uottawa.seg2105.project.cqondemand;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class CategoryListActivity extends AppCompatActivity {

    private User currentUser;
    private RecyclerView category_list;
    private CategoryListAdapter category_list_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoy_list);
        category_list = findViewById(R.id.category_list);
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser = State.getState().getCurrentUser();
        if (null == currentUser) {
            finish();
        } else {
            category_list.setHasFixedSize(true);
            category_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        }
    }

}
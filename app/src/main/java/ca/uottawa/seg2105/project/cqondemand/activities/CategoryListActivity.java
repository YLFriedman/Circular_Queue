package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.adapters.CategoryListAdapter;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

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

            /**
            if (currentUser.getType() == User.Types.ADMIN) {
                category_list.setHasFixedSize(true);
                category_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                Category.getCategories(new AsyncValueEventListener<Category>() {

                    @Override
                    public void onSuccess(ArrayList<Category> data) {
                        if (null != data && data.size() > 0) {
                            category_list_adapter = new CategoryListAdapter(getApplicationContext(), data, new View.OnClickListener() {
                                public void onClick(final View view) {
                                    TextView field = view.findViewById(R.id.txt_title);
                                    Intent intent = new Intent(getApplicationContext(), ServiceListActivity.class);
                                    intent.putExtra("title", field.getContentDescription());
                                    startActivity(intent);
                                }
                            });
                            category_list.setAdapter(category_list_adapter);
                        }
                    }

                    @Override
                    public void onFailure(AsyncEventFailureReason reason) {

                    }
                });
            }*/
        }
    }

}

package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

public class CategoryCreateActivity extends SignedInActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_create);
    }

    public void onCreateCategory(View view){
        final EditText field_category_name = findViewById(R.id.field_category_name);
        final String categoryName = field_category_name.getText().toString().trim();
        final Button btn_create_category = findViewById(R.id.btn_create_category);

        if (!Category.nameIsValid(categoryName)) {
            if (categoryName.isEmpty()) { field_category_name.setError("Category name is required!"); }
            else { field_category_name.setError("Category name is invalid. " + Category.ILLEGAL_CATEGORY_NAME_CHARS_MSG); }
            field_category_name.requestFocus();
            field_category_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        final Category newCategory;
        try {
            newCategory = new Category(categoryName);
        } catch (InvalidDataException e) {
            Toast.makeText(getApplicationContext(), "Unable to create the category. An invalid input has been detected: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        btn_create_category.setEnabled(false);

        newCategory.create(new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "The category '" + newCategory.getName() + "' has been successfully created. ", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ServiceListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("category_name", newCategory.getName());
                startActivity(intent);
                finish();
            }
            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                switch (reason) {
                    case DATABASE_ERROR:
                        Toast.makeText(getApplicationContext(), "Unable to create the category at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                        break;
                    case ALREADY_EXISTS:
                        field_category_name.setError("Category already exists!");
                        field_category_name.requestFocus();
                        field_category_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                        break;
                    default: // Some other kind of error
                        Toast.makeText(getApplicationContext(), "Unable to create the category at this time. Please try again later.", Toast.LENGTH_LONG).show();
                }
                btn_create_category.setEnabled(true);
            }
        });
    }

}

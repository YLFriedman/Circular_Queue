package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;

public class CategoryCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_create);
        EditText field_category_name = findViewById(R.id.field_category_name);
        Intent intent = getIntent();
        field_category_name.setText(intent.getStringExtra("title"));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void onCreateCategory(View view){
        final EditText field_category_name = findViewById(R.id.field_category_name);
        final String title = field_category_name.getText().toString().trim();
        final Button btn_create_category = findViewById(R.id.btn_create_category);

        btn_create_category.setEnabled(false);

        if (title.isEmpty()) {
            field_category_name.setError("Category name is required!");
            field_category_name.requestFocus();
            return;
        } else if (!Category.nameIsValid(title)) {
            field_category_name.setError("Category name is invalid. " + Category.ILLEGAL_CATEGORY_NAME_CHARS_MSG);
            field_category_name.requestFocus();
            return;
        }

        final Category newCategory = new Category(title);
        newCategory.create(new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "The category '" + newCategory.getName() + "' has been successfully created. ", Toast.LENGTH_LONG).show();
                btn_create_category.setEnabled(true);
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
                        break;
                    default:
                        // Some other kind of error
                        Toast.makeText(getApplicationContext(), "Unable to create the category at this time. Please try again later.", Toast.LENGTH_LONG).show();
                }
                btn_create_category.setEnabled(true);
            }
        });
    }
}

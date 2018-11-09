package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;

public class CategoryCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_create);
        EditText field_category_title = findViewById(R.id.field_category_title);
        Intent intent = getIntent();
        field_category_title.setText(intent.getStringExtra("title"));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void onCreateCategory(View view){
        final EditText field_category_title = findViewById(R.id.field_category_title);
        final String title = field_category_title.getText().toString().trim();

        if (title.isEmpty()) {
            field_category_title.setError("Category title is required!");
            field_category_title.requestFocus();
            return;
        } else if (!Category.nameIsValid(title)) {
            field_category_title.setError("Category title is invalid. " + Category.ILLEGAL_CATEGORY_NAME_CHARS_MSG);
            field_category_title.requestFocus();
            return;
        }

        final Category newCategory = new Category(title);
    }
}

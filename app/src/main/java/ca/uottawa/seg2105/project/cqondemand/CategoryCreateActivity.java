package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
    }
}

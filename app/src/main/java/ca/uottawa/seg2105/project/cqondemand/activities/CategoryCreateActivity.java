package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.database.DbCategory;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

/**
 * The class <b>CategoryCreateActivity</b> is a UI class that allows the admin user to create categories.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class CategoryCreateActivity extends SignedInActivity {

    /**
     * Sets up the activity. This is run during the creation phase of the activity lifecycle.
     * @param savedInstanceState a bundle containing the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_create);
        User signedInUser = State.getInstance().getSignedInUser();
        if (null == signedInUser || User.Type.ADMIN != signedInUser.getType()) { finish(); return; }
    }

    /**
     * The on-click handler for the create category button
     * @param view the view object that was clicked
     */
    public void onCreateCategoryClick(View view) {
        final EditText field_category_name = findViewById(R.id.field_category_name);
        final String categoryName = field_category_name.getText().toString().trim();
        final Button btn_create_category = findViewById(R.id.btn_create_category);

        if (!FieldValidation.categoryNameIsValid(categoryName)) {
            if (categoryName.isEmpty()) { field_category_name.setError(getString(R.string.empty_category_name_error)); }
            else { field_category_name.setError(getString(R.string.category_name_invalid)); }
            field_category_name.requestFocus();
            field_category_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        final Category newCategory = new Category(categoryName);

        btn_create_category.setEnabled(false);

        DbCategory.createCategory(newCategory, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.category_create_success), newCategory.getName()), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ServiceListActivity.class);
                intent.putExtra("category", newCategory);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                switch (reason) {
                    case DATABASE_ERROR:
                        Toast.makeText(getApplicationContext(), getString(R.string.category_create_db_error), Toast.LENGTH_LONG).show();
                        break;
                    case ALREADY_EXISTS:
                        field_category_name.setError(getString(R.string.category_name_taken));
                        field_category_name.requestFocus();
                        field_category_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                        break;
                    default: // Some other kind of error
                        Toast.makeText(getApplicationContext(), getString(R.string.category_create_generic_error), Toast.LENGTH_LONG).show();
                }
                btn_create_category.setEnabled(true);
            }
        });
    }

}

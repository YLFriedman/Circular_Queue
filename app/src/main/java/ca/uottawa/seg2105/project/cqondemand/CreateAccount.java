package ca.uottawa.seg2105.project.cqondemand;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

public class CreateAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    public void onTypeTglClick(View view){
        int id = view.getId();
        ToggleButton homeowner = findViewById(R.id.tgl_homeowner);
        ToggleButton service_provider = findViewById(R.id.tgl_service_provider);

        if (id == R.id.tgl_homeowner) {
            service_provider.setChecked(!homeowner.isChecked());
        } else if (id == R.id.tgl_service_provider) {
            homeowner.setChecked(!service_provider.isChecked());
        }
    }

}

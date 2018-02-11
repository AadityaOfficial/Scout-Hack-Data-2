package in.siamdtu.hackdata_2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class FirstActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        FloatingActionButton trial = (FloatingActionButton) findViewById(R.id.btNext);
        trial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent nextActivity=new Intent(FirstActivity.this,ExploreActivity.class);
                    startActivity(nextActivity);


            }
        });
    }
}

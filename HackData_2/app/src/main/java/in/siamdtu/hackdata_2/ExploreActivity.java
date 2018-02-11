package in.siamdtu.hackdata_2;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ExploreActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView imageView1, imageView2, imageView3;
    TextView label1, label2, label3;
    CardView cardView1,cardView2,cardView3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);


        ImageAndLabel imageAndLabel[] = new ImageAndLabel[3];
//        imageAndLabel[0] = (ImageAndLabel) getIntent().getExtras().getSerializable("value1");
//        imageAndLabel[1] = (ImageAndLabel) getIntent().getExtras().getSerializable("value2");
//        imageAndLabel[2] = (ImageAndLabel) getIntent().getExtras().getSerializable("value3");
        for(int i=0;i<3;i++){
            imageAndLabel[i] = new ImageAndLabel( BitmapFactory.decodeResource(getResources(),
                        Constant.images[i]),Constant.names[i]);
        }
        imageView1 = (ImageView) findViewById(R.id.image1);
        imageView2 = (ImageView) findViewById(R.id.image2);
        imageView3 = (ImageView) findViewById(R.id.image3);
        label1 = (TextView) findViewById(R.id.label1);
        label2 = (TextView) findViewById(R.id.label2);
        label3 = (TextView) findViewById(R.id.label3);

        cardView1 = (CardView)findViewById(R.id.card_view1);
        cardView2 = (CardView)findViewById(R.id.card_view2);
        cardView3 = (CardView)findViewById(R.id.card_view3);

        cardView1.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView3.setOnClickListener(this);

        imageView1.setImageBitmap(imageAndLabel[0].image);
        label1.setText(imageAndLabel[0].name);

        imageView2.setImageBitmap(imageAndLabel[1].image);
        label2.setText(imageAndLabel[1].name);

        imageView3.setImageBitmap(imageAndLabel[2].image);
        label3.setText(imageAndLabel[2].name);


    }
    @Override
    public void onClick(View v){
        Intent intent= new Intent(ExploreActivity.this,MainActivity.class);

        switch (v.getId()){
            case R.id.card_view1:intent.putExtra("location",0); break;
            case R.id.card_view2:intent.putExtra("location",1);break;
            case R.id.card_view3:intent.putExtra("location",2);break;
            default:break;
        }
        startActivity(intent);

    }
}

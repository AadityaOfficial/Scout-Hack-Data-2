package in.siamdtu.hackdata_2;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rany.albeg.wein.springfabmenu.SpringFabMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    ImageView imageView1,imageView2,imageView3;
    TextView label1,label2,label3;
    FloatingActionButton upload, book;
    ArrayList<String> _selectedFilePath;
    List<String> _selectedFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ImageAndLabel imageAndLabel[]  = new ImageAndLabel[3];
        _selectedFileName = new ArrayList<>();
        _selectedFilePath = new ArrayList<>();


        upload = (FloatingActionButton)findViewById(R.id.upload);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFromDevice();
            }
        });

        imageView1 = (ImageView)findViewById(R.id.image1);
        imageView2 = (ImageView)findViewById(R.id.image2);
        imageView3 = (ImageView)findViewById(R.id.image3);
        label1 = (TextView)findViewById(R.id.label1);
        label2 = (TextView)findViewById(R.id.label2);
        label3 = (TextView)findViewById(R.id.label3);
        String name[] = new String[3];
        Bitmap bitmap[] = new Bitmap[3];
        for(int i=0;i<3;i++){
//            Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("image"+(i+1));
//            byte[] bytes = (byte[]) getIntent().getByteArrayExtra("image"+(i+1));
//            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

            name[i] = getIntent().getStringExtra("label"+(i+1));
//            imageAndLabel[i] = new ImageAndLabel(bitmap,name);

            File imgFile = new  File(Environment.getExternalStorageDirectory()+File.separator+"Download"+File.separator+"image"+i+".jpg");

            if(imgFile.exists()){

                bitmap[i] = BitmapFactory.decodeFile(imgFile.getAbsolutePath());



            }
        }

        imageView1.setImageBitmap(bitmap[0]);
        label1.setText(name[0]);

        imageView2.setImageBitmap(bitmap[1]);
        label2.setText(name[1]);

        imageView3.setImageBitmap(bitmap[2]);
        label3.setText(name[2]);

        SpringFabMenu sfm = (SpringFabMenu)findViewById(R.id.sfm);

        sfm.setOnSpringFabMenuItemClickListener(new SpringFabMenu.OnSpringFabMenuItemClickListener() {
            @Override
            public void onSpringFabMenuItemClick(View view) {
                switch (view.getId()) {
                    case R.id.book:
                        break;
                    case R.id.upload:
                        break;
                }
            }
        });

    }

    private void selectFromDevice() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file)), 1);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Please install a file manager", Toast.LENGTH_LONG).show();

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * for Selecting File
         */
        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                String mfilePath;
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    int numFiles = clipData.getItemCount();
                    boolean isExeFileSelected = false;

                    for (int i = 0; i < numFiles; i++) {
                        mfilePath = FilePath.getPath(getApplicationContext(), clipData.getItemAt(i).getUri());
                        if (!mfilePath.substring(mfilePath.lastIndexOf('.') + 1).equals("exe")) {
                            _selectedFilePath.add(mfilePath);
                            _selectedFileName.add(mfilePath.substring(mfilePath.lastIndexOf('/') + 1));
                        } else {
                            isExeFileSelected = true;
                        }
                    }
                    if (isExeFileSelected) {
                        Toast.makeText(this, ".exe file not supported", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Uri uri = data.getData();
                    String fileDir = FilePath.getPath(getApplicationContext(), uri);
                    if (!fileDir.substring(fileDir.lastIndexOf('.') + 1).equals("exe")) {
                        _selectedFilePath.add(fileDir);
                        _selectedFileName.add(fileDir.substring(fileDir.lastIndexOf('/') + 1));
                    } else {
                        Toast.makeText(this, ".exe file not supported", Toast.LENGTH_SHORT).show();
                    }
                }

                Intent intent = new Intent(ResultActivity.this, Result2Activity.class);
                intent.putExtra("UriList", _selectedFilePath);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}

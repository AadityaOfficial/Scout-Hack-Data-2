package in.siamdtu.hackdata_2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;
import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Detector.ImageListener, CameraDetector.CameraEventListener{
    int location = 0;

    Button startSDKButton;
//    TextView smileTextView;
//    TextView ageTextView;
    ToggleButton toggleButton;

    SurfaceView cameraPreview;

    boolean isSDKStarted = false;
    ArrayList<Float> smileData = new ArrayList<>();
    ArrayList<Float> browData = new ArrayList<>();
    ArrayList<Float> cheekRaiseData = new ArrayList<>();
    ArrayList<Float> eyeClosureData = new ArrayList<>();
    ArrayList<Float> jawDropData = new ArrayList<>();
    ArrayList<Float> mouthOpenData = new ArrayList<>();
    ArrayList<Float> smirkData = new ArrayList<>();
    ArrayList<Float> upperLipData = new ArrayList<>();
    ArrayList<Float> genderData = new ArrayList<>();
    ImageAndLabel imageAndLabel[] = new ImageAndLabel[3];

    RelativeLayout mainLayout;
    boolean flag = false;
    CameraDetector detector;
    int previewWidth = 0;
    int previewHeight = 0;
    private EasyVideoPlayer player;
    final EasyVideoCallback easyVideoCallback = new EasyVideoCallback() {
        @Override
        public void onStarted(EasyVideoPlayer player) {

        }

        @Override
        public void onPaused(EasyVideoPlayer player) {
            // Make sure the player stops playing if the user presses the home button.
            player.pause();
        }

        @Override
        public void onPreparing(EasyVideoPlayer player) {
            Log.v("log","mesg");
        }

        @Override
        public void onPrepared(EasyVideoPlayer player) {
            Log.v("log","mesg");

            isSDKStarted = true;
            startDetector();
            player.start();
            startSDKButton.setText("stop Camera");
        }

        @Override
        public void onBuffering(int percent) {

        }

        @Override
        public void onError(EasyVideoPlayer player, Exception e) {

        }

        @Override
        public void onCompletion(EasyVideoPlayer player) {
            if(!flag){
                flag =true;
            }else{
                Log.v("log","mesg");
                calculateImageAndLabel();
                Intent intent = new Intent(MainActivity.this,ResultActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("label1",imageAndLabel[0].name);
                intent.putExtra("label2",imageAndLabel[1].name);
                intent.putExtra("label3",imageAndLabel[2].name);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                imageAndLabel[0].image.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] compressedByteArray = stream.toByteArray();
//                intent.putExtra("image1",compressedByteArray);
//
//                imageAndLabel[1].image.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] compressedByteArray1 = stream.toByteArray();
//                intent.putExtra("image2",compressedByteArray1);
//
//                imageAndLabel[2].image.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] compressedByteArray2 = stream.toByteArray();
//                intent.putExtra("image3",compressedByteArray2);
                for(int i=0;i<3;i++)
                    storeImage(imageAndLabel[i].image,i);
                finish();
                startActivity(intent);

            }

//            player.start();
        }

        @Override
        public void onRetry(EasyVideoPlayer player, Uri source) {

        }

        @Override
        public void onSubmit(EasyVideoPlayer player, Uri source) {
            Log.v("log","mesg");
        }
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location = getIntent().getIntExtra("location",0);
        Constant.location = location;
        startSDKButton = (Button) findViewById(R.id.sdk_start_button);
        startSDKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSDKStarted) {
                    isSDKStarted = false;
                    stopDetector();
                    startSDKButton.setText("Start Camera");
                } else {
                    isSDKStarted = true;
                    startDetector();
                    startSDKButton.setText("Stop Camera");
                }
            }
        });
        startSDKButton.setText("Start Camera");
        //We create a custom SurfaceView that resizes itself to match the aspect ratio of the incoming camera frames
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        cameraPreview = new SurfaceView(this) {
            @Override
            public void onMeasure(int widthSpec, int heightSpec) {
                int measureWidth = MeasureSpec.getSize(widthSpec);
                int measureHeight = MeasureSpec.getSize(heightSpec);
                int width;
                int height;
                if (previewHeight == 0 || previewWidth == 0) {
                    width = measureWidth;
                    height = measureHeight;
                } else {
                    float viewAspectRatio = (float)measureWidth/measureHeight;
                    float cameraPreviewAspectRatio = (float) previewWidth/previewHeight;

                    width = previewWidth/4;
                    height = previewWidth/4;
                }
                setMeasuredDimension(1,1);
            }
        };
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP|RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
        cameraPreview.setLayoutParams(params);
        cameraPreview.setAlpha(0);
        mainLayout.addView(cameraPreview,0);
        int rate = 10;
        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview);
        detector.setDetectSmile(true);
        detector.setDetectBrowRaise(true);
        detector.setDetectCheekRaise(true);
        detector.setDetectEyeClosure(true);
        detector.setDetectJawDrop(true);
        detector.setDetectMouthOpen(true);
        detector.setDetectSmirk(true);
        detector.setDetectUpperLipRaise(true);
        detector.setDetectGender(true);

        detector.setImageListener(this);
        detector.setOnCameraEventListener(this);
        detector.setMaxProcessRate(rate);

        player = (EasyVideoPlayer)findViewById(R.id.player);
        player.setCallback(easyVideoCallback);

        Uri uri = Uri.fromFile(new File( Environment.getExternalStorageDirectory()+Constant.video[location]));
//        Toast.makeText()
        player.setSource(uri);
        player.start();
        player.setAutoPlay(true);
        player.setLoop(false);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSDKStarted) {
            startDetector();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (detector.isRunning()) {
            detector.stop();
        }

    }

    void startDetector() {
        if (!detector.isRunning()) {
            detector.start();
        }
    }

    void stopDetector() {
        if (detector.isRunning()) {
            detector.stop();
        }
    }

    void switchCamera(CameraDetector.CameraType type) {
        detector.setCameraType(type);
    }

    @Override
    public void onImageResults(List<Face> list, Frame frame, float v) {
        if (list == null)
            return;
        if (list.size() == 0) {
//            smileTextView.setText("NO FACE");
//            ageTextView.setText("");
        } else {
            Face face = list.get(0);
//            smileTextView.setText(String.format("SMILE\n%.2f",face.expressions.getSmile()));
            smileData.add(face.expressions.getSmile());
            browData.add(face.expressions.getBrowRaise());
            cheekRaiseData.add(face.expressions.getCheekRaise());
            eyeClosureData.add(face.expressions.getEyeClosure());
            jawDropData.add(face.expressions.getJawDrop());
            mouthOpenData.add(face.expressions.getMouthOpen());
            smirkData.add(face.expressions.getSmirk());
            upperLipData.add(face.expressions.getUpperLipRaise());
        }
    }


    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void onCameraSizeSelected(int width, int height, Frame.ROTATE rotate) {
        if (rotate == Frame.ROTATE.BY_90_CCW || rotate == Frame.ROTATE.BY_90_CW) {
            previewWidth = height;
            previewHeight = width;
        } else {
            previewHeight = height;
            previewWidth = width;
        }
        cameraPreview.requestLayout();
    }
    public void calculateImageAndLabel(){
        ArrayList<Float> ans = new ArrayList<>();
        for(int i=0;i<smileData.size();i++){
            ans.add(smileData.get(i)+browData.get(i)+cheekRaiseData.get(i)+eyeClosureData.get(i)+jawDropData.get(i)+mouthOpenData.get(i)
                    +smirkData.get(i)+upperLipData.get(i));
        }
//        ArrayList<Float> max3 = new ArrayList<>();
//        max3.add(0f);max3.add(0f);max3.add(0f);
        float max3[][] = new float[2][3];
        for(int i=0;i<ans.size();i++){
            if(max3[1][0]<ans.get(i)){
                max3[1][0]=ans.get(i);
                Arrays.sort(max3[1]);
                i +=10;
            }
        }
        int maxIndex[] = new int[3];
        for(int i=0;i<3;i++){
            maxIndex[i] = ans.indexOf(max3[1][i]);
        }
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

        mediaMetadataRetriever.setDataSource(Environment.getExternalStorageDirectory()+Constant.video[location]);
        for(int i=0;i<3;i++){
            Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(maxIndex[i]*100000); //unit in microsecond
            imageAndLabel[i] = new ImageAndLabel();
            imageAndLabel[i].image = bmFrame;
            switch (location){
                case 0: if(maxIndex[i]/10 < 25){
                    imageAndLabel[i].name = "Casinos";
                }else if(maxIndex[i]/10<29){
                    imageAndLabel[i].name = "Bellagio Fountain Show";
                }else if(maxIndex[i]/10 < 52){
                    imageAndLabel[i].name = "V:The Ultimate Variety Show";
                }else if(maxIndex[i]/10<61){
                    imageAndLabel[i].name = "Freemont Street";
                }else{
                    imageAndLabel[i].name = "Roman Themed Caesar Palace";
                }
                case 1: if(maxIndex[i]/10 < 15){
                    imageAndLabel[i].name = "Northern Peninsula";
                }else if(maxIndex[i]/10<38){
                    imageAndLabel[i].name = "Union Square";
                }else if(maxIndex[i]/10 < 60){
                    imageAndLabel[i].name = "Chinatown";
                }else if(maxIndex[i]/10<78){
                    imageAndLabel[i].name = "Embarcadaro";
                }else if(maxIndex[i]/10 < 95){
                    imageAndLabel[i].name = "Fisherman's Warf";
                }else{
                    imageAndLabel[i].name = "Pier 39";
                }

                case 2: if(maxIndex[i]/10 < 25){
                    imageAndLabel[i].name = "Palm Cove Beach";
                }else if(maxIndex[i]/10<41){
                    imageAndLabel[i].name = "Great Barrier Reef";
                }else if(maxIndex[i]/10 < 50){
                    imageAndLabel[i].name = "Daintree Forest";
                }else if(maxIndex[i]/10<57){
                    imageAndLabel[i].name = "Brisbane";
                }else if(maxIndex[i]/10<59){
                    imageAndLabel[i].name = "Kayaking";
                }else if(maxIndex[i]/10 < 68){
                    imageAndLabel[i].name = "Gold Coast";
                }else{
                    imageAndLabel[i].name = "Sunshine Coast";
                }
            }
        }


    }

    private void storeImage(Bitmap image,int i) {
        File pictureFile = getOutputMediaFile(i);
        if (pictureFile == null) {
            Log.d("abc",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("anc", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("ana", "Error accessing file: " + e.getMessage());
        }
    }
    private  File getOutputMediaFile(int i){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File( Environment.getExternalStorageDirectory()
                + "/Download");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String mImageName="image"+ i +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
}

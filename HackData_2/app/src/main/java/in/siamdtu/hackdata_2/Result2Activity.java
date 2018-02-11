package in.siamdtu.hackdata_2;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static in.siamdtu.hackdata_2.R.id.label1;

public class Result2Activity extends AppCompatActivity {
    public class DoubleString {
        String image, name;
    }
    ImageView imageView[] ;
    TextView label[];

    Bitmap bitmap;
    DoubleString doubleStringArray[];
    ArrayList<String> selectedFilePath;
    int location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result2);

        imageView = new ImageView[3];
        label = new TextView[3];
        selectedFilePath = (ArrayList<String>) getIntent().getSerializableExtra("UriList");
        location = Constant.location;
        doubleStringArray = new DoubleString[3];
        imageView[0] = (ImageView)findViewById(R.id.image1);
        imageView[1] = (ImageView)findViewById(R.id.image2);
        imageView[2] = (ImageView)findViewById(R.id.image3);
        label[0] = (TextView)findViewById(label1);
        label[1] = (TextView)findViewById(R.id.label2);
        label[2] = (TextView)findViewById(R.id.label3);
        sendPic();

    }

    private void sendPic() {

        try {
            final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
            final Map<String, String> jsonParams = new HashMap<String, String>();
            for (int i = 0; i < selectedFilePath.size(); i++) {
                Log.i("Result2Activity", selectedFilePath.get(i));
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(selectedFilePath.get(i))));
                String image = getStringImage(bitmap);
                jsonParams.put("image" + (i + 1), image);
            }

            String url1 = "http://192.168.157.103:8079/image";
            JsonObjectRequest myRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url1,
                    new JSONObject(jsonParams),

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
//                                verificationSuccess(response);
                            loading.dismiss();
                            Log.i("JSON Response", response + "");
//                            for(Iterator<String> abc = response.keys();abc.hasNext(); ){
//                                //
//                            }
                            Iterator<String> iter = response.keys();
                            String type1 = iter.next();
                            String type2 = iter.next();
                            String type3 = iter.next();
                            type1.replace(" ", "");
                            type2.replace(" ", "");
                            type3.replace(" ", "");
                            String lat,lon;
                            switch (location){
                                case 1:lat ="-23.558236" ;lon="144.614808";break;
                                case 2:lat = "37.769350";lon="-122.436572";break;
                                case 3:lat = "36.218698";lon="-115.257246";break;
                                default:lat ="-23.558236" ;lon="144.614808";break;
                            }

                            doubleStringArray[0] =  locationAndImage(type1, lat, lon,0);
                            doubleStringArray[1] = locationAndImage(type2, lat, lon,1);
                            doubleStringArray[2] = locationAndImage(type3, lat, lon,2);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
//                                verificationFailed(error);
                            volleyError.printStackTrace();
                            loading.dismiss();

//                                Toast.makeText(SpeachQuestionActivity.this, "" + volleyError, Toast.LENGTH_LONG).show();
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };
            myRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(myRequest);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 10, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        System.out.println(encodedImage.length());
        return encodedImage;
    }

    private DoubleString locationAndImage(String type, String lat, String lon,final int i) {
        final DoubleString doubleString = new DoubleString();
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?location=" + lat + "," + lon +
                "&radius=5000&type=" + type + "&key=AIzaSyDPW4_vznuV9FuGmc6tpxtBp-ZeUWgSw_A";

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url, null,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String name, image;
                        try {
                            JSONArray result = response.getJSONArray("results");
                            name = result.getJSONObject(i+3).getString("name");
                            image = result.getJSONObject(i+3).getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                            doubleString.image = image;
                            doubleString.name = name;
                            label[i].setText(name);
//                            imageVi
                            Picasso.with(Result2Activity.this).load(Constant.photourl+image+"&key="+"AIzaSyDPW4_vznuV9FuGmc6tpxtBp-ZeUWgSw_A").into(imageView[i]);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {

                        }
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
        return doubleString;
    }

}

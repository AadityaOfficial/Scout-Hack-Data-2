package in.siamdtu.hackdata_2;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Parth on 11-02-2018.
 */

public class ImageAndLabel implements Serializable {
    Bitmap image;
    String  name;

    public ImageAndLabel() {
    }

    public ImageAndLabel(Bitmap image, String name) {
        this.image = image;
        this.name = name;
    }


}

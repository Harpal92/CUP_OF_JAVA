package com.example.intrack;

import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.caverock.androidsvg.SVG;
import java.io.InputStream;
import com.github.chrisbanes.photoview.PhotoView;



public class MapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        PhotoView mapView = findViewById(R.id.svgMap);



        try {
            InputStream inputStream = getAssets().open("map_layout.svg");  // file name should match yours
            SVG svg = SVG.getFromInputStream(inputStream);
            PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
            mapView.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null);
            mapView.setImageDrawable(drawable);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading map", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}

package com.example.intrack;

import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

public class MapActivity extends AppCompatActivity {

    private FrameLayout mapContainer;
    private PhotoView mapView;
    private PhotoViewAttacher attacher;
    private ImageView userMarker;
    private ImageView destinationMarker;
    private PathView pathView;

    private final Handler handler = new Handler();
    private final Matrix tempMatrix = new Matrix();

    private float userX = 500;
    private float userY = 9800;

    private boolean hasDestination = false;
    private float destinationX = 0;
    private float destinationY = 0;

    private final int delayMillis = 250;
    private final float movementStep = 5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapContainer = findViewById(R.id.mapContainer);
        mapView = findViewById(R.id.mapView);
        userMarker = findViewById(R.id.userMarker);
        destinationMarker = findViewById(R.id.destinationMarker);
        pathView = findViewById(R.id.pathView);

        mapView.setImageResource(R.drawable.map_layout);
        mapView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        attacher = mapView.getAttacher();

        updateUserMarker(userX, userY);

        mapView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                float[] coords = convertTouchToImageCoords(event.getX(), event.getY());
                destinationX = coords[0];
                destinationY = coords[1];
                updateDestinationMarker(destinationX, destinationY);
                hasDestination = true;
                drawPath(userX, userY, destinationX, destinationY);
            }
            return true;
        });

        handler.postDelayed(movementRunnable, delayMillis);
    }

    private final Runnable movementRunnable = new Runnable() {
        @Override
        public void run() {
            if (hasDestination) {
                float dx = destinationX - userX;
                float dy = destinationY - userY;
                float distance = (float) Math.hypot(dx, dy);

                if (distance < 20f) {
                    Toast.makeText(MapActivity.this, "You have arrived!", Toast.LENGTH_SHORT).show();
                    handler.removeCallbacks(this);
                    return;
                }

                float nx = dx / distance;
                float ny = dy / distance;

                userX += nx * movementStep;
                userY += ny * movementStep;
                updateUserMarker(userX, userY);
                drawPath(userX, userY, destinationX, destinationY);
                centerMapOnUser(userX, userY);
            }

            handler.postDelayed(this, delayMillis);
        }
    };

    private void updateUserMarker(float x, float y) {
        userMarker.setX(x - userMarker.getWidth() / 2f);
        userMarker.setY(y - userMarker.getHeight() / 2f);
    }

    private void updateDestinationMarker(float x, float y) {
        destinationMarker.setVisibility(View.VISIBLE);
        destinationMarker.setX(x - destinationMarker.getWidth() / 2f);
        destinationMarker.setY(y - destinationMarker.getHeight() / 2f);
    }

    private void drawPath(float fromX, float fromY, float toX, float toY) {
        pathView.setPath(fromX, fromY, toX, toY);
    }

    // 🧭 Center map view on user position
    private void centerMapOnUser(float userX, float userY) {
        float scale = attacher.getScale();
        float viewWidth = mapView.getWidth();
        float viewHeight = mapView.getHeight();

        float centerX = userX * scale - viewWidth / 2f;
        float centerY = userY * scale - viewHeight / 2f;

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(-centerX, -centerY);

        attacher.setDisplayMatrix(matrix);
    }

    private float[] convertTouchToImageCoords(float touchX, float touchY) {
        float[] coords = new float[]{touchX, touchY};
        Matrix currentMatrix = new Matrix();
        attacher.getDisplayMatrix(currentMatrix); // ✅ FIXED: get the current matrix
        currentMatrix.invert(tempMatrix);         // ✅ invert it for coordinate conversion
        tempMatrix.mapPoints(coords);             // ✅ map screen to image
        return coords;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(movementRunnable);
    }
}

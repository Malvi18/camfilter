package recorder.soundrecorder.com.fourarc.camfilter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button camera,gallery;
    private static final int  CAMERA_REQUEST_CODE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        camera = findViewById(R.id.btn_camera);
        gallery=findViewById(R.id.btn_gallery);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap;
        bitmap = (Bitmap) data.getExtras().get("data");
        EditImageActivity.phobitmap =bitmap;
        Intent intent = new Intent(getApplicationContext(), EditImageActivity.class);
        startActivity(intent);
    }
}

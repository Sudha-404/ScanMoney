package com.example.loginregister;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginregister.ml.Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class uploadimage extends AppCompatActivity {

    TextView result;
    ImageView imageView;
    Button logout,upload;
    Bitmap img;
    Uri imageurl;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    private StorageReference ref = FirebaseStorage.getInstance().getReference();
    ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        imageView = findViewById(R.id.imageView);
        upload = findViewById(R.id.signup3);
        logout = findViewById(R.id.signup);
        result = findViewById(R.id.result);


        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK&& result.getData()!= null) {
                   imageurl= result.getData().getData();
                    imageView.setImageURI(imageurl);
                }
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                // startActivityForResult(gallery,2);
                activityResultLauncher.launch(gallery); }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageurl != null) {
                    img = Bitmap.createScaledBitmap(img, 64, 64, true);
                    try {
                        Model model = Model.newInstance(getApplicationContext());
                        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
                        // Creates inputs for reference.
                        ByteBuffer input = ByteBuffer.allocateDirect(64 * 64 * 3 * 4).order(ByteOrder.nativeOrder());
                        for (int y = 0; y < 64; y++) {
                            for (int x = 0; x < 64; x++) {
                                int px = img.getPixel(x, y);

                                // Get channel values from the pixel value.
                                int r = Color.red(px);
                                int g = Color.green(px);
                                int b = Color.blue(px);

                                // Normalize channel values to [-1.0, 1.0]. This requirement depends
                                // on the model. For example, some models might require values to be
                                // normalized to the range [0.0, 1.0] instead.
                                float rf = (r - 127) / 255.0f;
                                float gf = (g - 127) / 255.0f;
                                float bf = (b - 127) / 255.0f;

                                input.putFloat(rf);
                                input.putFloat(gf);
                                input.putFloat(bf);
                            }
                        }


                        inputFeature0.loadBuffer(input);

                        // Runs model inference and gets result.
                        Model.Outputs outputs = model.process(inputFeature0);
                        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                        float[] confid = outputFeature0.getFloatArray();
                        int max = 0;
                        float maxconfid = 0;
                        for (int i = 0; i < confid.length; i++) {
                            if (confid[i] > maxconfid) {
                                maxconfid = confid[i];
                                max = i;
                            }
                        }


                        String[] classes = {"Fake", "Real"};
                        result.setText(classes[max]);


                        // Releases model resources if no longer used.
                        model.close();


                    } catch (IOException e) {
                        // TODO Handle the exception
                    }

                } else {
                    Toast.makeText(uploadimage.this,"Please select Image",Toast.LENGTH_SHORT).show();

                }
            }
        }); logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }



    private void uploadToFirebase(Uri url) {

        StorageReference file = ref.child(System.currentTimeMillis()+"."+getFileExtension(url));
        file.putFile(url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri url) {

                        Mysingleton model = new Mysingleton(url.toString());
                        String modelid = root.push().getKey();
                        root.child(modelid).setValue(model);
                        Toast.makeText(uploadimage.this,"Uploaded successfully",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(uploadimage.this, "Uploading Failed!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri gurl) {

        ContentResolver CR= getContentResolver();
        MimeTypeMap mi = MimeTypeMap.getSingleton();
        return mi.getExtensionFromMimeType(CR.getType(gurl));
    }

}

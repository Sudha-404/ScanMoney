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
import android.widget.ProgressBar;
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
    int imagesize = 64;

   //* private DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
   // private StorageReference ref = FirebaseStorage.getInstance().getReference();
    ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        imageView = findViewById(R.id.imageView);
        upload = findViewById(R.id.signup3);
        logout = findViewById(R.id.signup);
        result = findViewById(R.id.result);






        imageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                }else{
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        }); upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(camerIntent,1);
            }
        });



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }



  /** private void uploadToFirebase(Uri url) {

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
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(uploadimage.this,"Uploaded successfully",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(uploadimage.this, "Uploading Failed!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri gurl) {

        ContentResolver CR= getContentResolver();
        MimeTypeMap mi = MimeTypeMap.getSingleton();
        return mi.getExtensionFromMimeType(CR.getType(gurl));
    }*/

  public void classifyimage(Bitmap img){
      try {
          Model model = Model.newInstance(getApplicationContext());

          // Creates inputs for reference.

          TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
          ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4*imagesize*imagesize*3);
         int[] intvalues = new int[imagesize*imagesize];
         img.getPixels(intvalues, 0, img.getWidth(), 0,0, img.getWidth(), img.getHeight());
         int px=0;
         //iterate over each pixel and extract R,G, and B values. add those values individually to the byte buffer.
         for (int i = 0; i <imagesize;i++){
             for(int j = 0;j< imagesize;j++){
                 int value = intvalues[px++];//RGB
                 byteBuffer.putFloat(((value>>16)&0xFF)*(1.f/1));
                 byteBuffer.putFloat(((value>>18)&0xFF)*(1.f/1));
                 byteBuffer.putFloat((value&0xFF)*(1.f/1));
             }
         }





          inputFeature0.loadBuffer(byteBuffer);//load bytebuffer contain bytes og images

          // Runs model inference and gets result.
          Model.Outputs outputs = model.process(inputFeature0);// input
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
  }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 3){
                img =(Bitmap) data.getExtras().get("data");
                int dimension = Math.min(img.getWidth(),img.getHeight());
                img = ThumbnailUtils.extractThumbnail(img,dimension,dimension);
                imageView.setImageBitmap(img);


                img = Bitmap.createScaledBitmap(img,imagesize, imagesize, false);
                classifyimage(img);
            }else{
                Uri uri = data.getData();
                 img = null;

                 try{
                     img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                 }catch (IOException e){
                     e.printStackTrace();
                 }
                 imageView.setImageBitmap(img);

                 img = Bitmap.createScaledBitmap(img, imagesize, imagesize, false);
                classifyimage(img);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

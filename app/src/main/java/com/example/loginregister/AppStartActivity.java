package com.example.loginregister;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

public class AppStartActivity extends AppCompatActivity {
    private Button selectimg,uploadimg;
    private ImageView imageview;
    private ProgressBar progressBar;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    private StorageReference ref = FirebaseStorage.getInstance().getReference();
   ActivityResultLauncher<Intent> activityResultLauncher;
   private  Uri   imageurl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);

    selectimg= findViewById(R.id.signup);
    uploadimg= findViewById(R.id.signup2);
    imageview= findViewById(R.id.imageView);
    progressBar = findViewById(R.id.progressBar);

    progressBar.setVisibility(View.INVISIBLE);


    activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode()==RESULT_OK&& result.getData()!= null) {
          imageurl= result.getData().getData();
         imageview.setImageURI(imageurl);
            }
        }
    });



    imageview.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           Intent gallery = new Intent();
           gallery.setAction(Intent.ACTION_GET_CONTENT);
           gallery.setType("image/*");
          // startActivityForResult(gallery,2);
            activityResultLauncher.launch(gallery);
        }
    });

    uploadimg.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (imageurl != null){
                    uploadToFirebase(imageurl);
            }else{
                Toast.makeText(AppStartActivity.this,"Please select Image",Toast.LENGTH_SHORT).show();

            }
        }
    });

    selectimg.setOnClickListener(new View.OnClickListener() {
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
                            progressBar.setVisibility(View.INVISIBLE);
                               Mysingleton model = new Mysingleton(url.toString());
                                 String modelid = root.push().getKey();
                                 root.child(modelid).setValue(model);
                             Toast.makeText(AppStartActivity.this,"Uploaded successfully",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AppStartActivity.this, "Uploading Failed!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri gurl) {

        ContentResolver CR= getContentResolver();
        MimeTypeMap mi = MimeTypeMap.getSingleton();
        return mi.getExtensionFromMimeType(CR.getType(gurl));
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode==2&& resultCode ==RESULT_OK&& data!= null){
//            imageurl= data.getData();
//            imageview.setImageURI(imageurl);
//        }
//    }


     }


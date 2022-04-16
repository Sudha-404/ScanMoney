package com.example.loginregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    /** variables for different objects **/
    MaterialEditText username,email,password,fullname;
    Button signup;
    TextView acc;
    private FirebaseAuth mAuth;



      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.username);
          email = findViewById(R.id.email);
          password = findViewById(R.id.password);
          fullname = findViewById(R.id.fullname);
          signup = findViewById(R.id.signup);
          acc = findViewById(R.id.registered);

          mAuth= FirebaseAuth.getInstance();  /** current instance of database from firebase to perform various operations**/



          /**if (mAuth.getCurrentUser() != null){
              startActivity(new Intent(getApplicationContext(),MainActivity.class));  if registered success then go to main activity
               finish();
          }**/
          signup.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  String  txtUserName = username.getText().toString();  /** convert into string as it stay as object **/
                  String  txtEmail = email.getText().toString();
                  String  txtPassword = password.getText().toString();
                  String  txtFullname= fullname.getText().toString();


                  if(!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()){
                      email.setError("Please provide valid email.");
                      return;
                  }

                  if (TextUtils.isEmpty(txtEmail)){
                   email.setError("Email is required.");
                   return;
               }
                  if (TextUtils.isEmpty(txtFullname)){
                      fullname.setError("Full name is required.");
                      return;
                  }
                  if (TextUtils.isEmpty(txtUserName)){
                      username.setError("Username is required.");
                      return;
                  }


                  if (TextUtils.isEmpty(txtPassword)){
                      password.setError("Password is required.");
                      return;
                  }
               if(txtPassword.length()<6){
                   password.setError("Password must be at least 6 Characters.");
                   return;
               }





               // register the user in firebase
                  mAuth.createUserWithEmailAndPassword(txtEmail,txtPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {


                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()) {
                           /** if (task .issuccessful()){
                            FirebaseUser fbuser = mAuth.getCurrentUser();
                            fbuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener</Void>()(
                            @Override
                            public void onSuccess(Void aVoid){
                            Toast.makeText( RegisterActivity.this,"Verification Email has been Sent",Toast.LENGTH_SHORT().show();

                            }}).addOnFailureListener(new OnFailureListener(){
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "OnFailure Email not sent " + e.getmessage());
                            }



                             }**/
                           Toast.makeText(RegisterActivity.this,"User Created ",Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(getApplicationContext(),MainActivity.class));/**  if registered success then go to main activity **/
                       }else{
                           Toast.makeText(RegisterActivity.this,"Error occurred !"+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                       }
                   }
               });



              }
          });
          acc.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  startActivity(new Intent(getApplicationContext(),MainActivity.class));
              }
          });


    }



}


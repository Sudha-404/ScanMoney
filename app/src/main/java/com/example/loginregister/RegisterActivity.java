package com.example.loginregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    /** variables for different objects **/

    MaterialEditText username,email,password,number;
    Button signup;
    TextView acc;

   ProgressBar progressBar;
    private FirebaseAuth mAuth;
    boolean passwordVisible;






      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.username);
          email = findViewById(R.id.email);
          password = findViewById(R.id.password);
           number = findViewById(R.id.phone);
          signup = findViewById(R.id.signup);
          acc = findViewById(R.id.registered);
           progressBar=findViewById(R.id.progressBar3);



          mAuth= FirebaseAuth.getInstance();  /** current instance of database from firebase to perform various operations**/



          /**if (mAuth.getCurrentUser() != null){
              startActivity(new Intent(getApplicationContext(),MainActivity.class));  if registered success then go to main activity
               finish();
          }**/
          password.setOnTouchListener(new View.OnTouchListener() {
              @SuppressLint("ClickableViewAccessibility")
              @Override
              public boolean onTouch(View v, MotionEvent event) {
                  final int Right = 2;
                  if(event.getAction()==MotionEvent.ACTION_UP){
                      if(event.getRawX()>=password.getRight()-password.getCompoundDrawables()[Right].getBounds().width()){
                          int sel = password.getSelectionEnd();
                          if(passwordVisible){
                              password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_off_24,0);
                              password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                              passwordVisible=false;
                          }else{
                              password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_24,0);
                              password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                              passwordVisible=true;
                          }
                          password.setSelection(sel);
                          return true;
                      }
                  }
                  return false;
              }
          });






          signup.setOnClickListener(new View.OnClickListener() {
              @SuppressLint("ResourceType")
              @Override
              public void onClick(View v) {

                  String  txtUserName = username.getText().toString();  /** convert into string as it stay as object **/
                  String  txtEmail = email.getText().toString();
                  String  txtPassword = password.getText().toString();
                 String txtnumber = number.getText().toString();


                  if (TextUtils.isEmpty(txtUserName)){
                      username.setError("Fullame is required.");
                      return;
                  } if (txtUserName.length()>30){
                      username.setError("Fullname too long");
                      return;

                  }

                  if (TextUtils.isEmpty(txtEmail)) {
                      email.setError("Email is required.");
                      return;
                  }




                  if(!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()){
                      email.setError("Please provide valid email.");
                      return;
                  }




                  if (TextUtils.isEmpty(txtPassword)){
                      password.setError("Password is required.");
                      return;
                  }
              if(txtPassword.length()<6){
               password.setError("Password should be at least 6 characters");
               return;
              }
              if (!txtPassword.matches("(.*[A-Z].*)")){
                  password.setError("Password should have minimum one uppercase");
                      return;
              }
                  if (!txtPassword.matches("(.*[a-z].*)")){

                      password.setError("Password should have minimum one lowercase");
                      return;
                  }
                  if (!txtPassword.matches("(.*[0-9].*)")){

                      password.setError("Password should have minimum one digit number");
                      return;
                  }
                  if (!txtPassword.matches("^(.*[!@#$%^&*()_+=?,.<>]).*$")){

                      password.setError("Password should have minimum one special character");
                      return;
                  }
                  if (TextUtils.isEmpty(txtnumber)){
                      number.setError("Phone number is required.");
                      return;}
                  if (!(txtnumber.length() ==10)){
                      number.setError("Phone number should have 10 digits.");
                      return;}


                 progressBar.setVisibility(View.VISIBLE);










                  // register the user in firebase
                  mAuth.createUserWithEmailAndPassword(txtEmail,txtPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {


                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()) {

                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        Toast.makeText(RegisterActivity.this,"User Created.Please check your email for verification. ",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));

                                    }
                                }
                            });
 /**
                           fbuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void unused) {
                                   Toast.makeText(RegisterActivity.this, "Verification email has been sent", Toast.LENGTH_SHORT).show();
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                  /** Log.d("tag", "Email not sent"+e.getMessage());
                                   Log.d("tag","onFailure:Email not sent "+e.getMessage());
                               }
                           });**/




                           /**Toast.makeText(RegisterActivity.this,"User Created.Please check your email for verification. ",Toast.LENGTH_SHORT).show();
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




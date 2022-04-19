package com.example.loginregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
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

public class MainActivity extends AppCompatActivity  {



    MaterialEditText email, password, message;
    Button signin,verifycode;
    TextView create,forget;
    private ProgressBar progressBar;
    boolean passwordVisible;






    private FirebaseAuth mAuth;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        signin = findViewById(R.id.Login);
        create = findViewById(R.id.checkbox);
        forget = findViewById(R.id.forget);
        verifycode = findViewById(R.id.verifyemail);
        progressBar = findViewById(R.id.progressBarmain);
        message = findViewById(R.id.message);
      //  FirebaseUser Fuser = mAuth.getCurrentUser();
        progressBar.setVisibility(View.INVISIBLE);

          /**if (!Fuser.isEmailVerified()){
              verifycode.setVisibility(View.VISIBLE);
              message.setVisibility(View.VISIBLE);

              verifycode.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

                      Fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void unused) {
                              Toast.makeText(v.getContext(), "Verification email has been sent", Toast.LENGTH_SHORT).show();
                          }
                      }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              Log.d("tag", "Email not sent"+e.getMessage());
                              /** Log.d(,"onFailure:Email not sent "+e.getMessage());
                          }
                      });
                  }
              });

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






        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = email.getText().toString().trim();
                String pwd = password.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(user).matches()){
                    email.setError("Please provide valid email.");
                    return;
                }

                if (TextUtils.isEmpty(user)){
                    email.setError("Email is required.");
                    return;
                }


                if (TextUtils.isEmpty(pwd)){
                    password.setError("Password is required.");
                    return;
                }
                if(pwd.length()<6){
                    password.setError("Password must be at least 6 Characters.");
                    return;
                }








                mAuth.signInWithEmailAndPassword(user,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(mAuth.getCurrentUser().isEmailVerified()){
                            Toast.makeText(MainActivity.this,"Sign in Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),AppStartActivity.class));
                                progressBar.setVisibility(View.INVISIBLE);
                        }else{
                                Toast.makeText(MainActivity.this, "Please verify your email address",Toast.LENGTH_SHORT).show();

                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Error!"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialEditText resetemail= new MaterialEditText(v.getContext());
                AlertDialog.Builder pwdresetdialog = new AlertDialog.Builder(v.getContext());
                pwdresetdialog.setTitle("Reset Password ?");
                pwdresetdialog.setMessage("Please enter your email to received reset link. ");
                pwdresetdialog.setView(resetemail);


                pwdresetdialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = resetemail.getText().toString();
                        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void avoid) {
                                Toast.makeText(MainActivity.this, "Resent link is sent to your email",Toast.LENGTH_SHORT).show();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error! Resent Link not found"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                pwdresetdialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                    }
                });

                pwdresetdialog.create().show();
            }
        });

    }



}



   /*final FirebaseUser users = mAuth.getCurrentUser();
 *
 *
 * resend code = findViewById(R.id.resendcode)
 verifyMag = findViewById(R.id.verifyMag)

 if(!users.isEmailCerified()){
 resend code.setVisibility(View.VISIBLE);
 resend code.setVisibility(View.VISIBLE);
 resend code.setOnClickListener(new view.OnClickListener()){
@Override
public void onClick(View v) {
user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener</Void>()(
@Override
public void onSuccess(Void aVoid){
Toast.makeText(v.getcontext,"Verification Email has been Sent",Toast.LENGTH_SHORT().show();

}}).addOnFailureListener(new OnFailureListener(){
@Override
public void onFailure(@NonNull Exception e) {
Log.d("tag", "OnFailure Email not sent " + e.getmessage());
}
}
});
 }
 }


**/
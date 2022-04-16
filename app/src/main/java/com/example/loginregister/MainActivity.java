package com.example.loginregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {



    MaterialEditText email, password;
    Button signin;
    TextView create,forget;

    private FirebaseAuth mAuth;

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
                            Toast.makeText(MainActivity.this,"Sign in Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),AppStartActivity.class));
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Error !"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
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

                pwdresetdialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // retrieve email and reset link
                        String email = resetemail.getText().toString();
                        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                 Toast.makeText(MainActivity.this,"Resent link is sent to your email",Toast.LENGTH_SHORT).show();
                            }


                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Error ! Cannot sent Resent Link "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
                pwdresetdialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
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
package com.catatanku.ontime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivitySignup extends AppCompatActivity {
    private Button btndaftar;
    private TextView tvlogin;
    private EditText emailbaru,passwordbaru;
    FirebaseAuth mAuth;
    boolean passwordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        emailbaru=findViewById(R.id.et_emailbaru);

        mAuth = FirebaseAuth.getInstance();

        passwordbaru=findViewById(R.id.et_passwordbaru);
        passwordbaru.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int Right =2;
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getRawX()>=passwordbaru.getRight()-passwordbaru.getCompoundDrawables()[Right].getBounds().width()){
                        int selection=passwordbaru.getSelectionEnd();
                        if(passwordVisible){
                            // taruh foto disini
                            passwordbaru.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_close,0);
                            // untuk hide password
                            passwordbaru.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }else{
                            // taruh vector mata
                            passwordbaru.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_unclose,0);
                            // untuk show password
                            passwordbaru.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;
                        }
                        passwordbaru.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        // Tombol Signup
        btndaftar=findViewById(R.id.btnDaftar);
        btndaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivitySignup.this,ActivityLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // mengatur agar ketika mengklik tombol kembali tidak looping pada activity sebelumnya
                startActivity(intent);
                createUser();
            }
        });
        // Text view login
        tvlogin=findViewById(R.id.tv_login);
        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivitySignup.this,ActivityLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // mengatur agar ketika mengklik tombol kembali tidak looping pada activity sebelumnya
                startActivity(intent);
            }
        });

    }

    private void createUser() {
        String email = emailbaru.getText().toString();
        String password = passwordbaru.getText().toString();
        if (TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            emailbaru.setError("Email don't be empty");
            passwordbaru.setError("Password don't be empty");
            emailbaru.requestFocus();
            passwordbaru.requestFocus();

        }else if(password.length()<6){
            Toast.makeText(getApplicationContext(),"Password must be 6 characters!", Toast.LENGTH_SHORT).show();
        }else{
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.sendEmailVerification();
                        Toast.makeText(getApplicationContext(),"Check your email to verify your account", Toast.LENGTH_LONG).show();
                        finish();
                    }else if (!task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Cannot Regist, Try Again", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
}
package com.catatanku.ontime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class ActivityLogin extends AppCompatActivity {
    private Button login;
    private TextView daftar,forgetpass;
    private EditText email,password;
    FirebaseAuth mAuth;
    boolean passwordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.et_email);
        forgetpass=findViewById(R.id.forgetpass);
        mAuth = FirebaseAuth.getInstance();

        // untuk hide dan show password https://www.youtube.com/watch?v=iYr680_j3Jc
        password=findViewById(R.id.et_password);
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int Right =2;
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getRawX()>=password.getRight()-password.getCompoundDrawables()[Right].getBounds().width()){
                        int selection=password.getSelectionEnd();
                        if(passwordVisible){
                            // text visible
                            // taruh foto disini
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_close,0);
                            // untuk hide password
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }else{
                            // text terlihat
                            // taruh foto disini
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_unclose,0);
                            // untuk show password
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;
                        }
                        password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        login=findViewById(R.id.btnLogin);
        // Tombol Login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().length() <= 0 || password.getText().length() <= 0){
                    Toast.makeText(getApplicationContext(),"please fill in all data!", Toast.LENGTH_SHORT).show();
                }else{
                    login(email.getText().toString(), password.getText().toString());
                }
            }
        });

        daftar=findViewById(R.id.tv_daftar);
        // Tombol Daftar
        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityLogin.this,ActivitySignup.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // mengatur agar ketika mengklik tombol kembali tidak looping pada activity sebelumnya
                startActivity(intent);
            }
        });
        // Tombol Daftar
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityLogin.this,Activity_Forget.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // mengatur agar ketika mengklik tombol kembali tidak looping pada activity sebelumnya
                startActivity(intent);
            }
        });

    }
    private void login(String  email, String password){
        // CODING LOGIN
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user.isEmailVerified()){
                            Toast.makeText(getApplicationContext(),"Login Successfull", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ActivityLogin.this, MainActivity.class));
                            reload();
                        }else{
                            user.sendEmailVerification();
                            Toast.makeText(getApplicationContext(),"Login Successfull", Toast.LENGTH_SHORT).show();
                        }

                }else if (!task.isSuccessful()){

                    Toast.makeText(getApplicationContext(),"Login Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void reload(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }
//    /* Session = mengecek apakah ada user yang sudah login sebelumnya,
//    jika sudah maka akan langsung menunju ke main activity, sebaliknya
//    jika belum maka login terlebih dahulu
//    * */
//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            reload();
//        }
//    }
}
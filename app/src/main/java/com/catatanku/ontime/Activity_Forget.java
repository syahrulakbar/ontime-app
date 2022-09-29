package com.catatanku.ontime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Activity_Forget extends AppCompatActivity {
    private Button btnForget;
    private TextView tv_login;
    private EditText et_email;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        mAuth = FirebaseAuth.getInstance();

        tv_login=findViewById(R.id.tv_login);
        btnForget=findViewById(R.id.btnForget);
        et_email=findViewById(R.id.et_email);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Forget.this,ActivityLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // mengatur agar ketika mengklik tombol kembali tidak looping pada activity sebelumnya
                startActivity(intent);
            }
        });

        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }
    // Mengirim link untuk mengubah password akun ke email yang dituju
    // referensi https://www.youtube.com/watch?v=w-Uv-ydX_LY
    private void resetPassword() {
        String email = et_email.getText().toString().trim();
        // jika kotak kosong
        if(email.isEmpty()){
            et_email.setError("Email is required");
            et_email.requestFocus();
            return;
        }
        // jika email yang tidak dimasukan tidak ada dalam data
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_email.setError("Please provide valid email!");
            et_email.requestFocus();
            return;
        }
        // jika email ada pada data maka akan mengirim link reset password
        else{
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Toast.makeText(Activity_Forget.this,"Check your email to reset your password!", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(Activity_Forget.this,"Try again! Something wrong happened!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
package com.tgsbesar.myapplication.registerLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tgsbesar.myapplication.MainActivity;
import com.tgsbesar.myapplication.R;
import com.tgsbesar.myapplication.database.AppDatabase;
import com.tgsbesar.myapplication.database.DatabaseClient;
import com.tgsbesar.myapplication.database.Preferences;
import com.tgsbesar.myapplication.database.User;
import com.tgsbesar.myapplication.home.homeFragment;

public class Login extends AppCompatActivity {

    private TextInputEditText txtRMLogin, txtPassLogin;
    private TextInputLayout errorRMLog, errorPassLog;
    private TextView registernext;
    private Button login;
    public static final String NORM = "norm";
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtRMLogin = findViewById(R.id.tv_rmLogin);
        txtPassLogin = findViewById(R.id.tv_passLogin);

        errorRMLog = findViewById(R.id.layout_rmLogin);
        errorPassLog = findViewById(R.id.layout_passLogin);

        login = findViewById(R.id.signIn);

        registernext = findViewById(R.id.toRegister);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateForm())
                {
                    return;
                }else{
                    String email = txtRMLogin.getText().toString();
                    String password = txtPassLogin.getText().toString();

                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                            Preferences preferences = new Preferences(Login.this.getApplicationContext());
                                            preferences.setEmailNorm(email);
                                            Intent i = new Intent(Login.this, MainActivity.class);
                                            startActivity(i);
                                        }else{
                                            Toast.makeText(getApplicationContext(), "Verifikasi email dulu bung", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        registernext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateForm() {
        boolean result = true;

        if(TextUtils.isEmpty(txtRMLogin.getText().toString())){
            result = false;
            txtRMLogin.setError("Nomor rekam medis harus diisi");

        }else{
            txtRMLogin.setError(null);
        }

        if(TextUtils.isEmpty(txtPassLogin.getText().toString())){
            result = false;
            txtPassLogin.setError("Password harus diisi");

        }else{
            txtPassLogin.setError(null);
        }

        return result;
    }
}
package com.example.dell.kcnss;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

public class loginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText UserEmail,UserPassword;
    private TextView NeedNewAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loading;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        loading=new ProgressDialog(this);

        LoginButton=(Button) findViewById(R.id.login_button);
        UserEmail=(EditText) findViewById(R.id.login_email);
        UserPassword=(EditText) findViewById(R.id.login_password);
        NeedNewAccount=(TextView) findViewById(R.id.register_account_link);

        NeedNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendUserToRegisterActivity();

            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AllowingUserToLogin();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            SendUserToMainActivity();
        }
    }

    private void AllowingUserToLogin()
    {
        String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "PLEASE WRITE YOUR EMAIL", Toast.LENGTH_SHORT).show();

        }

       else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "PLEASE WRITE YOUR PASSWORD", Toast.LENGTH_SHORT).show();

        }

        else
        {
            loading.setTitle("LOGIN");
            loading.setMessage("PLEASE WAIT , WHILE WE ARE ALLOWING YOU TO LOGIN INTO YOUR ACCOUNT");
            loading.show();
            loading.setCanceledOnTouchOutside(true);

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                Toast.makeText(loginActivity.this, "YOU ARE LOGGED IN SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                            else
                            {
                                String message=task.getException().getMessage();
                                Toast.makeText(loginActivity.this, "ERROR OCCURED : " + message, Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }

                        }
                    });
        }
    }
//
    private void SendUserToMainActivity()
    {
        Intent mainIntent= new Intent(loginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity()
    {
        Intent registerIntent=new Intent(loginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);

    }
}

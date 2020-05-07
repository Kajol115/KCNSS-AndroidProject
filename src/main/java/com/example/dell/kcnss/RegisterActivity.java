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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText UserEmail,UserPassword,UserConfirmPassword;
    private ProgressDialog loading;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();

        UserEmail=(EditText) findViewById(R.id.register_email);
        UserPassword=(EditText) findViewById(R.id.register_password);
        UserConfirmPassword=(EditText) findViewById(R.id.register_confirm_password);
        CreateAccountButton=(Button) findViewById(R.id.register_create_account);
        loading=new ProgressDialog(this);



        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    CreateNewAccount();
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

    private void SendUserToMainActivity()
    {
        Intent mainIntent= new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void CreateNewAccount()
    {
        String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();
        String confirmPassword=UserConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "PLEASE WRITE YOUR EMAIL", Toast.LENGTH_SHORT).show();
        }

        else  if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "PLEASE WRITE YOUR PASSWORD", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(confirmPassword))
        {
            Toast.makeText(this, "PLEASE CONFIRM YOUR PASSWORD", Toast.LENGTH_SHORT).show();
        }

        else if(!password.equals(confirmPassword))
        {
            Toast.makeText(this, "YOUR PASSWORD DO NOT MATCH WITH YOUR CONFIRM PASSWORD", Toast.LENGTH_SHORT).show();
        }

        else
        {
            loading.setTitle("CREATING NEW ACCOUNT");
            loading.setMessage("PLEASE WAIT , WHILE WE ARE CREATING YOUR NEW ACCOUNT");
            loading.show();
            loading.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                SendUserToSetUpActivity();

                                Toast.makeText(RegisterActivity.this, "YOU ARE AUTHENTICATED SUCCESSFULLY!!!", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }

                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "ERROR OCCURED : " + message, Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }

                        }
                    });
        }
    }

    private void SendUserToSetUpActivity()
    {
        Intent setupIntent=new Intent(RegisterActivity.this,SetUpActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}

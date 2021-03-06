package com.example.dell.kcnss;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity {

    private Button SaveInformationButton;
    private EditText UserName,FullName;
    private CircleImageView profileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;
    String currentUserId;
    private ProgressDialog loadingBar;
    final static int Gallery_Pick=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        loadingBar=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        UserName=(EditText)findViewById(R.id.setup_username);
        FullName=(EditText) findViewById(R.id.setup_fullname);
        SaveInformationButton=(Button) findViewById(R.id.setup_information_button);
        profileImage=(CircleImageView) findViewById(R.id.setup_profile_image);

        SaveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveAccountsetupInformation();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Pick);

            }
        });


        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String image= dataSnapshot.child("profileimage").getValue().toString();

                    Picasso.with(SetUpActivity.this).load(image).placeholder(R.drawable.profile).into(profileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri imageUri= data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we updating your profile image...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();

                final StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SetUpActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();




                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            Log.d("IMAGEURL",downloadUrl);

                            UsersRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Intent selfIntent = new Intent(SetUpActivity.this, SetUpActivity.class);
                                                startActivity(selfIntent);

                                                Toast.makeText(SetUpActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SetUpActivity.this, "Error Occured Retrieving image : " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }


    private void SaveAccountsetupInformation()
    {
        String username= UserName.getText().toString();
        String fullname= FullName.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "PLEASE WRITE YOUR USERNAME", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this, "PLEASE WRITE YOUR FULLNAME", Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle("SAVING INFORMATION");
            loadingBar.setMessage("PLEASE WAIT , WHILE WE ARE CREATING YOUR NEW ACCOUNT");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap=new HashMap();
            userMap.put("username",username);
            userMap.put("fullname",fullname);
            userMap.put("status","Hey there, i am using KCNSS,developed by KAJOL PAREKH");
            userMap.put("gender","none");
            userMap.put("dob","none");
            userMap.put("contact","none");
            userMap.put("bloodgroup","none");
            userMap.put("blooddonor","yes");
            userMap.put("plateledonor","yes");
            userMap.put("bankaccount","yes");
            userMap.put("voterid","yes");
            userMap.put("aadharcard","yes");
            userMap.put("driverslicence","yes");

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(SetUpActivity.this, "YOUR ACCOUNT IS CREATED SUCCESSFULLY", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }

                    else
                    {
                        String message=task.getException().getMessage();
                        Toast.makeText(SetUpActivity.this, "ERROR OCCURED : "+ message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });

        }
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent= new Intent(SetUpActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}

package com.example.sallarkhan.fixuos_admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupAccountActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST = 1;
    private ImageButton imageButton;
   private EditText etName,etRoll,etCell;
    private String name,roll,cell;
    private Button submit;
    private DatabaseReference mDatabaseUsers;
    private StorageReference mStorageUsers;
    private FirebaseAuth mAuth;
    ProgressDialog proDialog;
    private Uri imageUri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);
        setTitle("Setup Account");
        setupReferances();
    }

    private void setupReferances() {
        etName = (EditText) findViewById(R.id.et_setup_name);
        etRoll = (EditText) findViewById(R.id.et_setup_roll);
        etCell = (EditText) findViewById(R.id.et_setup_cell);
        imageButton = (ImageButton) findViewById(R.id.setup_image_button);
        submit = (Button) findViewById(R.id.submit_in_setup);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("user");
        mStorageUsers = FirebaseStorage.getInstance().getReference().child("profile_images");
        proDialog = new ProgressDialog(this);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galeryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galeryIntent.setType("image/*");
                startActivityForResult(galeryIntent, GALLERY_REQUEST);
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              startSetupAccount();
            }
        });

    }

    private void startSetupAccount() {
        name = etName.getText().toString().trim();
        roll = etRoll.getText().toString().trim();
        cell = etCell.getText().toString().trim();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(roll) && !TextUtils.isEmpty(cell) && imageUri != null){
            proDialog.setMessage("Setting up Your Profile");
            proDialog.show();

            final String uid = mAuth.getCurrentUser().getUid();
            StorageReference filepath = mStorageUsers.child(uid);
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    mDatabaseUsers.child(uid).child("name").setValue(name);
                    mDatabaseUsers.child(uid).child("roll").setValue(roll);
                    mDatabaseUsers.child(uid).child("cell").setValue(cell);
                    mDatabaseUsers.child(uid).child("image").setValue(downloadUri);
                    proDialog.dismiss();

                    Intent mainIntent = new Intent(SetupAccountActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            });



        }else{
            Snackbar.make(findViewById(R.id.layout_setup_account), "Please fill all Fields and Upload Your Image",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST){
            Uri imageURI = data.getData();
            CropImage.activity(imageURI)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                imageButton.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }
}

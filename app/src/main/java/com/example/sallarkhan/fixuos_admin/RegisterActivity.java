package com.example.sallarkhan.fixuos_admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RegisterActivity extends AppCompatActivity {
   private EditText etEmail,etPass;
    private String email,pass;
    private Button btnSignUp,btnSignIn;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
  //  private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private ProgressDialog proDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Sign Up");
        setupReferences();
    }

    private void setupReferences() {
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users");
        mStorage = FirebaseStorage.getInstance().getReference().child("users");

        etEmail = (EditText) findViewById(R.id.et_signup_email);
        etPass = (EditText) findViewById(R.id.et_signup_pass);
        proDialog = new ProgressDialog(this);
        btnSignUp = (Button) findViewById(R.id.signup_in_signup);
        btnSignIn = (Button) findViewById(R.id.signin_in_signup);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });


    }

    private void startRegister() {
        email = etEmail.getText().toString().trim();
        pass = etPass.getText().toString();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
            proDialog.setMessage("Signing Up..");
            proDialog.show();
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        proDialog.dismiss();


                        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                }
            });
        }

    }
    private void checkUserExist() {
        final String uid = mAuth.getCurrentUser().getUid();
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(uid)){
                    Log.e("LoginActivity", "onDataChange: if" );
                    Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                }else {
                    Log.e("LoginActivity", "onDataChange: else" );

                    Intent setupIntent = new Intent(RegisterActivity.this,SetupAccountActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);
                    // Toast.makeText(getApplicationContext(),"Please setup yr Ac",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}

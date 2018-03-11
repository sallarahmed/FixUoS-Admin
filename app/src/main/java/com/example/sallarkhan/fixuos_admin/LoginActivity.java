package com.example.sallarkhan.fixuos_admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";
    EditText etEmail,etPass;
    Button btnSignin,btnSignup;
    ProgressDialog proDialog;
    FirebaseAuth mAuth;
   // DatabaseReference mDatabase;
    DatabaseReference mDatabaseUser;
    StorageReference mStorage;
    private SignInButton mGooglebtn;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupRefrences();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                    .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            proDialog.setMessage("Signing In Via Google");
            proDialog.show();
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                proDialog.dismiss();
                Snackbar.make(findViewById(R.id.login_activity_layout), "Error Login", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
             //   Toast.makeText(LoginActivity.this,"Error Login",Toast.LENGTH_LONG).show();
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.login_activity_layout), "Authentication failed",
                                    Snackbar.LENGTH_LONG).setAction("Action", null).show();

                            //Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }
                        proDialog.show();
                        // ...
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void setupRefrences() {
        setContentView(R.layout.activity_login);
        setTitle("Log In");
        etEmail = (EditText) findViewById(R.id.et_email_login);
        etPass = (EditText) findViewById(R.id.et_pass_login);
        btnSignin = (Button) findViewById(R.id.signin_of_login);
        btnSignup = (Button) findViewById(R.id.signup_of_login);
        mGooglebtn = (SignInButton) findViewById(R.id.google_signin_btn);
        proDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("user");
        mDatabaseUser.keepSynced(true);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                String pass = etPass.getText().toString();
                signInViaEmail(email,pass);
            }
        });


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });


        mGooglebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }


    private void signInViaEmail(String email, String pass) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
            proDialog.setMessage("Signing u In...");
            proDialog.show();
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Log.e("LoginActivity", "onDataChange: if" );
                        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);

                    }else {
                        Snackbar.make(findViewById(R.id.login_activity_layout), "Error Login", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                   //     Toast.makeText(LoginActivity.this,"Error Login",Toast.LENGTH_LONG).show();
                    }
                    proDialog.dismiss();
                }
            });

        }else{
            Snackbar.make(findViewById(R.id.login_activity_layout), "Please enter Username and Password", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

/*    private void checkUserExist() {
        final String uid = mAuth.getCurrentUser().getUid();
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(uid)){
                    Log.e("LoginActivity", "onDataChange: if" );
                    Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                }else {
                    Log.e("LoginActivity", "onDataChange: else" );

                    Intent setupIntent = new Intent(LoginActivity.this,SetupAccountActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);
                   // Toast.makeText(getApplicationContext(),"Please setup yr Ac",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }*/


}

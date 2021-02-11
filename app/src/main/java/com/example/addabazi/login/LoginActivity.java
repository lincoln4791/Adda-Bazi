package com.example.addabazi.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.addabazi.MainActivity;
import com.example.addabazi.MessagesActivity;
import com.example.addabazi.R;
import com.example.addabazi.SignUpActivity;
import com.example.addabazi.common.Util;
import com.example.addabazi.password.ResetPasswordActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etEmail,etPassword;
    private String email, password;
    private Button btnLogin;
    private TextView signUp;
    private View customProgressbar;
    private ImageView iv;
    private SignInButton signInFacebookBtn;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private  static int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail_loginActivity_ID);
        etPassword = findViewById(R.id.etPassword_loginActivity_ID);
        btnLogin = findViewById(R.id.btnLoginID);
        signUp = findViewById(R.id.tvSignUp_loginActivity_ID);
        customProgressbar = findViewById(R.id.customProgressBarID);
        iv= findViewById(R.id.default_profile_picture_id);


        // SIgn in with Goolge implementation
        mAuth=FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInFacebookBtn = findViewById(R.id.sign_in_button_google);
        signInFacebookBtn.setSize(SignInButton.SIZE_STANDARD);
        signInFacebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button_google:
                        signInWithGoogle();
                        break;
                    // ...
                }

            }
        });




        //Action Listeners

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginTask();
            }
        });



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSignUpActivity();
            }
        });

    }




    private void loginTask() {
        email= Objects.requireNonNull(etEmail.getText()).toString().trim();
        password= Objects.requireNonNull(etPassword.getText()).toString().trim();

        if (email.equals("")){
            etEmail.setError(getString(R.string.hint_enter_email));
        }
        else if (password.equals("")){
            etPassword.setError(getString(R.string.hint_enter_password));
        }
        else{

            FirebaseAuth firebaseauth = FirebaseAuth.getInstance();
            if(Util.connectionAvailable(this)) {
                customProgressbar.setVisibility(View.VISIBLE);
                firebaseauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        customProgressbar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    Util.updateToken(LoginActivity.this,instanceIdResult.getToken());
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            Toast.makeText(getApplicationContext(), "Login Failed " + task.getException(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }


            else{
                Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
                startActivity(intent);
            }
        }
    }




    private void gotoSignUpActivity() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
/*        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair(iv,"image");
        pairs[1] = new Pair(etEmail,"et1");
        pairs[2] = new Pair(etPassword,"et2");
        pairs[3] = new Pair(btnLogin,"btn");
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,pairs);
        startActivity(intent,activityOptions.toBundle());*/
        startActivity(intent);
    }




    public void forgetPasswordClicked(View view){
        Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    Util.updateToken(LoginActivity.this,instanceIdResult.getToken());
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
            });


        }
    }








    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }






    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseGoogleAuth(account);

            // Signed in successfully, show authenticated UI.
            /*Intent intent = new Intent(MainActivity.this,MainActivity2.class);
            startActivity(intent);*/
            Log.d("tag", "Success");
            //updateUI(account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("tag", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }





    private void firebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            Util.updateToken(LoginActivity.this,instanceIdResult.getToken());
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            Toast.makeText(LoginActivity.this, R.string.loginSuccessful, Toast.LENGTH_SHORT).show();
                            finish();

                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
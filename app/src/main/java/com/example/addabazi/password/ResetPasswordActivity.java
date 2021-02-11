package com.example.addabazi.password;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.addabazi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private FirebaseUser firebaseUser;
    private  FirebaseAuth firebaseAuth;
    private LinearLayout llResetPasswordStatusInner, llResetPasswordStatusOuter;
    private TextView resetPasswordStatusText;
    private Button retryBtn;
    private View customProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etEmail = findViewById(R.id.et_Email_ResetPassword_ID);
        llResetPasswordStatusOuter =findViewById(R.id.ll_resetPasswordStatusTextInner_ID);
        llResetPasswordStatusInner =findViewById(R.id.ll_resetPasswordStatusTextOuter_ID);
        resetPasswordStatusText = findViewById(R.id.tv_resetPasswordStstusText_ID);
        customProgressbar=findViewById(R.id.customProgressBarID);
        retryBtn = findViewById(R.id.btn_RetrySendingResetEmail_ID);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();


    }

    public void btnResetPasswordClicked(View view){
        String email = etEmail.getText().toString().trim();
        if(email.equals("")){
            etEmail.setError(getString(R.string.hint_enter_email));
        }

        else{
            customProgressbar.setVisibility(View.VISIBLE);
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    customProgressbar.setVisibility(View.GONE);

                    llResetPasswordStatusOuter.setVisibility(View.GONE);
                    llResetPasswordStatusInner.setVisibility(View.VISIBLE);
                     if(task.isSuccessful()){
                         resetPasswordStatusText.setText(getString(R.string.resetPasswordInstructionHasBeenSent,email));

                         new CountDownTimer(60000, 1000) {
                             @Override
                             public void onTick(long millisUntilFinished) {
                                 retryBtn.setText(getString(R.string.retryTimer,String.valueOf(millisUntilFinished/1000)));
                                 retryBtn.setOnClickListener(null);
                             }

                             @Override
                             public void onFinish() {
                                 retryBtn.setText(getString(R.string.retry));
                                 retryBtn.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                        llResetPasswordStatusOuter.setVisibility(View.GONE);
                                        llResetPasswordStatusInner.setVisibility(View.VISIBLE);

                                     }
                                 });

                             }
                         }.start();

                     }
                     else{

                         retryBtn.setText(getString(R.string.retry));
                         retryBtn.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {

                                 llResetPasswordStatusOuter.setVisibility(View.GONE);
                                 llResetPasswordStatusInner.setVisibility(View.VISIBLE);
                                 resetPasswordStatusText.setText(getString(R.string.resetPasswordEmailSendingFailed)+task.getException());


                             }
                         });

                     }
                }
            });
        }

    }




    public void btnCloseClicked(View view){
        finish();
    }

}
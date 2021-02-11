package com.example.addabazi.password;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.addabazi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.addabazi.R.string.passwordChangedFailed;

public class ChangePasswordActivity extends AppCompatActivity {
    private TextInputEditText etPassword,etConfirmPassword;
    private FirebaseUser firebaseUser;
    private String password,confirmPassword;
    private View customProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etPassword = findViewById(R.id.etPassword_ID);
        etConfirmPassword = findViewById(R.id.etConfirmPassword_ID);
        customProgressbar=findViewById(R.id.customProgressBarID);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



    }





    public void btnChangePasswordClicked(View view){

        password = etPassword.getText().toString().trim();
        confirmPassword = etConfirmPassword.getText().toString().trim();

        if(password.equals("")){
            etPassword.setError(getString(R.string.hint_enter_password));
        }

        else if(confirmPassword.equals("")){
            etConfirmPassword.setError(getString(R.string.hint_confirm_password));
        }

        else if(!password.equals(confirmPassword)){
            etConfirmPassword.setError(getString(R.string.password_mismatch));
        }

        else {
            if(firebaseUser != null){
                customProgressbar.setVisibility(View.VISIBLE);
                firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        customProgressbar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            Toast.makeText(ChangePasswordActivity.this, R.string.passwordChangedSuccessfully, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(ChangePasswordActivity.this, passwordChangedFailed+" "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

    }
}
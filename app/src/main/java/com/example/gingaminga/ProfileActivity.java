package com.example.gingaminga;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Button backButton, editPasswordButton, updatePasswordButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user.getEmail();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        backButton = (Button) findViewById(R.id.btn_back);
        editPasswordButton = (Button) findViewById(R.id.btn_edit_profile);
        updatePasswordButton = (Button) findViewById(R.id.btn_update_profile);
        logoutButton = (Button) findViewById(R.id.btn_logout);
        inputEmail = (EditText) findViewById(R.id.edt_email);
        inputPassword = (EditText) findViewById(R.id.edt_password);

        inputEmail.setText(userEmail);
        inputPassword.setVisibility(View.GONE);
        updatePasswordButton.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputPassword.setVisibility(View.VISIBLE);
                editPasswordButton.setVisibility(View.GONE);
                updatePasswordButton.setVisibility(View.VISIBLE);
            }
        });

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String newPassword = inputPassword.getText().toString().trim();
//                Toast.makeText(ProfileActivity.this, ""+newPassword, Toast.LENGTH_SHORT).show();
                if (user != null && !newPassword.equals("")) {
                    if (newPassword.length() < 6) {
                        inputPassword.setError(getString(R.string.minimum_password));
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "Password is updated", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(ProfileActivity.this, "Please login again with new password", Toast.LENGTH_SHORT).show();
                                    signOut();
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                } else if (newPassword.equals("")) {
                    inputPassword.setError("Enter new password");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    public void signOut() {
        mAuth.signOut();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        progressBar.setVisibility(View.GONE);
//    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }
}
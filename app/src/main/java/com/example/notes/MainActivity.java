package com.example.notes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    private TextView createNewAccount;
    public CheckBox staySignedIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        signIn = (Button) findViewById(R.id.signIn);
        createNewAccount = (TextView) findViewById(R.id.createNewAccount);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(MainActivity.this, NotesActivity.class));
        }

        signIn.setOnClickListener(this);
        createNewAccount.setOnClickListener(this);
    }

    public void login() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        progressDialog.setMessage("Signing in. Please wait...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            //we will start notes activity here
                            finish();
                            startActivity(new Intent(MainActivity.this, NotesActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Could not sign in. Please check your email and password.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v == signIn) {
            //User will login
            login();
        }
        if (v == createNewAccount) {
            //User will be moved to registration activity
            finish();
            startActivity(new Intent(this, RegistrationActivity.class));
        }
    }
}
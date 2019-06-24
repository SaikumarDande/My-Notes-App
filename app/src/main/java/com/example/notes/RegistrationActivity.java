package com.example.notes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button register;
    private TextView haveAnAccount;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText)findViewById(R.id.editTextConfirmPassword);
        register = (Button)findViewById(R.id.register);
        haveAnAccount = (TextView)findViewById(R.id.haveAnAccount);

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(RegistrationActivity.this, NotesActivity.class));
        }

        register.setOnClickListener(this);
        haveAnAccount.setOnClickListener(this);
    }

    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmpassword = editTextConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Enter email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(confirmpassword)){
            Toast.makeText(this, "Enter confirm password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!confirmpassword.equals(password)){
            Toast.makeText(this, "Confirm password is not correct", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Registering User. Please wait...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.cancel();
                        if (task.isSuccessful()){
                            Toast.makeText(RegistrationActivity.this, "Registerd Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistrationActivity.this, NotesActivity.class));
                        }
                        else{
                            Toast.makeText(RegistrationActivity.this, "Could not register. Plaese try again",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if (v == register){
            registerUser();
        }
        if (v == haveAnAccount){
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}

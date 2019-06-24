package com.example.notes;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity {

    private EditText inputNote;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference notesdatabase;
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputNote = (EditText)findViewById(R.id.input_note);
        firebaseAuth = FirebaseAuth.getInstance();
        notesdatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(firebaseAuth.getCurrentUser().getUid());

        try{
            String noteId = getIntent().getStringExtra("noteId");
            notesdatabase.child(noteId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean aBoolean = Datachangecall.isA();
                    if (aBoolean) {
                        String edittext = dataSnapshot.child("input_notes").getValue().toString().trim();
                        inputNote.setText(edittext);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*private void retrieveNotes() {
        notesdatabase.child(noteId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(EditNoteActivity.this, "Inside onDataChange", Toast.LENGTH_SHORT).show();
                Log.d("Tag", "Inside datachange");
                text = dataSnapshot.child("input_notes").getValue().toString().trim();
                inputNote.setText(text);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_note){
            String notes = inputNote.getText().toString().trim();
            if(!TextUtils.isEmpty(notes))
                saveNote(notes);
            else
                Toast.makeText(EditNoteActivity.this, "Write something to save", Toast.LENGTH_SHORT).show();
        }

        if (id == android.R.id.home){
            //if (text == inputNote.getText().toString()){
              //  saveNote(text);
            //}
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNote(String inputNote) {
        String noteId = getIntent().getStringExtra("noteId");
        if (noteId == null) {
            if (firebaseAuth.getCurrentUser() != null) {
                final DatabaseReference newNoteRef = notesdatabase.push();

                final Map noteMap = new HashMap();
                noteMap.put("input_notes", inputNote);
                noteMap.put("notes_date", new Date().getTime());

                Thread mainthread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditNoteActivity.this, "Notes added to database", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else
                                    Toast.makeText(EditNoteActivity.this, "Notes are not added to database", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                mainthread.start();
            } else {
                Toast.makeText(EditNoteActivity.this, "User not signed in...", Toast.LENGTH_SHORT).show();
            }
        }else{
            Map updateNoteMap = new HashMap();
            updateNoteMap.put("input_notes", inputNote);
            updateNoteMap.put("notes_date", new Date().getTime());

            notesdatabase.child(noteId).updateChildren(updateNoteMap);
            finish();
        }
    }
}

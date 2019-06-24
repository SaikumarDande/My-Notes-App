package com.example.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.animation.Positioning;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notes.NotesDate.NoteDate;
import com.example.notes.model.Notes;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity{

    private RecyclerView recyclerView;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference notesdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.notes_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseAuth = FirebaseAuth.getInstance();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddNewNote();
            }
        });

        if (firebaseAuth.getCurrentUser() != null){
            notesdatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(firebaseAuth.getCurrentUser().getUid());
        }
    }

    private void onAddNewNote(){
        startActivity(new Intent(NotesActivity.this, EditNoteActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        FirebaseRecyclerOptions<Notes> details = new FirebaseRecyclerOptions.Builder<Notes>().setQuery(notesdatabase, Notes.class).build();
        final FirebaseRecyclerAdapter<Notes, NotesViewHolder> adapter = new FirebaseRecyclerAdapter<Notes, NotesViewHolder>(details) {

            @Override
            protected void onBindViewHolder(@NonNull final NotesViewHolder holder, int position, @NonNull final Notes model) {
                holder.usernotes.setText(model.getInput_notes());
                holder.notesdate.setText(NoteDate.dateFromLong(model.getNotes_date()));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String noteId = getRef(holder.getAdapterPosition()).getKey();
                        Datachangecall.setA(true);
                        Intent editActivity = new Intent(NotesActivity.this, EditNoteActivity.class);
                        editActivity.putExtra("noteId", noteId);
                        startActivity(editActivity);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String noteId = getRef(holder.getAdapterPosition()).getKey();
                        if (noteId != null){
                            Intent editActivity = new Intent(NotesActivity.this, EditNoteActivity.class);
                            Datachangecall.setA(false);
                            deleteNote(noteId);
                        }
                        return false;
                    }
                });
            }

            @NonNull
            @Override
            public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_layout, viewGroup, false);
                NotesViewHolder viewHolder = new NotesViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteNote(final String noteId) {
        new AlertDialog.Builder(this).setTitle("Notes")
                .setNegativeButton("dismiss dialog", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notesdatabase.child(noteId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(NotesActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                            loadNotes();
                        }else{
                        }
                    }
                });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder{

        TextView usernotes, notesdate;
        CheckBox checkBox;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);

            usernotes = itemView.findViewById(R.id.note_text);
            notesdate = itemView.findViewById(R.id.note_date);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(NotesActivity.this, MainActivity.class));
    }
}


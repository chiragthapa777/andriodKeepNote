package com.example.keepnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    TextView pageTitle;
    ImageButton saveNoteBtn;
    Button deleteBtn;
    String title, content, docId;
    boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText = findViewById(R.id.title_edit_text);
        contentEditText = findViewById(R.id.content_edit_text);
        saveNoteBtn = findViewById(R.id.add_note_btn);
        pageTitle = findViewById(R.id.page_title);
        deleteBtn = findViewById(R.id.delete_btn);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
            deleteBtn.setVisibility(View.VISIBLE);
        }
        if (isEditMode) {
            titleEditText.setText(title);
            contentEditText.setText(content);
            pageTitle.setText("Edit Your Note");

        }

        deleteBtn.setOnClickListener(v -> deleteNoteFromFireBase());

        saveNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
    }

    private void deleteNoteFromFireBase() {
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNote().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(NoteDetailsActivity.this, "Note is deleted successfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this, task.getException().getLocalizedMessage());
                }
            }
        });

    }

    void saveNote() {
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        if (noteTitle == null || noteTitle.isEmpty()) {
            titleEditText.setError("Title is required");
            return;
        }
        if (isEditMode) {
            if (content.equals(getIntent().getStringExtra("content")) && title.equals(getIntent().getStringExtra("title"))) {
                finish();
            }
        }
        Note note = new Note(noteTitle, noteContent, Timestamp.now());
        saveNoteToFireBase(note);
    }

    void saveNoteToFireBase(Note note) {
        DocumentReference documentReference;
        if (isEditMode) {
            documentReference = Utility.getCollectionReferenceForNote().document(docId);
        } else {
            documentReference = Utility.getCollectionReferenceForNote().document();
        }
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Utility.showToast(NoteDetailsActivity.this, isEditMode ? "Note is updated successfully" : "Note is added successfully");
                    finish();
                } else {
                    Utility.showToast(NoteDetailsActivity.this, task.getException().getLocalizedMessage());
                }
            }
        });
    }
}
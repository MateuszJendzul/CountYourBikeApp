package com.matis8571.countyourbike.Notepad.Models;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.matis8571.countyourbike.App.MainActivity;
import com.matis8571.countyourbike.R;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("Convert2Lambda")
public class NotesTakerActivity extends AppCompatActivity {
    private static final String TAG = "NotesTakerActivity";
    EditText noteTitleEdit, noteTextEdit;
    Notes notes;
    Button notesTakerBackButton, notesTakerAddButton, notesTakerHomeButton;
    boolean isOldNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_taker_activity_layout);

        noteTitleEdit = findViewById(R.id.note_taker_bote_title_ID);
        noteTextEdit = findViewById(R.id.note_taker_note_text_ID);
        notesTakerBackButton = findViewById(R.id.notes_taker_back_button_ID);
        notesTakerAddButton = findViewById(R.id.notes_taker_add_button_ID);
        notesTakerHomeButton = findViewById(R.id.notes_taker_home_button_ID);

        notes = new Notes();
        try {
            notes = (Notes) getIntent().getSerializableExtra("oldNote");
            noteTitleEdit.setText(notes.getTitle());
            noteTextEdit.setText(notes.getNotes());
            isOldNote = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        notesTakerHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notesTakerHomeButtonIntent = new Intent(NotesTakerActivity.this,
                        MainActivity.class);
                startActivity(notesTakerHomeButtonIntent);
            }
        });

        notesTakerAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = noteTitleEdit.getText().toString();
                String description = noteTextEdit.getText().toString();

                if (noteTitleEdit.getText().toString().isEmpty()) {
                    Toast.makeText(NotesTakerActivity.this, "Empty title",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Week day, date, month, year, hour:minute, AM or PM
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter
                        = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                Date date = new Date();

                if (!isOldNote) {
                    notes = new Notes();
                }
                notes.setTitle(title);
                notes.setNotes(description);
                notes.setDateAndTime(formatter.format(date));

                Intent notesTakerAddButtonIntent = new Intent();
                notesTakerAddButtonIntent.putExtra("note", notes);
                setResult(Activity.RESULT_OK, notesTakerAddButtonIntent);
                finish();
            }
        });

        notesTakerBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
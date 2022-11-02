package com.matis8571.countyourbike.Notepad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.matis8571.countyourbike.App.Bike1;
import com.matis8571.countyourbike.App.MainActivity;
import com.matis8571.countyourbike.Notepad.Adapters.NotesListAdapter;
import com.matis8571.countyourbike.Notepad.Database.NotesClickListener;
import com.matis8571.countyourbike.Notepad.Database.RoomDB;
import com.matis8571.countyourbike.Notepad.Models.Notes;
import com.matis8571.countyourbike.Notepad.Models.NotesTakerActivity;
import com.matis8571.countyourbike.R;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Convert2Lambda")
public class MainActivityNotepad extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    RecyclerView recyclerHome;
    NotesListAdapter notesListAdapter;
    List<Notes> notes = new ArrayList<>();
    RoomDB database;
    SearchView searchViewHome;
    Notes selectedNote;
    Button notesBackButton, noteAddButton, notepadMainToMain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_noptepad_layout);

        recyclerHome = findViewById(R.id.recyclerHomeID);
        searchViewHome = findViewById(R.id.searchViewHomeID);
        noteAddButton = findViewById(R.id.noteAddButtonID);
        notesBackButton = findViewById(R.id.notesBackButtonID);
        notepadMainToMain = findViewById(R.id.notepadMainToMainID);

        database = RoomDB.getInstance(this);
        notes = database.mainNotepadDAO().getAll();

        updateRecycler(notes);

        noteAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fabAddIntent = new Intent(MainActivityNotepad.this, NotesTakerActivity.class);
                //noinspection deprecation
                startActivityForResult(fabAddIntent, 101);
            }
        });

        searchViewHome.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        notesBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        notepadMainToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notepadMainToMainIntent = new Intent(MainActivityNotepad.this, MainActivity.class);
                startActivity(notepadMainToMainIntent);
            }
        });
    }

    private void filter(String newText) {
        List<Notes> filteredList = new ArrayList<>();
        for (Notes singleNote : notes) {
            if (singleNote.getTitle().toLowerCase().contains(newText.toLowerCase())
                    || singleNote.getNotes().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(singleNote);
            }
        }
        notesListAdapter.filterList(filteredList);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Notes newNotes = (Notes) data.getSerializableExtra("note");
                database.mainNotepadDAO().insert(newNotes);
                notes.clear();
                notes.addAll(database.mainNotepadDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Notes newNotes = (Notes) data.getSerializableExtra("note");
                database.mainNotepadDAO().update(newNotes.getID(), newNotes.getTitle(), newNotes.getNotes());
                notes.clear();
                notes.addAll(database.mainNotepadDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateRecycler(List<Notes> notes) {
        recyclerHome.setHasFixedSize(true);
        recyclerHome.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapter = new NotesListAdapter(MainActivityNotepad.this, notes, notesClickListener);
        recyclerHome.setAdapter(notesListAdapter);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivityNotepad.this, NotesTakerActivity.class);
            intent.putExtra("oldNote", notes);
            //noinspection deprecation
            startActivityForResult(intent, 102);
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNote = notes;
            showPopup(cardView);
        }
    };

    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_notepad_menu);
        popupMenu.show();
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pin:
                if (selectedNote.isPinned()) {
                    database.mainNotepadDAO().pin(selectedNote.getID(), false);
                    Toast.makeText(this, "Unpinned", Toast.LENGTH_SHORT).show();
                } else {
                    database.mainNotepadDAO().pin(selectedNote.getID(), true);
                    Toast.makeText(this, "Pinned", Toast.LENGTH_SHORT).show();
                }
                notes.clear();
                notes.addAll(database.mainNotepadDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
                return true;

            case (R.id.delete):
                database.mainNotepadDAO().delete(selectedNote);
                notes.remove(selectedNote);
                notesListAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return false;
        }
    }
}
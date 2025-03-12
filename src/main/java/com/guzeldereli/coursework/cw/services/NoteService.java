package com.guzeldereli.coursework.cw.services;

import com.guzeldereli.coursework.cw.models.Note;
import com.guzeldereli.coursework.cw.models.NoteFragment;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;

public interface NoteService
{
    public ArrayList<Note> GetNotes();

    public Note GetNoteByUUID(String UUID);
    public ArrayList<Triple<Note, Boolean, Integer>> SearchNotes(String query, int limit);

    public boolean NoteExists(String UUID);

    public Note CreateNote();
    public boolean UpdateNote(Note note);
    public boolean DeleteNote(String NoteUUID);
}

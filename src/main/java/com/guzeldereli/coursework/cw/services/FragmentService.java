package com.guzeldereli.coursework.cw.services;

import com.guzeldereli.coursework.cw.models.Note;
import com.guzeldereli.coursework.cw.models.NoteFragment;

import java.util.ArrayList;

public interface FragmentService
{
    public ArrayList<NoteFragment> GetFragments(String NoteUUID);
    public NoteFragment GetFragmentByUUID(String NoteUUID, String FragmentUUID);

    public boolean NoteFragmentExists(String NoteUUID, String FragmentUUID);

    public NoteFragment CreateNoteFragment(String NoteUUID, String type);
    public boolean UpdateNoteFragment(String NoteUUID, NoteFragment noteFragment);
    public boolean DeleteNoteFragment(String NoteUUID, String FragmentUUID);
}

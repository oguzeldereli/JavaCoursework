package com.guzeldereli.coursework.cw.services;

import com.guzeldereli.coursework.cw.models.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Named("fragmentService")
@ApplicationScoped
public class FragmentServiceImplementation implements FragmentService
{
    @Inject
    private NoteService noteService;
    @Inject
    private FileService fileService;

    private String GetNotesPath()
    {
        String userHome = System.getProperty("user.home");
        Path userHomePath = Paths.get(userHome);
        userHomePath = userHomePath.resolve("notes-app-data");

        try
        {
            if(!Files.exists(userHomePath))
            {
                Files.createDirectory(userHomePath);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        return userHomePath.toString();
    }

    private String GetNotePath(Note note)
    {
        Path filePath = Paths.get(GetNotesPath());
        filePath = filePath.resolve(note.UUID + ".xml");
        return filePath.toString();
    }

    private String GetNotePath(String UUID)
    {
        Path filePath = Paths.get(GetNotesPath());
        filePath = filePath.resolve(UUID + ".xml");
        return filePath.toString();
    }

    private void SaveNote(Note note)
    {
        String notePath = GetNotesPath();
        Path filePath = Paths.get(notePath);
        String fileName = note.UUID + ".xml";
        filePath = filePath.resolve(fileName);
        note.ToXMLFile(filePath.toString());
    }

    @Override
    public ArrayList<NoteFragment> GetFragments(String NoteUUID)
    {
        Note note = noteService.GetNoteByUUID(NoteUUID);
        if(note == null)
        {
            return null;
        }

        return note.GetFragments();
    }

    @Override
    public NoteFragment GetFragmentByUUID(String NoteUUID, String FragmentUUID)
    {
        Note note = noteService.GetNoteByUUID(NoteUUID);
        if(note == null)
        {
            return null;
        }

        return note.GetFragment(FragmentUUID);
    }

    @Override
    public boolean NoteFragmentExists(String NoteUUID, String FragmentUUID)
    {
        Note note = noteService.GetNoteByUUID(NoteUUID);
        if(note == null)
        {
            return false;
        }

        NoteFragment fragment = note.GetFragment(FragmentUUID);
        return fragment != null;
    }

    @Override
    public NoteFragment CreateNoteFragment(String NoteUUID, String type)
    {
        Note note = noteService.GetNoteByUUID(NoteUUID);
        if(note == null)
        {
            return null;
        }

        NoteFragment noteFragment;
        if(type.equals("text"))
        {
            noteFragment = new NoteText();
        }
        else if(type.equals("image"))
        {
            noteFragment = new NoteImage();
        }
        else if (type.equals("url"))
        {
            noteFragment = new NoteUrl();
        }
        else
        {
            return null;
        }

        note.AddFragment(noteFragment);
        SaveNote(note);
        return noteFragment;
    }

    @Override
    public boolean UpdateNoteFragment(String NoteUUID, NoteFragment noteFragment)
    {
        Note note = noteService.GetNoteByUUID(NoteUUID);
        if(note == null)
        {
            return false;
        }

        note.UpdateNoteFragment(noteFragment);
        SaveNote(note);
        return true;
    }

    @Override
    public boolean DeleteNoteFragment(String NoteUUID, String FragmentUUID)
    {
        Note note = noteService.GetNoteByUUID(NoteUUID);
        if(note == null)
        {
            return false;
        }

        // If the file is an image, delete the image.
        NoteFragment fragment = note.GetFragment(FragmentUUID);
        if(fragment.getType().equals("image"))
        {
            NoteImage noteImage = (NoteImage) fragment;
            if(noteImage.IsImageRemote == false)
            {
                fileService.DeleteFile(noteImage.ImagePath);
            }
        }

        note.RemoveFragment(FragmentUUID);
        SaveNote(note);
        return true;
    }
}

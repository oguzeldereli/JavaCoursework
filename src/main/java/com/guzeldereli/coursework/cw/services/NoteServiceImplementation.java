package com.guzeldereli.coursework.cw.services;

import com.guzeldereli.coursework.cw.models.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named("noteService")
@ApplicationScoped
public class NoteServiceImplementation implements NoteService
{
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
    public ArrayList<Note> GetNotes()
    {
        ArrayList<Note> notes;
        Path notePath = Paths.get(GetNotesPath());
        try (Stream<Path> stream = Files.list(notePath))
        {
            notes = stream
                .filter(path -> path.toString().endsWith(".xml"))
                .map(path -> Note.FromXMLFile(path.toString()))
                .collect(Collectors.toCollection(ArrayList::new));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        return notes;
    }

    @Override
    public Note GetNoteByUUID(String UUID)
    {
        String notePath = GetNotesPath();
        Path filePath = Paths.get(notePath);
        filePath = filePath.resolve(UUID + ".xml");

        if(!Files.exists(filePath))
        {
            return null;
        }

        return Note.FromXMLFile(filePath.toString());
    }

    @Override
    public ArrayList<Triple<Note, Boolean, Integer>> SearchNotes(String query, int limit)
    {
        ArrayList<Triple<Note, Boolean, Integer>> searchResults = new ArrayList<>();
        ArrayList<Note> notes = GetNotes();
        int counter = 0;
        for (Note note : notes)
        {
            Pair<Boolean, Integer> singleSearchResult = note.SearchText(query);
            searchResults.add(Triple.of(note, singleSearchResult.getLeft(), singleSearchResult.getRight()));
            if(++counter == limit)
            {
                break;
            }
        }
        return searchResults;
    }

    @Override
    public boolean NoteExists(String UUID)
    {
        return GetNoteByUUID(UUID) != null;
    }

    @Override
    public Note CreateNote()
    {
        Note note = new Note();
        SaveNote(note);
        return note;
    }

    @Override
    public boolean UpdateNote(Note note)
    {
        if(!NoteExists(note.UUID))
        {
            return false;
        }

        SaveNote(note);
        return true;
    }

    @Override
    public boolean DeleteNote(String NoteUUID)
    {
        Note note = GetNoteByUUID(NoteUUID);
        if(note == null)
        {
            return false;
        }

        // Delete all associated files
        ArrayList<NoteFragment> fragments = note.GetFragments();
        for(NoteFragment fragment : fragments)
        {
            if(fragment.getType().equals("image"))
            {
                NoteImage img = (NoteImage) fragment;

                if(img.IsImageRemote == false)
                {
                    fileService.DeleteFile(img.ImagePath);
                }
            }
        }

        Path filePath = Paths.get(GetNotePath(NoteUUID));
        try
        {
            Files.delete(filePath);
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}

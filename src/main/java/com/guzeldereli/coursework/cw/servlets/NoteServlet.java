package com.guzeldereli.coursework.cw.servlets;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guzeldereli.coursework.cw.models.Note;
import com.guzeldereli.coursework.cw.services.NoteService;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.apache.commons.lang3.tuple.Triple;

@WebServlet(name = "noteServlet", value = "/notes")
public class NoteServlet extends HttpServlet
{
    @Inject
    private NoteService noteService;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");
        ArrayList<Note> notes;
        String query = request.getParameter("query");
        String[] categories = request.getParameterValues("category");
        if (query != null && !query.isEmpty())
        {
            // no limit needed, only for future support
            ArrayList<Triple<Note, Boolean, Integer>> results = noteService.SearchNotes(query, -1);
            notes = results.stream()
                    .filter(Triple::getMiddle)
                    .sorted(Comparator.comparing(Triple::getRight, Comparator.reverseOrder()))
                    .map(Triple::getLeft)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        else
        {
            notes = noteService.GetNotes();
        }

        if (categories != null && categories.length > 0) {
            Set<String> categorySet = new HashSet<>(Arrays.asList(categories));

            notes = notes.stream()
                    .filter(note -> !note.Categories.isEmpty())
                    .filter(note -> note.Categories.stream().anyMatch(categorySet::contains))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String notesJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(notes);
        response.getWriter().write(notesJson);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");
        Note note = noteService.CreateNote();
        ObjectMapper objectMapper = new ObjectMapper();
        String noteJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(note);
        response.getWriter().write(noteJson);
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");
    }
}
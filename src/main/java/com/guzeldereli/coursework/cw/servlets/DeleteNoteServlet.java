package com.guzeldereli.coursework.cw.servlets;

import java.io.*;

import com.guzeldereli.coursework.cw.common.RequestObjectGetter;
import com.guzeldereli.coursework.cw.models.Note;
import com.guzeldereli.coursework.cw.services.NoteService;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

/*
The creation of this file requires context. Apparently on DELETE requests, Tomcat automatically
removes the body data of the request unless specified in Tomcat settings. As such, I will have to
do all PUT and DELETE actions as a separate servlet. This also applies to all update and delete
actions for all other servlets.
*/

@WebServlet(name = "deleteNoteServlet", value = "/notes/delete")
public class DeleteNoteServlet extends HttpServlet
{
    @Inject
    private NoteService noteService;

    public void init() {

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        return;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");

        JsonObject json = RequestObjectGetter.getJson(request);
        if (json == null)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String noteId = json.getString("uuid");
        boolean success = noteService.DeleteNote(noteId);
        response.getWriter().write("{ \"success\":" + (success ? "true" : "false") + "}");
    }

    public void destroy()
    {
    }
}
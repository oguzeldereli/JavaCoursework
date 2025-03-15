package com.guzeldereli.coursework.cw.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guzeldereli.coursework.cw.common.RequestObjectGetter;
import com.guzeldereli.coursework.cw.models.NoteFragment;
import com.guzeldereli.coursework.cw.services.FragmentService;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@WebServlet(name = "deleteFragmentServlet", value = "/fragments/delete")
public class DeleteFragmentServlet extends HttpServlet
{
    @Inject
    private FragmentService fragmentService;
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");

        JsonObject json = RequestObjectGetter.getJson(request);
        if (json == null)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String noteUUID = json.getString("note-uuid");
        String fragmentUUID = json.getString("fragment-uuid");
        boolean success = fragmentService.DeleteNoteFragment(noteUUID, fragmentUUID);
        response.getWriter().write("{ \"success\":" + (success ? "true" : "false") + "}");
    }
}

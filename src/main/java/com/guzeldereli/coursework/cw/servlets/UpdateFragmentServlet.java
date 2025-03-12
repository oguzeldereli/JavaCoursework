package com.guzeldereli.coursework.cw.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebServlet(name = "updateFragmentServlet", value = "/fragments/update")
public class UpdateFragmentServlet extends HttpServlet
{
    @Inject
    private FragmentService fragmentService;
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");

        JsonObject json;
        try (JsonReader reader = Json.createReader(request.getInputStream())) {
            json = reader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{ \"success\": false, \"error\": \"Failed to parse JSON\" }");
            return;
        }

        String noteUUID = json.getString("note-uuid");
        String fragmentJSON = json.getJsonObject("fragment").toString();
        ObjectMapper mapper = new ObjectMapper();
        NoteFragment fragment = mapper.readValue(fragmentJSON, NoteFragment.class);
        boolean success = fragmentService.UpdateNoteFragment(noteUUID, fragment);
        response.getWriter().write("{ \"success\":" + (success ? "true" : "false") + "}");
    }
}

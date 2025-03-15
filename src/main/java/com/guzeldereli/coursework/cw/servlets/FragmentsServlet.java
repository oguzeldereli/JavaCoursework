package com.guzeldereli.coursework.cw.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guzeldereli.coursework.cw.common.RequestObjectGetter;
import com.guzeldereli.coursework.cw.models.Note;
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
import java.lang.reflect.Array;
import java.util.ArrayList;

@WebServlet(name = "fragmentsServlet", value = "/fragments")
public class FragmentsServlet extends HttpServlet
{
    @Inject
    private FragmentService fragmentService;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
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

        String noteUUID = json.getString("uuid");
        ArrayList<NoteFragment> fragments = fragmentService.GetFragments(noteUUID);
        ObjectMapper objectMapper = new ObjectMapper();
        String fragmentsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fragments);
        response.getWriter().write(fragmentsJson);
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

        String noteUUID = json.getString("uuid");
        String fragmentType = json.getString("type");
        NoteFragment fragment = fragmentService.CreateNoteFragment(noteUUID, fragmentType);
        ObjectMapper objectMapper = new ObjectMapper();
        String fragmentJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fragment);
        response.getWriter().write(fragmentJson);
    }
}

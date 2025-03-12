package com.guzeldereli.coursework.cw.servlets;

import com.guzeldereli.coursework.cw.services.FileService;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;

@WebServlet(name = "deleteFilesServlet", value = "/files/delete")
public class DeleteFilesServlet extends HttpServlet
{
    @Inject
    private FileService fileService;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        JsonObject json;
        try (JsonReader reader = Json.createReader(request.getInputStream())) {
            json = reader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String filename = json.getString("filename");
        fileService.DeleteFile(filename);
    }
}

package com.guzeldereli.coursework.cw.servlets;

import com.guzeldereli.coursework.cw.services.FileService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;

@WebServlet(name = "filesServlet", value = "/files/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB before writing to disk
        maxFileSize = 1024 * 1024 * 10, // 10MB max file size
        maxRequestSize = 1024 * 1024 * 50 // 50MB max request size
)
public class FilesServlet extends HttpServlet
{
    @Inject
    private FileService fileService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/"))
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Image name is missing.");
            return;
        }

        File file = fileService.GetFile(pathInfo.substring(1));
        if (file == null)
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mimeType = getServletContext().getMimeType(file.getName());
        if (mimeType == null)
        {
            mimeType = "application/octet-stream";
        }

        response.setContentType(mimeType);
        response.setContentLengthLong(file.length());

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream())
        {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1)
            {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Part filePart = request.getPart("file");
        String filename = fileService.WriteFile(filePart);
        response.getWriter().write("{ \"filename\":\"" + filename + "\"}");
    }
}

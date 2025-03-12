package com.guzeldereli.coursework.cw.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Named("fileService")
@ApplicationScoped
public class FileServiceImplementation implements FileService
{
    private String extractFileName(Part part)
    {
        String contentDisp = part.getHeader("content-disposition");
        for (String content : contentDisp.split(";"))
        {
            if (content.trim().startsWith("filename"))
            {
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
            }
        }
        return null;
    }

    private String GetFilesPath()
    {
        String userHome = System.getProperty("user.home");
        Path userHomePath = Paths.get(userHome);
        userHomePath = userHomePath.resolve("notes-app-data/files/");

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

    @Override
    public String GetExtension(String filename)
    {
        if(filename == null)
            return null;
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    @Override
    public String WriteFile(Part filePart) throws IOException
    {
        Path dirPath = Paths.get(GetFilesPath());
        String originalFileName = extractFileName(filePart);
        String extension = GetExtension(originalFileName);
        String newFileName = java.util.UUID.randomUUID().toString() + "." + extension;

        Path filePath = dirPath.resolve(newFileName);

        try (InputStream inputStream = filePart.getInputStream())
        {
            Files.copy(inputStream, filePath);
            return newFileName;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public File GetFile(String filename)
    {
        Path path = Paths.get(GetFilesPath());
        path = path.resolve(filename);
        if(!Files.exists(path))
        {
            return null;
        }

        return path.toFile();
    }

    @Override
    public void DeleteFile(String filename)
    {
        Path path = Paths.get(GetFilesPath());
        path = path.resolve(filename);

        if(Files.exists(path))
        {
            try
            {
                Files.delete(path);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
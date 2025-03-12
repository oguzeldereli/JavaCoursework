package com.guzeldereli.coursework.cw.services;

import jakarta.servlet.http.Part;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public interface FileService
{
    public String WriteFile(Part filePart) throws IOException;
    public File GetFile(String filename);
    public void DeleteFile(String filename);
    public String GetExtension(String filename);
}

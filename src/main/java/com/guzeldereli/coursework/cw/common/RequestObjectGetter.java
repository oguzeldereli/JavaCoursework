package com.guzeldereli.coursework.cw.common;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.http.HttpServletRequest;

import java.net.http.HttpRequest;

public class RequestObjectGetter {
    public static JsonObject getJson(HttpServletRequest request)
    {
        JsonObject json;
        try (JsonReader reader = Json.createReader(request.getInputStream()))
        {
            json = reader.readObject();
            return json;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}

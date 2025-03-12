package com.guzeldereli.coursework.cw.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("url")
public class NoteUrl extends NoteFragment
{
    @JsonProperty("url")
    public String Url;

    public NoteUrl(String url)
    {
        super();
        Url = url;
    }

    public NoteUrl()
    {
        super();
        Url = "";
    }
}

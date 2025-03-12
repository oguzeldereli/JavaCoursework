package com.guzeldereli.coursework.cw.models;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("image")
public class NoteImage extends NoteFragment
{
    @JsonProperty("isImageRemote")
    public boolean IsImageRemote;
    @JsonProperty("imagePath")
    public String ImagePath;

    public NoteImage()
    {
        super();
        IsImageRemote = false;
        ImagePath = "";
    }
}

package com.guzeldereli.coursework.cw.models;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("text")
public class NoteText extends NoteFragment
{
    @JsonProperty("text")
    public String Text;

    public NoteText(String text)
    {
        Text = text;
    }

    public NoteText()
    {
        super();
        Text = "";
    }
}

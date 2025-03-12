package com.guzeldereli.coursework.cw.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NoteText.class, name = "text"),
        @JsonSubTypes.Type(value = NoteImage.class, name = "image"),
        @JsonSubTypes.Type(value = NoteUrl.class, name = "url")
})
public class NoteFragment
{
    @JsonProperty("uuid")
    public String UUID;

    public NoteFragment()
    {
        UUID = java.util.UUID.randomUUID().toString();
    }

    public String getType()
    {
        if (this instanceof NoteText)
            return "text";
        else if (this instanceof NoteImage)
            return "image";
        else if (this instanceof NoteUrl)
            return "url";
        else
            return null;
    }
}

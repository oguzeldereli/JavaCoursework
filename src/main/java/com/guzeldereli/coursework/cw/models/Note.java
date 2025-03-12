package com.guzeldereli.coursework.cw.models;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Note
{
    @JsonProperty("uuid")
    public final String UUID;
    @JsonProperty("title")
    public String Title;
    @JsonProperty("summary")
    public String Summary;
    @JsonProperty("categories")
    public final ArrayList<String> Categories = new ArrayList<String>();
    @JsonProperty("fragments")
    private final ArrayList<NoteFragment> _fragments = new ArrayList<NoteFragment>();

    public Note()
    {
        this.UUID = java.util.UUID.randomUUID().toString();
    }

    public Note(String UUID)
    {
        this.UUID = UUID;
    }

    public void AddCategory(String category)
    {
        Categories.add(category);
    }

    public String GetCategory(int index)
    {
        return Categories.get(index);
    }

    @JsonIgnore
    public int CategoryCount()
    {
        return Categories.size();
    }

    public boolean HasCategory(String category)
    {
        return Categories.contains(category);
    }

    public void AddFragment(NoteFragment fragment)
    {
        _fragments.add(fragment);
    }

    public ArrayList<NoteFragment> GetFragments()
    {
        return _fragments;
    }

    public NoteFragment GetFragment(String UUID)
    {
        return _fragments.stream().filter(fragment -> fragment.UUID.equals(UUID)).findFirst().orElse(null);
    }

    public boolean UpdateNoteFragment(NoteFragment fragment)
    {
        NoteFragment oldFragment = GetFragment(fragment.UUID);
        if(oldFragment == null)
        {
            return false;
        }

        _fragments.remove(oldFragment);
        _fragments.add(fragment);
        return true;
    }

    public boolean RemoveFragment(String UUID)
    {
        NoteFragment oldFragment = GetFragment(UUID);
        if (oldFragment == null)
        {
            return false;
        }

        _fragments.remove(oldFragment);
        return true;
    }

    @JsonIgnore
    public int FragmentCount()
    {
        return _fragments.size();
    }

    public Pair<Boolean, Integer> SearchText(String text)
    {
        int counter = 0;
        boolean found = false;
        if(Title.contains(text))
        {
            counter += 100;
            found = true;
        }

        if(Summary.contains(text))
        {
            counter += 5;
            found = true;
        }

        for(NoteFragment fragment : _fragments)
        {
            if(fragment instanceof NoteText)
            {
                NoteText noteText = (NoteText) fragment;
                counter += noteText.Text.split(text, -1).length - 1;
                if(noteText.Text.contains(text))
                {
                    found = true;
                }
            }

        }

        return Pair.of(found, counter);
    }

    public static Note FromXMLFile(String path)
    {
        Document doc = GetXMLDocument(path);
        if(doc == null)
        {
            return null;
        }

        // Confirm that the file is a Note
        if(!doc.getDocumentElement().getNodeName().equals("note"))
        {
            return null;
        }

        Note note = new Note(doc.getDocumentElement().getAttribute("uuid"));
        note.Title = doc.getDocumentElement().getAttribute("title");
        note.Summary = doc.getDocumentElement().getAttribute("summary");

        // Get Categories
        NodeList categoriesNodes = doc.getElementsByTagName("categories");
        for (int i = 0; i < categoriesNodes.getLength(); i++)
        {
            Node categoriesNode = categoriesNodes.item(i);
            if(categoriesNode.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }

            Element categoriesElement = (Element) categoriesNode;
            NodeList categoryList = categoriesElement.getElementsByTagName("category");
            for (int j = 0; j < categoryList.getLength(); j++)
            {
                Element categoryElement = (Element) categoryList.item(j);
                String category = categoryElement.getTextContent();
                note.Categories.add(category);
            }
        }

        // Get Fragments
        Element fragmentsNode = (Element) doc.getElementsByTagName("fragments").item(0);
        NodeList fragmentList = fragmentsNode.getElementsByTagName("fragment");
        for (int i = 0; i < fragmentList.getLength(); i++)
        {
            Node nNode = fragmentList.item(i);
            if(nNode.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }

            Element eElement = (Element) nNode;
            NoteFragment fragment = NodeElementToNoteFragment(eElement);
            note.AddFragment(fragment);
        }

        return note;
    }

    public void ToXMLFile(String path)
    {
        try
        {
            Path p = Paths.get(path);
            if(Files.exists(p)) {
                Files.delete(p);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return;
        }

        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("note");
            rootElement.setAttribute("uuid", this.UUID);
            rootElement.setAttribute("title", this.Title);
            rootElement.setAttribute("summary", this.Summary);
            doc.appendChild(rootElement);

            Element categoriesElement = doc.createElement("categories");
            rootElement.appendChild(categoriesElement);

            // Add Categories
            for (String category : Categories)
            {
                Element categoryElement = doc.createElement("category");
                categoryElement.appendChild(doc.createTextNode(category));
                categoriesElement.appendChild(categoryElement);
            }

            Element fragmentsElement = doc.createElement("fragments");
            rootElement.appendChild(fragmentsElement);

            // Add Fragments
            for (NoteFragment fragment : _fragments)
            {
                Element fragmentElement = doc.createElement("fragment");
                fragmentElement.setAttribute("type", fragment.getType());
                fragmentElement.setAttribute("uuid", fragment.UUID);

                if (fragment instanceof NoteText)
                {
                    NoteText noteText = (NoteText) fragment;
                    fragmentElement.appendChild(doc.createTextNode(noteText.Text));
                }
                else if (fragment instanceof NoteImage)
                {
                    NoteImage noteImage = (NoteImage) fragment;
                    fragmentElement.setAttribute("isRemote", String.valueOf(noteImage.IsImageRemote));
                    fragmentElement.appendChild(doc.createTextNode(noteImage.ImagePath));
                }
                else if (fragment instanceof NoteUrl)
                {
                    NoteUrl noteUrl = (NoteUrl) fragment;
                    fragmentElement.appendChild(doc.createTextNode(noteUrl.Url));
                }

                fragmentsElement.appendChild(fragmentElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(path));

            transformer.transform(source, result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String SerializeToJson(Note note)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(note);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static Note DeserializeFromJson(String json)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, Note.class);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static Document GetXMLDocument(String path)
    {
        File file = new File(path);
        try (FileInputStream fis = new FileInputStream(file))
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fis);
            doc.getDocumentElement().normalize();
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static NoteText NodeElementToNoteText(Element NodeElement)
    {
        NoteText noteText = new NoteText();
        noteText.UUID = NodeElement.getAttribute("uuid");
        noteText.Text = NodeElement.getTextContent();
        return noteText;
    }

    private static NoteImage NodeElementToNoteImage(Element NodeElement)
    {
        NoteImage noteImage = new NoteImage();
        noteImage.UUID = NodeElement.getAttribute("uuid");
        noteImage.IsImageRemote = NodeElement.getAttribute("isRemote").equals("true");
        noteImage.ImagePath = NodeElement.getTextContent();
        return noteImage;
    }

    private static NoteUrl NodeElementToNoteUrl(Element NodeElement)
    {
        NoteUrl noteUrl = new NoteUrl();
        noteUrl.UUID = NodeElement.getAttribute("uuid");
        noteUrl.Url = NodeElement.getTextContent();
        return noteUrl;
    }

    private static NoteFragment NodeElementToNoteFragment(Element NodeElement)
    {
        String type = NodeElement.getAttribute("type");
        switch (type)
        {
            case "text":
                return (NoteFragment) NodeElementToNoteText(NodeElement);
            case "image":
                return (NoteFragment) NodeElementToNoteImage(NodeElement);
            case "url":
                return (NoteFragment) NodeElementToNoteUrl(NodeElement);
            default:
                return null;
        }
    }
}

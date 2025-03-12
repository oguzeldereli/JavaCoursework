import {clearNoteEditor, renderNoteEditor} from "./noteEditorRenderer.js";
import {getNote, getNotes, loadNotes, setNotes} from "./noteIndexManager.js";

let selectedNoteUUID = null;
let selectedNote = null;

export async function setSelectedNote(UUID)
{
    selectedNoteUUID = UUID;
    selectedNote = await getNote(UUID);
    await renderNoteEditor(selectedNote);
}

export async function getSelectedNote()
{
    return selectedNote;
}

export async function getSelectedNoteUUID()
{
    return selectedNoteUUID;
}

export async function deleteSelectedNote()
{
    if(!selectedNoteUUID || !selectedNote)
    {
        return;
    }

    const response = await fetch("notes/delete", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Content-Length": JSON.stringify({ uuid: selectedNoteUUID }).length.toString()
        },
        body: JSON.stringify({ uuid: selectedNoteUUID })
    });
    if(!response.ok)
    {
        console.log("A problem occurred while deleting the note.");
    }

    const {success} = await response.json();
    if(!success)
    {
        console.log("A problem occurred while deleting the note.");
    }

    await loadNotes();
    await setSelectedNote(null);
}

export async function updateSelectedNote(changeFunction)
{
    if(!selectedNoteUUID || !selectedNote)
    {
        return;
    }

    changeFunction(selectedNote);

    const response = await fetch("notes/update", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Content-Length": JSON.stringify(selectedNote).length.toString()
        },
        body: JSON.stringify(selectedNote)
    });
    if(!response.ok)
    {
        console.log("A problem occurred while updating the note.");
    }

    const {success} = await response.json();
    if(!success)
    {
        console.log("A problem occurred while updating the note.");
    }

    await loadNotes();
}

export async function AddFragmentToSelectedNote(type)
{
    if(!selectedNoteUUID || !selectedNote)
    {
        return;
    }

    const response = await fetch("fragments", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Content-Length": JSON.stringify({ uuid: selectedNoteUUID, type: type  }).length.toString()
        },
        body: JSON.stringify({ uuid: selectedNoteUUID, type: type })
    });
    if(!response.ok)
    {
        console.log("A problem occurred while adding the fragment.");
        return;
    }

    const fragment = await response.json();

    await loadNotes();
    await setSelectedNote(selectedNoteUUID);

    return fragment;
}

export async function DeleteFragmentFromSelectedNote(fragmentUUID)
{
    if(!selectedNoteUUID || !selectedNote)
    {
        return;
    }

    const response = await fetch("fragments/delete", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Content-Length": JSON.stringify({  "note-uuid": selectedNoteUUID, "fragment-uuid": fragmentUUID  }).length.toString()
        },
        body: JSON.stringify({ "note-uuid": selectedNoteUUID, "fragment-uuid": fragmentUUID })
    });
    if(!response.ok)
    {
        console.log("A problem occurred while deleting the fragment.");
    }

    await loadNotes();
    await setSelectedNote(selectedNoteUUID);
}

export async function UpdateFragmentOfSelectedNote(fragment)
{
    if(!selectedNoteUUID || !selectedNote)
    {
        return;
    }

    const response = await fetch("fragments/update", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Content-Length": JSON.stringify({ "note-uuid": selectedNoteUUID, "fragment": fragment }).length.toString()
        },
        body: JSON.stringify({ "note-uuid": selectedNoteUUID, "fragment": fragment })
    });
    if(!response.ok)
    {
        console.log("A problem occurred while updating the fragment.");
        return;
    }

    await loadNotes();
    await setSelectedNote(selectedNoteUUID);
}

export async function UploadFile(file)
{
    let formData = new FormData();
    formData.append("file", file);

    let response = await fetch("files", {
        method: "POST",
        body: formData
    });

    const {filename} = await response.json();
    return filename;
}

export async function DeleteFile(file)
{
    let response = await fetch("files/delete", {
        method: "POST",
        body: JSON.stringify({filename: file})
    });

    if(!response.ok)
    {
        console.log("An error occurred while deleting the file.");
    }
}
import {renderNoteIndex} from "./noteIndexRenderer.js";

let notes = null;

export function getNotes()
{
    return notes;
}

export async function setNotes(newNotes)
{
    notes = newNotes;
    await renderNoteIndex(notes);
}

export async function getNote(uuid)
{
    return notes.find(note => note.uuid === uuid);
}

export async function loadNotes() {
    const searchBox = document.getElementById("search-query-field");
    const categoryBox = document.getElementById("category-field");

    const categories = categoryBox.value.split(',').map(cat => cat.trim()).filter(cat => cat !== "");
    const searchQuery = searchBox.value ? `query=${encodeURIComponent(searchBox.value)}` : '';

    const categoryParams = categories.map(cat => `category=${encodeURIComponent(cat)}`).join('&');
    const queryParams = [searchQuery, categoryParams].filter(param => param).join('&');

    try {
        const response = await fetch(`notes?${queryParams}`);

        if (!response.ok) {
            console.log("An error occurred while fetching notes.");
            return;
        }

        const notes = await response.json();
        await setNotes(notes);
    } catch (err) {
        console.log("An error occurred while fetching notes:", err);
    }
}

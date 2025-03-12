import {loadNotes} from "./noteIndexManager.js";
import {setSelectedNote} from "./noteEditorManager.js";

document.addEventListener("DOMContentLoaded", async () => {
    await loadNotes();
});

document.getElementById("create-new-note-button").addEventListener("click", async (ev) => {
    const response = await fetch("notes", {method: "POST"}).catch((err) => console.log("An error occurred while creating a new note."));
    if(!response.ok)
    {
        console.log("An error occurred while fetching notes.");
        return;
    }

    const note = await response.json();
    await loadNotes();
    await setSelectedNote(note.uuid);
});

document.addEventListener("click", (ev) => {
    if (!ev.target.classList.contains('dropdown-button')) {
        const dropdowns = document.getElementsByClassName("dropdown-content");
        let i;
        for (i = 0; i < dropdowns.length; i++) {
            const openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('dropdown-show')) {
                openDropdown.classList.remove('dropdown-show');
            }
        }
    }
});

document.addEventListener("click", (ev) => {
    if (ev.target.classList.contains('modal')) {
        const modals = document.getElementsByClassName("modal");
        let i;
        for (i = 0; i < modals.length; i++) {
            const openModal = modals[i];
            openModal.remove();
        }
    }
});

document.getElementById("search-query-field").addEventListener("input", async (ev) => {
    await loadNotes();
    await setSelectedNote(null);
});

document.getElementById("category-field").addEventListener("input", async (ev) => {
    await loadNotes();
    await setSelectedNote(null);
});
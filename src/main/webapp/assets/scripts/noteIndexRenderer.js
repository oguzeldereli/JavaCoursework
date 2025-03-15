import {getSelectedNoteUUID, setSelectedNote} from "./noteEditorManager.js";

export async function clearNoteIndex()
{
    const indexTitle = document.getElementById("note-index-title");
    indexTitle && indexTitle.remove();
    let notesContainer = document.getElementById("note-index");
    notesContainer.innerHTML = "";
}

export async function renderNotesContainer(notes)
{
    let notesContainer = document.getElementById("note-index");
    notesContainer.innerHTML = "<p>Loading notes...</p>";
    if(notes.length === 0)
    {
        notesContainer.innerHTML = "<div id='note-index-empty-placeholder'><img alt='No notes available image, a confused cartoon character' src='assets/images/empty.png' /><p>No Notes available</p><p>Use the 'New Note' button above to add one</p></div>";
        return;
    }

    notesContainer.innerHTML = "";
    return notesContainer;
}

export async function renderNoteContainerTitle(container)
{
    let noteContainerTitle = document.createElement("div");
    noteContainerTitle.id = "note-index-title";
    noteContainerTitle.textContent = "Note Index";
    container.parentNode.insertBefore(noteContainerTitle, container);
}

export async function renderNote(container, note)
{
    let noteContainerElement = document.createElement("div");
    let noteUUIDElement = document.createElement("div");
    let noteTitleElement = document.createElement("div");
    let noteSummaryElement = document.createElement("div");
    let noteCategoryContainer = document.createElement("div");

    noteContainerElement.classList.add("note-index-item");
    noteUUIDElement.classList.add("note-index-item-uuid");
    noteTitleElement.classList.add("note-index-item-title");
    noteSummaryElement.classList.add("note-index-item-summary");
    noteCategoryContainer.style.display = "flex";
    noteCategoryContainer.style.flexWrap = "wrap";
    noteCategoryContainer.style.gap = "0.2rem";
    noteCategoryContainer.style.padding = "0.2rem 0";

    noteContainerElement.onclick = async (ev) => await setSelectedNote(note.uuid);

    noteUUIDElement.textContent = note.uuid;
    noteTitleElement.textContent = note.title || "-No Name-";
    noteSummaryElement.textContent = note.summary || "-No Summary-";

    noteContainerElement.appendChild(noteUUIDElement);
    noteContainerElement.appendChild(noteTitleElement);
    noteContainerElement.appendChild(noteSummaryElement);
    noteContainerElement.appendChild(noteCategoryContainer);
    container.appendChild(noteContainerElement);

    for(const category of note.categories) {
        const categoryChip = document.createElement("div");
        categoryChip.classList.add("note-editor-category-chip");
        const categoryChipText = document.createElement("span");
        categoryChipText.textContent = category;
        categoryChip.appendChild(categoryChipText);
        noteCategoryContainer.appendChild(categoryChip);
    }
}

export async function renderNoteIndex(notes)
{
    await clearNoteIndex();
    let notesContainer = await renderNotesContainer();
    await renderNoteContainerTitle(notesContainer);

    for (const note of notes) {
        await renderNote(notesContainer, note);
    }
}
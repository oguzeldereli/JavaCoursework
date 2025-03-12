import {
    AddFragmentToSelectedNote, DeleteFragmentFromSelectedNote,
    deleteSelectedNote, getSelectedNoteUUID,
    setSelectedNote, UpdateFragmentOfSelectedNote,
    updateSelectedNote, UploadFile
} from "./noteEditorManager.js";

export async function clearNoteEditor()
{
    let noteEditorContainer = document.getElementById("note-editor");
    noteEditorContainer.innerHTML = "";
    console.log("rerendered");
}

export async function renderNoteEditor(note)
{
    await clearNoteEditor();
    if(!note)
    {
        return;
    }

    let noteEditorContainer = document.getElementById("note-editor");
    noteEditorContainer.innerHTML =
        `
        <div class='note-editor-container'>
            <div class="note-editor-row">
                <div id="note-editor-title-container">
                    <input aria-label="Note Title" id='note-editor-title-field' type='text' placeholder='Title'/>
                </div>
            </div>
            <div class="note-editor-row">
                <div id="note-editor-summary-container">
                    <textarea aria-label="Note Summary" class="note-editor-text-area" id='note-editor-summary-field' placeholder='Summary'></textarea>
                </div>
            </div>
            <div class="note-editor-row">
                <div id="note-editor-category-container">
                    <button id="note-editor-add-category-button" class="note-editor-category-chip"><span>Add Category &plus;</span></button>
                </div>
            </div>
            <div id="note-editor-section-container">
                
            </div>
            <div class="note-editor-row">
                <div class="dropdown">
                    <button class="dropdown-button" id="note-editor-add-section-button">Add Note Section</button>
                    <div class="dropdown-content">
                        <div class="note-editor-add-section-dropdown-content"> 
                            <button id="note-editor-add-text-section-button" class="note-editor-add-section-dropdown-button">Text</button>
                            <button id="note-editor-add-image-section-button" class="note-editor-add-section-dropdown-button">Image</button>
                            <button id="note-editor-add-url-section-button" class="note-editor-add-section-dropdown-button">Url</button>
                        </div>
                    </div>
                </div>
                <button id="note-editor-close-button">Close Note</button>
                <button id="note-editor-delete-button">Delete Note</button>
            </div>
        </div>
        `;


    const deleteButton = document.getElementById("note-editor-delete-button");
    deleteButton.addEventListener("click", async () => await deleteSelectedNote());

    const closeButton = document.getElementById("note-editor-close-button");
    closeButton.addEventListener("click", async () => await setSelectedNote(null));

    const titleField = document.getElementById("note-editor-title-field");
    titleField.value = note.title;
    titleField.addEventListener("input", async (ev) => {
        await updateSelectedNote((note) => note.title = ev.target.value);
    });

    const summaryField = document.getElementById("note-editor-summary-field");
    summaryField.value = note.summary;
    summaryField.style.height = 'auto';
    summaryField.style.height = summaryField.scrollHeight + 'px';
    summaryField.addEventListener("input", async (ev) => {
        summaryField.style.height = 'auto';
        summaryField.style.height = summaryField.scrollHeight + 'px';
        await updateSelectedNote((note) => note.summary = ev.target.value);
    });

    const addSectionButton = document.getElementById("note-editor-add-section-button");
    addSectionButton.addEventListener("click", (ev) => {
        const contents = ev.target.parentNode.getElementsByClassName("dropdown-content");
        for (const content of contents)
        {
            content.classList.toggle("dropdown-show");
        }
    });

    const addTextSectionButton = document.getElementById("note-editor-add-text-section-button");
    addTextSectionButton.addEventListener("click", async (ev) => {
        await AddFragmentToSelectedNote("text");
    });

    const addImageSectionButton = document.getElementById("note-editor-add-image-section-button");
    addImageSectionButton.addEventListener("click", async (ev) => {
        await ShowAddImageModal();
    });

    const addUrlSectionButton = document.getElementById("note-editor-add-url-section-button");
    addUrlSectionButton.addEventListener("click", async (ev) => {
        await ShowAddUrlModal();
    });

    const addCategorySectionButton = document.getElementById("note-editor-add-category-button");
    addCategorySectionButton.addEventListener("click", async (ev) => {
        await ShowAddCategoryModal();
    });

    if(!note.fragments)
    {
        return;
    }

    const sectionContainer = document.getElementById("note-editor-section-container");
    let i = 1;
    for(const fragment of note.fragments)
    {
        const row = document.createElement("div");
        row.classList.add("note-editor-row");
        sectionContainer.appendChild(row);

        if(fragment.type === "text")
        {
            const textArea = document.createElement("textarea");
            textArea.classList.add("note-editor-text-area");
            textArea.ariaLabel = "Text section";
            textArea.id = fragment.uuid;
            textArea.textContent = fragment.text;
            textArea.placeholder = `Section ${i}`;
            i += 1;
            textArea.addEventListener("input", async (ev) => {
                textArea.style.height = 'auto';
                textArea.style.height = textArea.scrollHeight + 'px';

                await updateSelectedNote((note) => {
                    const frag = note.fragments.find(f => f.uuid === fragment.uuid);
                    frag.text = ev.target.value;
                });
            });

            row.appendChild(textArea);
            textArea.style.height = 'auto';
            textArea.style.height = textArea.scrollHeight + 'px';

        }
        else if(fragment.type === "url")
        {
            const url = document.createElement("a");
            url.href = fragment.url;
            url.textContent = fragment.url;
            url.classList.add("note-editor-url");
            row.appendChild(url);
        }
        else if(fragment.type === "image")
        {
            const image = document.createElement("img");
            image.src = fragment.isImageRemote ? fragment.imagePath : "./files/" + fragment.imagePath;
            image.classList.add("note-editor-img");
            row.appendChild(image);
        }
        else
        {
            const errText = document.createElement("div");
            errText.textContent = "Invalid section type. Please delete this section.";
            row.appendChild(errText);
        }

        const moveUpButton = document.createElement("button");
        moveUpButton.classList.add("note-editor-move-section-button");
        moveUpButton.innerHTML = `&and;`;
        moveUpButton.addEventListener("click", async (ev) => {
            await updateSelectedNote((note) => {
                const index = note.fragments.findIndex(frag => frag.uuid === fragment.uuid);
                if (index > 0)
                {
                    [note.fragments[index], note.fragments[index - 1]] = [note.fragments[index - 1], note.fragments[index]];
                }
            });
            await setSelectedNote(await getSelectedNoteUUID());
        });
        row.appendChild(moveUpButton);

        const moveDownButton = document.createElement("button");
        moveDownButton.classList.add("note-editor-move-section-button");
        moveDownButton.innerHTML = `&or;`;
        moveDownButton.addEventListener("click", async (ev) => {
            await updateSelectedNote((note) => {
                const index = note.fragments.findIndex(frag => frag.uuid === fragment.uuid);
                if (index !== -1 && index < note.fragments.length - 1)
                {
                    [note.fragments[index], note.fragments[index + 1]] = [note.fragments[index + 1], note.fragments[index]];
                }
            });
            await setSelectedNote(await getSelectedNoteUUID());
        });
        row.appendChild(moveDownButton);

        const deleteAreaButton = document.createElement("button");
        deleteAreaButton.classList.add("note-editor-delete-section-button");
        deleteAreaButton.innerHTML = `&times;`;
        deleteAreaButton.addEventListener("click", async (ev) => {
            await DeleteFragmentFromSelectedNote(fragment.uuid);
        });
        row.appendChild(deleteAreaButton);
    }

    const categoriesContainer = document.getElementById("note-editor-category-container");

    for(const category of note.categories) {
        const categoryChip = document.createElement("div");
        categoryChip.classList.add("note-editor-category-chip");
        const deleteCategoryButton = document.createElement("i");
        deleteCategoryButton.classList.add("note-editor-category-delete-button");
        deleteCategoryButton.classList.add("fa-solid");
        deleteCategoryButton.classList.add("fa-xmark");
        const categoryChipText = document.createElement("span");
        categoryChipText.textContent = category;
        categoryChip.appendChild(categoryChipText);
        categoryChip.appendChild(deleteCategoryButton);
        categoriesContainer.insertBefore(categoryChip, categoriesContainer.firstChild);

        deleteCategoryButton.addEventListener("click", async (ev) => {
            if(note.categories && category)
            {
                var index = note.categories.indexOf(category);
                if (index !== -1) {
                    note.categories.splice(index, 1);
                }
                await setSelectedNote(await getSelectedNoteUUID());
            }
        });
    }
}

export async function ShowAddUrlModal()
{
    const modal = document.createElement("div");
    modal.innerHTML =
        `
        <div class="modal">
            <div class="modal-content">
                <p style="display: block; font-family: 'Montserrat', 'Roboto', sans-serif; font-size: 1.5rem; font-weight: 500; padding: 0.2rem 0; margin: 0;">Add Url</p>
                <div style="display: flex;">
                    <input id="note-editor-url-field" type="text" aria-label="Add Url" placeholder="Add Url..."/>
                    <button id="note-editor-url-add-button">&plus;</button>
                </div
            </div>
        </div>
        `;

    const editor = document.getElementById("note-editor");
    editor.appendChild(modal);

    const urlField = document.getElementById("note-editor-url-field");
    const button = document.getElementById("note-editor-url-add-button");

    button.addEventListener("click", async (ev) => {
        const fragment = await AddFragmentToSelectedNote("url");
        let urlValue = urlField.value.trim();
        if (!urlValue) return;

        try
        {
            let url = new URL(urlValue);
            if (url.protocol !== "https:")
            {
                url = new URL("https://" + urlValue.replace(/^(https?:\/\/)?/, ""));
            }
            urlField.value = url.href;
            fragment.url = url.href;
        }
        catch (error)
        {
            urlField.value = `https://${urlValue.replace(/^(https?:\/\/)?/, "")}`;
            fragment.url = urlField.value;
        }

        await UpdateFragmentOfSelectedNote(fragment);
        modal.remove();
    });
}

export async function ShowAddImageModal()
{
    const modal = document.createElement("div");
    modal.innerHTML =
        `
        <div class="modal">
            <div class="modal-content">
                <p style="display: block; font-family: 'Montserrat', 'Roboto', sans-serif; font-size: 1.5rem; font-weight: 500; padding: 0.2rem 0; margin: 0;">Add Url</p>
                <div style="display: flex;">
                    <input id="note-editor-img-url-field" type="text" aria-label="Add Image Url" placeholder="Add Image Url..."/>
                    <div id="note-editor-img-file-field" aria-label="Upload Image File">
                        <label for="note-editor-img-file-upload">Upload Image</label>
                        <input id="note-editor-img-file-upload" type="file" />   
                    </div>
                    <button id="note-editor-img-add-button">&plus;</button>
                </div>
                <p id="note-editor-img-filename" style="display: none;" />
            </div>
        </div>
        `;

    const editor = document.getElementById("note-editor");
    editor.appendChild(modal);

    const imageUrlField = document.getElementById("note-editor-img-url-field");
    const imageFileField = document.getElementById("note-editor-img-file-upload");
    const button = document.getElementById("note-editor-img-add-button");

    imageFileField.addEventListener("change", (ev) => {

        if(ev.target.files.length > 0)
        {
            const imageFilenameField = document.getElementById("note-editor-img-filename");
            imageFilenameField.textContent = imageFileField.files[0].name;
            imageFilenameField.style.display = "block";
            imageUrlField.value = "";
            imageUrlField.disabled = true;

            // No need for server validation since this is not a public application.
            const allowedFileExtensions = ["png", "jpg", "jpeg", "webp", "gif", "tiff", "bmp", "svg"];
            console.log(ev.target.files[0].name.split('.').findLast(p => p));
            if(!allowedFileExtensions.includes(ev.target.files[0].name.split('.')[1]))
            {
                imageFilenameField.textContent = "Invalid file, allowed extensions are \"png\", \"jpg\", \"jpeg\", \"webp\", \"gif\", \"tiff\", \"bmp\", \"svg\".";
                imageFileField.files = [];
            }
        }
        else
        {
            const imageFilenameField = document.getElementById("note-editor-img-filename");
            imageFilenameField.textContent = "";
            imageFilenameField.style.display = "none";
            imageUrlField.disabled = false;
        }
    });

    button.addEventListener("click", async (ev) => {
        let filename = null;
        let isImageRemote = null;
        if(imageFileField.files.length > 0)
        {
            filename = await UploadFile(imageFileField.files[0]);
            isImageRemote = false;
        }
        else
        {
            filename = imageUrlField.value;
            isImageRemote = true;
        }

        if(filename)
        {
            const fragment = await AddFragmentToSelectedNote("image");
            fragment.imagePath = filename;
            fragment.isImageRemote = isImageRemote;
            await UpdateFragmentOfSelectedNote(fragment);
            modal.remove();
        }
    });
}

export async function ShowAddCategoryModal()
{
    const modal = document.createElement("div");
    modal.innerHTML =
        `
        <div class="modal">
            <div class="modal-content">
                <p style="display: block; font-family: 'Montserrat', 'Roboto', sans-serif; font-size: 1.5rem; font-weight: 500; padding: 0.2rem 0; margin: 0;">Add Category</p>
                <div style="display: flex;">
                    <input id="note-editor-category-field" type="text" aria-label="Add Category" placeholder="Add Category..."/>
                    <button id="note-editor-category-add-button">&plus;</button>
                </div>
            </div>
        </div>
        `;

    const editor = document.getElementById("note-editor");
    editor.appendChild(modal);

    const categoryField = document.getElementById("note-editor-category-field");
    const button = document.getElementById("note-editor-category-add-button");

    button.addEventListener("click", async (ev) => {
        if (categoryField.value.trim()) {
            await updateSelectedNote((note) => {
                if (!note.categories) {
                    note.categories = [];
                }
                note.categories = [...note.categories, categoryField.value.trim()]; // Ensure array change is detected
            });

            await setSelectedNote(await getSelectedNoteUUID()); // Force UI refresh
            modal.remove();
        }
    });
}
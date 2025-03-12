<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Notes App</title>
    <link rel="stylesheet" href="assets/css/common.css" type="text/css"/>
    <link rel="stylesheet" href="assets/css/index.css" type="text/css"/>
    <script src="https://kit.fontawesome.com/d884f988f9.js" crossorigin="anonymous"></script>
</head>
<body>
    <nav class="navbar-container">
        <ul class="navbar" aria-label="navigation bar">
            <li class="navbar-logo" aria-label="navigation bar logo">
                <img alt="notes app logo, icon of a book" src="assets/images/book.png"/>
            </li>
        </ul>
    </nav>
    <main class="content-container">
        <div class="note-finder" aria-label="Note Finder">
            <input id="search-query-field" type="search" placeholder="Start typing to filter note index..." aria-label="Start typing to filter note index..."/>
            <input id="category-field" name="category-field" type="text" placeholder="Filter by comma-separated categories..." aria-label="Filter by comma-separated categories" />
            <button id="create-new-note-button" type="button">New Note</button>
        </div>
        <div id="note-editor" aria-label="Note Editor"></div>
        <div id="note-index" aria-label="Note Index"></div>
    </main>
    <script src="assets/scripts/index.js" type="module"></script>
</body>
</html>
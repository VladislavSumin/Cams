<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Cams</title>
</head>

<style>
    html, body {
        height: 100%;
        width: 100%;
        margin: 0;
    }
</style>

<body>
<div style="display: inline-flex; width: 100%; height: 100%">
    <div style="display: inline-flex;flex-direction: column">
        <div style="display: inline-flex; margin: 3px">
            <input type="button" id="btn_previous" value="<">
            <input type="date" id="calendar">
            <input type="button" id="btn_next" value=">">
        </div>
        <select id="list" style="width: 100%; height: 100%; font-family: monospace" size="2"></select>
    </div>
    <div style="display: inline-flex; flex: 1">
        <video id="video" style="width: 100%; height: 100%">
            Your browser does not support the video tag.
        </video>
    </div>
</div>
<script src="/js/main.js"></script>
</body>

</html>
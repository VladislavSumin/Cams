var list, player, calendar;
init();

function init() {
    //init list
    {
        list = document.getElementById("list");
        list.onchange = function () {
            var item = JSON.parse(this.options[this.selectedIndex].value);
            playVideo(item["id"]);
        };
        updateList(new Date());
    }

    //init player
    {
        player = document.getElementById("video");
        player.autoplay = true;
        player.controls = true;
        player.type = "video/mp4";
    }

    //init calendar;
    {
        calendar = document.getElementById("calendar");
        setCalendarDate(new Date());

        calendar.addEventListener("change", function () {
            var input = this.value;
            var dateEntered = new Date(input);
            updateList(dateEntered);
        });
    }

    //init btn previous && next
    {
        var prev = document.getElementById("btn_previous");
        prev.addEventListener("click", function (ev) {
            changeDateAndUpdateList(-1)
        });
        var next = document.getElementById("btn_next");
        next.addEventListener("click", function (ev) {
            changeDateAndUpdateList(1)
        });

    }
}

function changeDateAndUpdateList(deltaDate) {
    var date = new Date(calendar.value);
    date.setDate(date.getDate() + deltaDate);
    setCalendarDate(date);
    updateList(date);
}

function setCalendarDate(date) {
    var dd = date.getDate();
    var mm = date.getMonth() + 1; //January is 0!

    var yyyy = date.getFullYear();
    if (dd < 10) dd = '0' + dd;
    if (mm < 10) mm = '0' + mm;
    date = yyyy + '-' + mm + '-' + dd;
    calendar.value = date;
}

function updateList(date) {
    list.innerHTML = "";
    var data = getList(date);
    var reg = /(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})(\d{2})/;
    for (var i = 0; i < data.length; i++) {
        var option = document.createElement("option");
        option.value = JSON.stringify(data[i]);
        // var d = reg.exec(data[i]["name"]);
        // option.text = data[i]["camName"] + " " + d[4] + ":" + d[5] + ":" + d[6];
        // getHours(), getMinutes(), getSeconds(), getMilliseconds()
        var d = new Date(data[i]["timestamp"]);
        option.text = data[i]["camera"]["name"] + " " + d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds();
        list.appendChild(option);
    }
}

/**
 * request video list by one day
 * @param date
 * @returns {string}
 */
function getVideo(date) {
    var xmlHttp = new XMLHttpRequest();
    date.setHours(0, 0, 0, 0);
    var params = "date=" + date.toUTCString();
    xmlHttp.open("GET", "/api/v1/records?" + params, false); // false for synchronous request
    xmlHttp.send(null);
    return xmlHttp.responseText;
}

function getList(date) {
    // var records = [];
    var raw = JSON.parse(getVideo(date));
    return raw
    // var reg = /(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})(\d{2})/;
    //
    // for (var k in raw) {
    //     if (!raw.hasOwnProperty(k)) continue;
    //     raw[k].forEach(function (i) {
    //         var a = reg.exec(i);
    //         var d = new Date();
    //         d.setFullYear(+a[1], +a[2], +a[3]);
    //         d.setHours(+a[4], +a[5], +a[6]);
    //         records.push({"camName": k, "name": i, "date": d});
    //     });
    // }
    // return records.sort(function (a, b) {
    //     return b["name"].localeCompare(a["name"]);
    // });
}

/**
 * Воспроизводит указанную запись
 */
function playVideo(id) {
    player.src = "api/v1/records/record/" + id;
    player.load();
    player.play();
}
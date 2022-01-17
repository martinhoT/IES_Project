// Aux functions

function removeOptions(selectElement) {
    let i, L = selectElement.options.length - 1;
    for(i = L; i >= 0; i--) {
        selectElement.remove(i);
    }
}

function setOptions(res, frag, room){
    removeOptions(room);

    for (let i = 0; i < res.length; i++) {
        const option = document.createElement("option");
        option.value = "" + res[i];
        option.text = " " + res[i];
        frag.appendChild(option);
    }

    room.appendChild(frag);
}

function removeTBody() {
    const table = document.getElementById("blacklistTable");

    table.querySelectorAll('tbody').forEach((tbody, i) => {
        table.removeChild(tbody);
    });
}

function setBlacklistTable(data){
    document.getElementById("roomTable").style.visibility = "visible"

    //clean tbody
    removeTBody();

    const tableBody = document.createElement("tbody");

    data.forEach(function (rowData) {
        console.log(rowData);
        const row = document.createElement("tr");

        //roomId
        let cellData = rowData.roomId;

        let cell = document.createElement('td');
        cell.className = "column1";
        cell.innerHTML = cellData

        row.appendChild(cell);

        //email
        cellData = rowData.email;

        cell = document.createElement('td');
        cell.className = "column2";
        cell.innerHTML = cellData

        row.appendChild(cell);

        tableBody.appendChild(row);
    })

    document.getElementById("blacklistTable").appendChild(tableBody);
}

// ADD/REMOVE from blacklist

function setRoomValues() {
    const frag = document.createDocumentFragment();
    const room = document.getElementById("room");
    const dep = document.getElementById("dep");
    const getDep = dep.value;

    document.getElementById("roomTable").style.visibility = "hidden"

    console.log(getDep)

    $.ajax({
        type: 'GET',
        url: "http://" + location.hostname + ":84/api/getRooms",
        data: {
            Result : getDep
        },
        dataType: 'json',
        success: function (res) {
            setOptions(res, frag, room)
        }
    });

}
function addToBlacklist() {
    const email = document.getElementById("exampleInputEmail1").value;
    const room = document.getElementById("room").value;

    $.ajax({
        type:'POST',
        url : "http://" + location.hostname + ":84/api/addUserBlacklist",
        data: {
            Email : email,
            Room: room,
        },
        dataType: 'json',
        success: function(data) {
            console.log('success',data);
        },
    });
}

function removeBlacklist() {
    const email = document.getElementById("exampleInputEmail1").value;
    const room = document.getElementById("room").value;

    $.ajax({
        type:'POST',
        url :"http://" + location.hostname + ":84/api/removeUserBlacklist",
        data: {
            Email : email,
            Room: room,
        },
        dataType: 'json',
        success: function(data) {
            console.log('success',data);
        },
    });
}

// Blacklist by ROOM


function setRoomValuesForRoomModal() {
    const frag = document.createDocumentFragment();
    const room = document.getElementById("room2");
    const dep = document.getElementById("dep2");
    const getDep = dep.value;

    document.getElementById("roomTable").style.visibility = "hidden"

    console.log(getDep)

    $.ajax({
        type: 'GET',
        url: "http://" + location.hostname + ":84/api/getRooms",
        data: {
            Result : getDep
        },
        dataType: 'json',
        success: function (res) {
            setOptions(res, frag, room)
        }
    });

}

function blacklistByRoom() {
    const room = document.getElementById("room2").value;
    console.log("blacklistByRoom()");
    console.log(room);
    $.ajax({
        type:'POST',
        url :"http://" + location.hostname + ":84/api/blacklistByRoom",
        data: {
            Room: room,
        },
        dataType: 'json',
        success: function(data) {
            setBlacklistTable(data)
        }
    });
}

// Blacklist by DEPARTMENT


function setValuesForDepModal() {
    const frag = document.createDocumentFragment();
    const room = document.getElementById("room3");
    const dep = document.getElementById("dep3");
    const getDep = dep.value;

    document.getElementById("roomTable").style.visibility = "hidden"


    $.ajax({
        type: 'GET',
        url: "http://" + location.hostname + ":84/api/department",
        data: {
        },
        dataType: 'json',
        success: function (res) {
            setOptions(res, frag, room)
        }
    });

}

function blacklistByDep() {
    const dep = document.getElementById("dep3").value;

    $.ajax({
        type:'POST',
        url :"http://" + location.hostname + ":84/api/blacklistByDepartment",
        data: {
            Dep: dep,
        },
        dataType: 'json',
        success: function(data) {
            setBlacklistTable(data)
        }
    });
}


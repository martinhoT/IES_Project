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
        option.value = "" + res[i].id;
        option.text = " " + res[i].id;
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
        let cellData = rowData.room;

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

    $.ajax({
        type: 'GET',
        url: "http://" + location.hostname + ":84/api/room",
        data: {
            dep: getDep
        },
        dataType: 'json',
        success: function (data, textStatus, jqXHR) {
            setOptions(data[getDep], frag, room)
        }
    });

}
function addToBlacklist() {
    const email = document.getElementById("exampleInputEmail1").value;
    const room = document.getElementById("room").value;

    $.ajax({
        type: 'POST',
        url: "http://" + location.hostname + ":84/api/blacklist/user",
        contentType: "application/json",
        data: JSON.stringify({
            email: email,
            room: room,
        }),
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
        type: 'DELETE',
        url: "http://" + location.hostname + ":84/api/blacklist/user",
        contentType: "application/json",
        data: JSON.stringify({
            email: email,
            room: room,
        }),
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
        url: "http://" + location.hostname + ":84/api/room",
        data: {
            dep: getDep
        },
        dataType: 'json',
        success: function (data, textStatus, jqXHR) {
            setOptions(data[getDep], frag, room)
        }
    });

}

function blacklistByRoom() {
    const room = document.getElementById("room2").value;
    console.log("blacklistByRoom()");
    console.log(room);
    $.ajax({
        type: 'GET',
        url: "http://" + location.hostname + ":84/api/blacklist",
        data: {
            room: room,
        },
        dataType: 'json',
        success: function(data) {
            if (data.length > 0)
                setBlacklistTable(data)
            else{
                document.getElementById("roomTable").style.visibility = "hidden"
                alert("There's no blacklisted user for this room");
            }
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
        type: 'GET',
        url: "http://" + location.hostname + ":84/api/blacklist",
        data: {
            dep: dep,
        },
        dataType: 'json',
        success: function(data) {
            if (data.length > 0 )
                setBlacklistTable(data)
            else{
                document.getElementById("roomTable").style.visibility = "hidden"
                alert("There's no blacklisted user for this department");
            }
        }
    });
}


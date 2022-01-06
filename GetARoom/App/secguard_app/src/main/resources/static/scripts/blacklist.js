function removeOptions(selectElement) {
    let i, L = selectElement.options.length - 1;
    for(i = L; i >= 0; i--) {
        selectElement.remove(i);
    }
}

function setRoomValues() {
    const frag = document.createDocumentFragment();
    const room = document.getElementById("room");
    const dep = document.getElementById("dep");
    const getDep = dep.value;

    console.log(getDep)

    $.ajax({
        type: 'GET',
        url: "http://" + location.hostname + ":84/api/getRooms",
        data: {
            Result : getDep
        },
        dataType: 'json',
        success: function (res) {

            removeOptions(room);

            for (let i = 0; i < res.length; i++) {
                const option = document.createElement("option");
                option.value = "" + res[i];
                option.text = " " + res[i];
                frag.appendChild(option);
            }

            room.appendChild(frag);

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


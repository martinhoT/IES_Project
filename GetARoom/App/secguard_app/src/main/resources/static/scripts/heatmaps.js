function ViewModel() {
    this.department = ko.observable("0");
    this.floors = ko.observable([]);
    this.rooms = ko.observable([]);
    this.full_room_name = function(room) {
        return "/room/" + room
    };
};
var viewModel = new ViewModel();
ko.applyBindings(viewModel);

// Run on localhost
//client = new Paho.MQTT.Client("localhost", 1884, "", "");
client = new Paho.MQTT.Client(location.hostname, 1884, "", "");

// Set callback handlers
client.onConnectionLost = onConnectionLost;
client.onMessageArrived = onMessageArrived;

connected = false;
client.connect({onSuccess:onConnect});



// When a new floor is chosen, subscribe to its respective topic
// Also unsubscribe from the previously subscribed topics
$("#department_selected").change(function(e) {
    let dep_number = $("#department_selected").val()
    viewModel.department(dep_number)

    $.getJSON("http://fetcher:8080/api/department",
        function (data, textStatus, jqXHR) {
            n_floors = data.floors;
            viewModel.floors( Array.from(Array(n_floors), (_,i) => 1 + i) );
            $.getJSON("http://fetcher:8080/api/roomStyles", {"dep": dep_number},
                function (data, textStatus, jqXHR) {
                    // viewModel.rooms( Array.from(rooms, (v,_) => v.room.split(".")[2]) );
                    viewModel.rooms( data );
                }
            );
        }
    );

    if (connected) {
        client.unsubscribe(current_topic);
        current_topic = "status/" + dep_number + "/#";
        client.subscribe(current_topic);
    }
});

function onConnect() {
    console.log("Successfully connected to the broker.");
    // current_topic = "status/" + dep_number.replaceAll(".", "/") + "/#";
    // client.subscribe(current_topic);

    connected = true;
}

function onConnectionLost(responseObject) {
    if (responseObject.errorCode !== 0) {
        console.log("Connection lost:" + responseObject.errorMessage);
    }
}

function onMessageArrived(message) {
    msg = message.payloadString;
    console.log("Received message:" + msg);
    var stts = JSON.parse(msg);
    for (const roomid in stts) {
        if (Object.hasOwnProperty.call(stts, roomid)
            && document.getElementById("pct:" + roomid)!==null
            && document.getElementById("clr:" + roomid)!==null)
        {
            document.getElementById("pct:" + roomid).innerText = stts[roomid];
            occupacy = Number(stts[roomid]);
            if (occupacy < 20) {
                document.getElementById("clr:" + roomid).className = "box hm-empty";
            }
            else if (occupacy < 40) {
                document.getElementById("clr:" + roomid).className = "box hm-almost-empty";
            }
            else if (occupacy < 60) {
                document.getElementById("clr:" + roomid).className = "box hm-mid";
            }
            else if (occupacy < 80) {
                document.getElementById("clr:" + roomid).className = "box hm-almost-full";
            }
            else {
                document.getElementById("clr:" + roomid).className = "box hm-full";
            }
        }
    }
}
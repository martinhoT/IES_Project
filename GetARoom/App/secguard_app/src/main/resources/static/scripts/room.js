var ViewModel = {
    Events: ko.observableArray([])
}
ko.applyBindings(ViewModel, document.getElementById("ko-body"))

// Run on localhost
//client = new Paho.MQTT.Client("localhost", 1884, "", "");
client = new Paho.MQTT.Client(location.hostname, 1884, "", "");

// Set callback handlers
client.onConnectionLost = onConnectionLost;
client.onMessageArrived = onMessageArrived;

connected = false;
client.connect({onSuccess:onConnect});


function onConnect() {
    console.log("Successfully connected to the broker.");
    room = $("#room").text();
    current_topic = "event/" + room.replaceAll(".", "/");
    $.getJSON("http://" + location.hostname + ":84/api/event", {"room": room, "pageCapacity": 0},
        function (data, textStatus, jqXHR) {
            for(let event of data){
                ViewModel.Events.push(event);
            }
            client.subscribe(current_topic);
        }
    );
}

function onConnectionLost(responseObject) {
    if (responseObject.errorCode !== 0) {
        console.log("Connection lost:" + responseObject.errorMessage);
    }
}

function onMessageArrived(message) {
    msg = message.payloadString;
    console.log("Received message:" + msg);
    var evnt = JSON.parse(msg);
    roomid = stts["room"];

    let notPresent = true;
    for (let event of ViewModel.Events) {
        if (event.email === evnt.email &&
            event.user === evnt.user &&
            event.entered === evnt.entered &&
            new Date(event.time).getTime() === new Date(evnt.time).getTime())

            notPresent = false;
            break;
    }

    if (notPresent) {
        ViewModel.Events.push(evnt);
    }

}
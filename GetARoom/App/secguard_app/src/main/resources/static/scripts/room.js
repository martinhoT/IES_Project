var ViewModel = {
    Events: ko.observableArray([])
}
ko.applyBindings(ViewModel)

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
    current_topic = "event/#";
    room = $("#room").text();
    $.getJSON("http://localhost:84/api/event", {"room": room},
        function (data, textStatus, jqXHR) {
            for(let event of data){
                ViewModel.Events.push(event);
            }
    })
    client.subscribe(current_topic);
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
    roomid = stts["room"];

    if(roomid === $("#room").text()){
        ViewModel.Events.push(stts);
    }

}
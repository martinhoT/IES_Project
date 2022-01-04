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

client.connect({onSuccess:onConnect});


function onConnect() {
    console.log("Successfully connected to the broker.");
    current_topic = "event/#";
    $.getJSON("http://localhost:84/api/today",
        function (data, textStatus, jqXHR) {
            for(let event of data){
                ViewModel.Events.push(event);
            }
    })
    client.subscribe(current_topic);
    // $.getJSON("http://localhost:84/api/today", {"room": room},
    // current_topic = "event/" + dep 
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
    ViewModel.Events.push(stts);
    // roomid = stts["room"];

}
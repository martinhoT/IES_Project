// $.ajax({
//     type: "GET",
//     url: "api/status",
//     data: "data",
//     dataType: "dataType",
//     success: function (response) {
        
//     }
// });

// Create a client instance
client = new Paho.MQTT.Client("localhost", 1884, "", "clientId");

// set callback handlers
client.onConnectionLost = onConnectionLost;
client.onMessageArrived = onMessageArrived;

// connect the client
client.connect({onSuccess:onConnect});



// called when the client connects
function onConnect() {
    // Once a connection has been made, make a subscription and send a message
    console.log("onConnect");
    client.subscribe("js/test");
    message = new Paho.MQTT.Message(JSON.stringify({
        "4.7.8": 0,
        "4.0.10": 0
    }));
    message.destinationName = "js/test";
    client.send(message);
}

// called when the client loses its connection
function onConnectionLost(responseObject) {
    if (responseObject.errorCode !== 0) {
        console.log("onConnectionLost:" + responseObject.errorMessage);
    }
}

// called when a message arrives
function onMessageArrived(message) {
    console.log("onMessageArrived:" + message.payloadString);
    var stts = JSON.parse(message.payloadString);
    for (const roomid in stts) {
        if (Object.hasOwnProperty.call(stts, roomid)) {
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
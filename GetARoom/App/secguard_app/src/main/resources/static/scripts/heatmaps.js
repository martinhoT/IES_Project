function ViewModel() {
    var self = this;

    self.department = ko.observable("0");
    self.floors = ko.observableArray([
        {
            "floor": "X",
            "img": "#",
            "styles": [
                {
                    "room": "",
                    "top": "0px",
                    "left": "0px",
                    "width": "0px",
                    "height": "0px"
                }
            ]
        }
    ]);

    // Checks whether the children of the floors observable array have already been loaded and bound in the HTML.
    // This way, the MQTT client can safely update the HTML elements, checking first if they have been bound.
    self.floorsBound = false;
    self.savedStyles = [];
    self.flagFloorsBound = function(parent) {
        console.log("Floors loaded, data can be updated.");
        self.floorsBound = true;
        self.updateFloors();
    }
    self.updateFloors = function() {
        for (style of self.savedStyles) {
            percentage = document.getElementById("pct:" + style.roomid)
            color = document.getElementById("clr:" + style.roomid)
            // if (percentage != null)
                percentage.innerText = style.pct;
            // if (color != null)
                color.className = style.clr;
        }
        self.savedStyles = []
    }
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

current_topic = null;

// Used as variables to hold temporary data obtained from AJAX calls.
// After all data from the different calls has been obtained, populate this
// data into the Knockout observable (in this case, it's 'floors').
backgroundPopulator = [];
backgroundPopulatorInserted = 0;

// When a new floor is chosen, subscribe to its respective topic
// Also unsubscribe from the previously subscribed topics
$("#department_selected").change(function(e) {
    let dep_number = $("#department_selected").val()
    viewModel.department(dep_number)
    viewModel.floorsBound = false;

    $.getJSON("http://localhost:84/api/department", {"dep": dep_number},
        function (data, textStatus, jqXHR) {
            n_floors = 0;
            viewModel.floors([]);
            backgroundPopulator = [];
            backgroundPopulatorInserted = 0;

            for (let department of data)
                if (department.dep === dep_number) {
                    n_floors = department.floors;
                    break;
                }
            // viewModel.floors( Array.from(Array(n_floors), (_,i) => 1 + i) );
            for (let i = 0; i < n_floors; i++) {
                $.getJSON("http://localhost:84/api/roomStyles", {"dep": dep_number, "floor": i+1},
                    function (data, textStatus, jqXHR) {
                        // viewModel.rooms( Array.from(rooms, (v,_) => v.room.split(".")[2]) );
                        backgroundPopulator[i] = {
                            "floor": i+1,
                            "img": 'images/dep' + viewModel.department() + '_' + (i+1) + '.jpg',
                            "styles": data
                        };
                        backgroundPopulatorInserted++;
                        if (backgroundPopulatorInserted === n_floors)
                            viewModel.floors(backgroundPopulator);
                    }
                );
            }
        }
    );

    if (connected) {
        if (current_topic != null) {
            client.unsubscribe(current_topic);
            console.log("Unsubscribed from topic " + current_topic);
        }
        current_topic = "status/" + dep_number + "/#";
        client.subscribe(current_topic);
        console.log("Subscribed to new topic " + current_topic);
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

    roomid = stts["room"];
    occupacy = parseInt(Number(stts["occupacy"])*100);
    
    colorClass = "box"
    if (occupacy < 20) {
        colorClass = "box hm-empty";
    }
    else if (occupacy < 40) {
        colorClass = "box hm-almost-empty";
    }
    else if (occupacy < 60) {
        colorClass = "box hm-mid";
    }
    else if (occupacy < 80) {
        colorClass = "box hm-almost-full";
    }
    else {
        colorClass = "box hm-full";
    }
    
    viewModel.savedStyles.push({
        roomid: roomid,
        pct: occupacy + "%",
        clr: colorClass
    });

    if (viewModel.floorsBound) {
        viewModel.updateFloors();
    }
}
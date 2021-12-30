function ViewModel() {
    var self = this;
    self.department = ko.observable("0");
    self.floors = ko.observableArray([]);
    self.rooms = ko.observableArray([]);
    
    self.full_room_name = function(room) {
        return "/room/" + room
    };

    self.heatmap_img_src = function(floor) {
        return '/images/dep' + self.department() + '_' + floor + '.jpg';
    }

    self.actual_floor_number = function(index) {
        return Number(index) + 1;
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
                            "img": '/images/dep' + viewModel.department() + '_' + (i+1) + '.jpg',
                            "styles": data
                        };
                        backgroundPopulatorInserted++;
                        if (backgroundPopulatorInserted === n_floors);
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
    occupacy = Number(stts["occupacy"])*100;
    
    document.getElementById("pct:" + roomid).innerText = occupacy + "%";
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
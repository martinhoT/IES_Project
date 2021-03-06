var heatmapsScriptVars = {
    viewModel: undefined
}

$(document).ready(function() {
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
                if (percentage != null)
                    percentage.innerText = style.pct;
                if (color != null) {
                    color.style.backgroundColor = style.clr;
                }
            }
            self.savedStyles = []
        }
    };
    heatmapsScriptVars.viewModel = new ViewModel();
    ko.applyBindings( heatmapsScriptVars.viewModel, document.getElementById("ko-body") );
    
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
    
    // Obtain room style data (position and size of the clickable boxes on the floor maps)
    // Switch from a topic to another (unsubscribe to old, subscribe to new)
    updateDepFloors = function(e) {
        let dep_number = $("#department_selected").val()
        heatmapsScriptVars.viewModel.department(dep_number)
        heatmapsScriptVars.viewModel.floorsBound = false;
    
        $.getJSON("http://" + location.hostname + ":84/api/department", {"dep": dep_number},
            function (data, textStatus, jqXHR) {
                n_floors = data[0].floors;
                heatmapsScriptVars.viewModel.floors([]);
                backgroundPopulator = [];
                backgroundPopulatorInserted = 0;
    
                for (let i = 0; i < n_floors; i++) {
                    $.getJSON("http://" + location.hostname + ":84/api/room_styles", {"dep": dep_number, "floor": i+1},
                        function (data, textStatus, jqXHR) {
                            backgroundPopulator[i] = {
                                "floor": i+1,
                                "img": 'images/dep' + heatmapsScriptVars.viewModel.department() + '_' + (i+1) + '.jpg',
                                "styles": data
                            };
                            backgroundPopulatorInserted++;
                            if (backgroundPopulatorInserted === n_floors)
                                heatmapsScriptVars.viewModel.floors(backgroundPopulator);
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
    }
    
    // When a new floor is chosen, subscribe to its respective topic
    // Also unsubscribe from the previously subscribed topics
    $("#department_selected").change( updateDepFloors );
    
    function onConnect() {
        console.log("Successfully connected to the broker.");
        
        connected = true;
        updateDepFloors();
    }
    
    function onConnectionLost(responseObject) {
        if (responseObject.errorCode !== 0)
            console.log("Connection lost:" + responseObject.errorMessage);
    }
    
    function onMessageArrived(message) {
        msg = message.payloadString;
        console.log("Received message:" + msg);
        var stts = JSON.parse(msg);
    
        roomid = stts["room"];
        occupancy = stts["occupancy"]
        occupancyPercentage = parseInt(Number(occupancy)*100);
    
        hexRange = "0123456789abcdef";
    
        hexIndex = parseInt(occupancy*16)
        hexIndex = hexIndex > 15 ? 15 : hexIndex;
    
        colorStyle = "#" + hexRange[hexIndex] + hexRange[hexIndex] + hexRange[15-hexIndex] + hexRange[15-hexIndex] + "00";
        
        heatmapsScriptVars.viewModel.savedStyles.push({
            roomid: roomid,
            pct: occupancyPercentage + "%",
            clr: colorStyle
        });
    
        if (heatmapsScriptVars.viewModel.floorsBound) {
            heatmapsScriptVars.viewModel.updateFloors();
        }
    }
})
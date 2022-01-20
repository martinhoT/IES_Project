var logsScriptVars = {
    viewModel: undefined,
    mqttClient: undefined,
    mqttClientConnected: false,
    mqttClientTopic: "event/#"
};

$(document).ready(function() {

    function ViewModel() {
        var self = this;

        self.pages = ko.observableArray([]);
        self.room = ko.observable("");
        self.events = ko.observableArray([]);
        self.newEvents = ko.observableArray([]);

        self.pageCapacity = ko.observable(20);
        self.pageSelected = ko.observable(0);

        self.choosePage = function(page) {
            self.pageSelected(page);

            // Update the number of pages in case new messages created new pages
            $.getJSON("http://localhost:84/api/event/pages", {room: self.room(), pageNumber: self.pageSelected(), pageCapacity: self.pageCapacity()},
            function(data, testStatus, jqXHR) {
                self.pages( [...Array(data).keys()] );
            });

            self.updateEvents();
        }

        self.updateEvents = function() {
            
            $.getJSON("http://localhost:84/api/event", {room: self.room(), pageNumber: self.pageSelected(), pageCapacity: self.pageCapacity()},
            function(data, testStatus, jqXHR) {
                self.events(data);
            });

            self.newEvents.removeAll();

            // If it's connected and there was a change in the room filter
            if (logsScriptVars.mqttClientConnected && ((self.room().length === 0 && logsScriptVars.mqttClientTopic !== "event/#") || (self.room() !== logsScriptVars.mqttClientTopic))) {
                logsScriptVars.mqttClient.unsubscribe(logsScriptVars.mqttClientTopic);
                // hmm, no input validation...
                if (self.room().length === 0)
                    logsScriptVars.mqttClientTopic = "event/#";
                else
                    logsScriptVars.mqttClientTopic = "event/" + self.room().replaceAll(".", "/");
                logsScriptVars.mqttClient.subscribe(logsScriptVars.mqttClientTopic);
            }
        }

        self.resetDefault = function() {
            $("#room").val("");
            self.room("");
            $("#page-capacity").val(20);
            self.pageCapacity(20);
        }

        self.nextPage = function() {
            if (self.pageSelected() < self.pages().length-1) {
                self.choosePage(self.pageSelected() + 1);
            }
        }

        self.prevPage = function() {
            if (self.pageSelected() > 0) {
                self.choosePage(self.pageSelected() - 1);
            }
        }

        // Wrapper function for choosePage, since the page number on text inputs is +1 of the actual value
        self.choosePageInput = function() {
            self.choosePage( $("#page-number-input").val() - 1);
        }

    }
    logsScriptVars.viewModel = new ViewModel();
    ko.applyBindings(logsScriptVars.viewModel, document.getElementById("ko-body"));

    logsScriptVars.viewModel.choosePage(0);

    logsScriptVars.mqttClient = new Paho.MQTT.Client(location.hostname, 1884, "", "");

    // Set callback handlers
    logsScriptVars.mqttClient.onConnectionLost = onConnectionLost;
    logsScriptVars.mqttClient.onMessageArrived = onMessageArrived;

    logsScriptVars.mqttClient.connect({onSuccess:onConnect});


    function onConnect() {
        console.log("Successfully connected to the broker.");
        logsScriptVars.mqttClient.subscribe(logsScriptVars.mqttClientTopic);
        logsScriptVars.mqttClientConnected = true;
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
        
        // Since the messages are retained, this message may have already been received, due to subscriptions and unsubscriptions
        if (!message.duplicate)
            logsScriptVars.viewModel.newEvents.push(evnt);
    }
})

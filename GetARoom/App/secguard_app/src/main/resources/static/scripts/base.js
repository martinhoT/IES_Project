function ViewModel() {
    var self = this;

    self.notifications = ko.observableArray(),
    self.notifications_popups = ko.observableArray(),
    self.commitSeen = function(e) {
        // Obtain first the array of notifications to be deleted, so that only one POST request is done.
        to_be_deleted = []
        $("#unseen-notification-list > input:checked").each(function(e) {
            to_be_deleted.push( self.notifications().splice( e.val() )[0] );
        });

        $.post("http://" + location.hostname + ":84/api/alerts/mark_read",
            to_be_deleted,
            function (data, textStatus, jqXHR) {},
            "json"
        );
    }
}
var viewModel = new ViewModel();
ko.applyBindings( viewModel );



// Max size of the blacklist notification popups
const NOTIFICATIONS_POPUPS_MAX_SIZE = 3;



// MQTT client that will listen for blacklist alerts
client = new Paho.MQTT.Client(location.hostname, 1884, "", "");

// Set callback handlers
client.onConnectionLost = onConnectionLost;
client.onMessageArrived = onMessageArrived;

client.connect({onSuccess:onConnect});



function onConnect() {
    console.log("Event client successfully connected to the broker.");
    
    // Listen for all future events.
    // This is done first or else, between the time when the old notifications are obtained from the database
    // and the subscription to the broker is done, alerts may be published to the broker that aren't seen.
    // Since these are important events to keep track of, it's necessary to enforce some kind of order like this,
    // even if it will lead to duplicates.
    client.subscribe("blacklist_notification");
    
    // Obtain unread notifications from the database
    $.getJSON("http://" + location.hostname + ":84/api/unseen_notifications",
    function (data, textStatus, jqXHR) {
        for (let notification of data) {
            // The subscription to the broker is done first, so it may be possible that
            // there is an overlap and some of these notifications have already been obtained,
            // so we check that we don't add them twice.
            let present = false;
            for (let present_notification of viewModel.notifications())
                if (present_notification.id === notification.id) {
                    present = true;
                    break;
                }
            if (!present) 
                viewModel.notifications().push( notification );
        }
    });
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

    viewModel.notifications().push(evnt);
    
    viewModel.notifications_popups().push(evnt);
    if (viewModel.notifications_popups().length > 3)
        viewModel.notifications_popups().shift();
}
var baseScriptVars = {
    viewModel: undefined
}

$(document).ready(function() {
    
    function ViewModel() {
        var self = this;
    
        self.notifications = ko.observableArray([]),
        self.notifications_popups = ko.observableArray([]),

        self.unseen_count = ko.observable(0);

        self.commitSeen = function(notification_popup) {
            let prev_length = self.notifications().length;
            
            // thanks MongoDB
            self.notifications_popups.remove(function(item) {
                return item.user === notification_popup.user &&
                    item.email === notification_popup.email &&
                    item.room === notification_popup.room &&
                    new Date(item.time).getTime() === new Date(notification_popup.time).getTime();
            });
        }

        /**
         * NOTIFICATIONS
         */
        self.seeUnseen = ko.observable(true);

        self.commitSeenList = function(e) {
            // Obtain first the array of notifications to be deleted, so that only one POST request is done.
            to_be_seen = []
            $("#notification-table > tr > td > input:checked").each(function(idx) {
                // Pop the notification
                notification = baseScriptVars.viewModel.notifications.splice(this.value,1)[0];
                
                to_be_seen.push( notification );
                baseScriptVars.viewModel.unseen_count( baseScriptVars.viewModel.unseen_count() - 1);
                
                // Put the notification back on, with 'seen' set to 'true'. This forces KnockoutJS to update the list itself.
                notification.seen = true;
                baseScriptVars.viewModel.notifications.push( notification );
            });
    
            if (to_be_seen.length > 0) {
                $.ajax({
                    type: "POST",
                    url: "http://" + location.hostname + ":84/api/alerts/mark_seen",
                    data: JSON.stringify(to_be_seen),
                    function (data, textStatus, jqXHR) {},
                    contentType: "application/json",
                    dataType: "json"
                });
            }
        };

        self.toggleCheckmarks = function(e) {
            $("#notification-table > tr > td > input").each(function(idx) {
                this.checked = !this.checked;
            });
        }
    }

    baseScriptVars.viewModel = new ViewModel();
    ko.applyBindings( baseScriptVars.viewModel, document.getElementById("notification-consumers") );
    


    // Max size of the blacklist notification popups
    const NOTIFICATIONS_POPUPS_MAX_SIZE = 3;
    


    // MQTT client that will listen for blacklist alerts
    var client = new Paho.MQTT.Client(location.hostname, 1884, "", "");
    
    // Set callback handlers
    client.onConnectionLost = onConnectionLost;
    client.onMessageArrived = onMessageArrived;
    
    client.connect({onSuccess: onConnect});



    function onConnect() {
        console.log("Event client successfully connected to the broker.");
        
        // Listen for all future events.
        // This is done before obtaining unseen notifications from the fetcher. Otherwise, between the time when the
        // old notifications are obtained from the database and the subscription to the broker is done, alerts may be
        // published to the broker that aren't seen.
        // Since these are important events to keep track of, it's necessary to enforce some kind of order like this,
        // even if it will lead to duplicates.
        client.subscribe("blacklist_notification");
        
        // Obtain unread notifications from the database
        $.getJSON("http://" + location.hostname + ":84/api/alerts/unseen",
        function (data, textStatus, jqXHR) {
            for (let notification of data) {
                // The subscription to the broker is done first, so it may be possible that
                // there is an overlap and some of these notifications have already been obtained,
                // so we check that we don't add them twice.
                let present = false;
                for (let present_notification of baseScriptVars.viewModel.notifications())
                    if (present_notification.id === notification.id) {
                        present = true;
                        break;
                    }
                if (!present) {
                    baseScriptVars.viewModel.notifications.push( notification );
                    baseScriptVars.viewModel.unseen_count( baseScriptVars.viewModel.unseen_count() + 1 );
                }
            }
        });
    }

    function onConnectionLost(responseObject) {
        if (responseObject.errorCode !== 0)
            console.log("Connection lost:" + responseObject.errorMessage);
    }

    function onMessageArrived(message) {
        msg = message.payloadString;
        console.log("Received notification:" + msg);
        var evnt = JSON.parse(msg);
    
        baseScriptVars.viewModel.notifications.push( evnt );
        baseScriptVars.viewModel.unseen_count( baseScriptVars.viewModel.unseen_count() + 1 );
        
        baseScriptVars.viewModel.notifications_popups.push(evnt);
        if (baseScriptVars.viewModel.notifications_popups().length > NOTIFICATIONS_POPUPS_MAX_SIZE)
            baseScriptVars.viewModel.notifications_popups.shift();
    }

    // TODO: use pagination
    if (document.getElementById("receive-all-notifications") !== null) {
        $.getJSON("http://" + location.hostname + ":84/api/alerts/seen",
        function (data, textStatus, jqXHR) {
            for (let notification of data)
                baseScriptVars.viewModel.notifications.push(notification);
        });
    }

});

var logsScriptVars = {
    viewModel: undefined
};

$(document).ready(function() {

    function ViewModel() {
        var self = this;

        self.pages = ko.observableArray([]);
        self.room = ko.observable("");
        self.events = ko.observableArray([]);

        self.pageCapacity = ko.observable(20);
        self.pageSelected = ko.observable(0);

        self.choosePage = function(page) {
            self.pageSelected(page);
            self.updateEvents();
        }

        self.updateEvents = function() {
            
            $.getJSON("http://localhost:84/api/event", {room: self.room(), pageNumber: self.pageSelected(), pageCapacity: self.pageCapacity()},
            function(data, testStatus, jqXHR) {
                self.events(data);
            });
        }

        // Used in case a new page count has to be used (the filters)
        self.updateAll = function() {
            $.getJSON("http://localhost:84/api/event/pages", {pageNumber: self.pageSelected(), pageCapacity: self.pageCapacity()},
            function(data, testStatus, jqXHR) {
                self.pages( [...Array(data).keys()] );

                self.choosePage(0);
            });
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

    }
    logsScriptVars.viewModel = new ViewModel();
    ko.applyBindings(logsScriptVars.viewModel, document.getElementById("ko-body"));

    logsScriptVars.viewModel.updateAll();
})
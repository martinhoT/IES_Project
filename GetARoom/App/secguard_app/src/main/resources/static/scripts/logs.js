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
            
            $.getJSON("http://localhost:84/api/event", {pageNumber: self.pageSelected(), pageCapacity: self.pageCapacity()},
            function(data, testStatus, jqXHR) {
                self.events(data);
            });
        }

        self.resetDefault = function() {
            $("#room").val("");
            $("#page-capacity").val(20);
        }

    }
    logsScriptVars.viewModel = new ViewModel();
    ko.applyBindings(logsScriptVars.viewModel, document.getElementById("ko-body"));

    $.getJSON("http://localhost:84/api/event/pages",
    function(data, testStatus, jqXHR) {
        logsScriptVars.viewModel.pages( Array(data).keys() );

        logsScriptVars.viewModel.updateEvents();
    });

})
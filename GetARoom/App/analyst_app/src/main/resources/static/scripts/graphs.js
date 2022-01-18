google.charts.load('current', {'packages':['line']});
google.charts.setOnLoadCallback(drawChart);

var Events;
var floorData = {};
var options = {};

function ViewModel(){
    var self = this;
    
    self.floors = ko.observableArray([]);
    self.departments = ko.observableArray([]);
    self.selectedFloor = ko.observable();

    self.flagFloorsBound = function(parent) {
        console.log("Floors loaded, data can be updated.");
        self.floorsBound = true;
        self.selectedFloor(self.floors()[0])

        options = {
            title: 'Floor '+  self.floors()[0] +' room occupancy over time',
            legend: {
                position: 'left',
                textStyle:{
                    fontSize: 16
                }
            },
            vAxis: {
                format:'#,###%',
                viewWindow:{
                    max: 1,
                    min: 0
                }}
        };

        var chart = new google.charts.Line(document.getElementById('curve_chart_'+ self.floors()[0]));
        chart.draw(floorData[self.floors()[0]], google.charts.Line.convertOptions(options));    
    }

    self.sortedFloors = ko.pureComputed(function () {
        return self.floors.sorted(function (left, right) {
            leftfloor = left.split("-")[1]
            rightfloor = right.split("-")[1]
            left = left.split("-")[0]
            right = right.split("-")[0]
            return left === right ? (
                    leftfloor === rightfloor ? 0
                    : leftfloor < rightfloor ? -1
                    : 1
                )
                : left > right ? -1
                : 1;
        });
    });

}
var viewModel = new ViewModel();
ko.applyBindings(viewModel);

$.getJSON("http://localhost:84/api/status   ",
function (data, textStatus, jqXHR) {
    Events = data;
});

$("#selected_floor").change(function() {
    console.log( $("#selected_floor").val() );
    viewModel.selectedFloor( $("#selected_floor").val() )
    // TODO
    options = {
        title: 'Floor '+  $("#selected_floor").val() +' room occupancy over time',
        legend: {
            position: 'left',
            textStyle:{
                fontSize: 16
            }
        },
        vAxis: {
            format:'#,###%',
            viewWindow:{
                max: 1,
                min: 0
            }}
    };

    var chart = new google.charts.Line(document.getElementById('curve_chart_'+$("#selected_floor").val()));
    chart.draw(floorData[$("#selected_floor").val()], google.charts.Line.convertOptions(options));
});

function drawChart() {
    viewModel.floors([]);
    var roomsByFloor = {};
    $.getJSON("http://localhost:84/api/status",
        function (data, textStatus, jqXHR) {
            for(let event of data){
                var floor = event['room'].split(".").splice(0,2).join("-");
            
                if(!(floor in roomsByFloor)){
                    roomsByFloor[floor] = {};
                }
                else if(Object.keys(roomsByFloor[floor]).length >= 15 && !(event['room'] in roomsByFloor[floor])){
                    floor = floor + "_2";
                    if(!(floor in roomsByFloor)){
                        roomsByFloor[floor] = {};
                    }
                }
            
                if(!(floor in floorData)){
                    viewModel.floors.push(floor);
                    floorData[floor] = new google.visualization.DataTable();
                    floorData[floor].addColumn('date', 'Time');
                }
            
                if(!(event['room'] in roomsByFloor[floor])){
                    roomsByFloor[floor][event['room']] = event['occupancy'];
                    floorData[floor].addColumn('number', event['room']);
                }
            
                var row = [new Date(event['time'])];
            
                for(var key in roomsByFloor[floor]){
                    if(event['room'] === key){
                        row.push(event['occupancy']);
                        roomsByFloor[floor][event['room']] = event['occupancy'];
                    }
                    else{
                        row.push(roomsByFloor[floor][key]);
                    }
                }
            
                floorData[floor].addRow(row);
            }
        });
}
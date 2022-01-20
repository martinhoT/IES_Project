google.charts.load('current', {'packages':['line']});
google.charts.setOnLoadCallback(drawChart);

var floorData = {};
var data_per_day = {};

function ViewModel(){
    var self = this;
    
    self.floors = ko.observableArray([]);
    self.days = ko.observableArray([]);
    self.departments = ko.observableArray([]);
    self.selectedFloor = ko.observable();
    self.selectedDay = ko.observable();

    floorData = data_per_day[self.days()[0]];

    self.flagFloorsBound = function(parent) {
        console.log("Floors loaded, data can be updated.");
        self.floorsBound = true;
        self.selectedDay(self.days()[0]);
        self.selectedFloor(self.sortedFloors()[0]);
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

$("#selected_day").change(function() {
    console.log( $("#selected_day").val() );
    viewModel.selectedDay( $("#selected_day").val() )
    floorData = data_per_day[$("#selected_day").val()]

    viewModel.floors(Object.keys(floorData));
});

$("#selected_floor").change(function() {
    console.log( $("#selected_floor").val() );
    viewModel.selectedFloor( $("#selected_floor").val() )
    // TODO
    var options = {
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
    google.visualization.events.addListener(chart, 'error', function (err) {
        console.log(err.id, err.message);
        google.visualization.errors.removeError(err.id);
        google.visualization.errors.removeAll(document.getElementById('curve_chart_'+$("#selected_floor").val()));
    });
    chart.draw(floorData[$("#selected_floor").val()], google.charts.Line.convertOptions(options));
});

function drawChart() {
    viewModel.floors([]);
    viewModel.days([]);
    var roomsByFloor = {};
    $.getJSON("http://localhost:84/api/status",
        function (data, textStatus, jqXHR) {
            for(let event of data){
                var floor = event['room'].split(".").splice(0,2).join("-");

                var date = new Date(event['time'])
                var day = date.getDate().toString() +"/"+(date.getMonth()+1).toString() +"/"+date.getFullYear().toString();

                if (!(Object.keys(data_per_day).includes(day))){
                    data_per_day[day] = {};
                    if (Object.keys(data_per_day).length >= 2){
                        var prev_day = Object.keys(data_per_day)[Object.keys(data_per_day).indexOf(day)-1]
                        data_per_day[prev_day] = floorData;
                    }
                    floorData = {};
                    roomsByFloor = {};
                }

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
            data_per_day[Object.keys(data_per_day)[Object.keys(data_per_day).length - 1]] = floorData;
            viewModel.days(Object.keys(data_per_day));
            viewModel.floors(Object.keys(data_per_day[viewModel.days()[0]]));
            floorData = data_per_day[viewModel.days()[0]];

            var options = {
                title: 'Floor '+  viewModel.sortedFloors()[0] +' room occupancy over time',
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
            
            var chart = new google.charts.Line(document.getElementById('curve_chart_'+ viewModel.sortedFloors()[0]));
            google.visualization.events.addListener(chart, 'error', function (err) {
                console.log(err.id, err.message);
                google.visualization.errors.removeError(err.id);
            });
            chart.draw(floorData[viewModel.sortedFloors()[0]], google.charts.Line.convertOptions(options));
        });
}
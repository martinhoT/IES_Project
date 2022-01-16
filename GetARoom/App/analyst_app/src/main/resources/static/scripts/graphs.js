google.charts.load('current', {'packages':['line']});
google.charts.setOnLoadCallback(drawChart);

var Events;
var floorData = {};
var options = {
    title: 'Room occupacy by time',
    curveType: 'function',
    legend: {positon : 'bottom'},
    vAxis: { format:'#,###%'}
};

function ViewModel(){
    var self = this;
    
    self.floors = ko.observableArray([]);
    self.departments = ko.observableArray([]);
    self.selectedFloor = ko.observable();
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
                    roomsByFloor[floor][event['room']] = event['occupacy'];
                    floorData[floor].addColumn('number', event['room']);
                }
            
                var row = [new Date(event['time'])];
            
                for(var key in roomsByFloor[floor]){
                    if(event['room'] === key){
                        row.push(event['occupacy']);
                        roomsByFloor[floor][event['room']] = event['occupacy'];
                    }
                    else{
                        row.push(roomsByFloor[floor][key]);
                    }
                }
            
                floorData[floor].addRow(row);
            }
            
    });
}
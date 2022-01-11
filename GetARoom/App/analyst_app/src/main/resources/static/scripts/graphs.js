google.charts.load('current', {'packages':['line']});
google.charts.setOnLoadCallback(drawChart);

var Events;

function ViewModel(){
    var self = this;
    
    self.floors = ko.observableArray([]);
}
var viewModel = new ViewModel();
ko.applyBindings(viewModel);

$.getJSON("http://localhost:84/api/status_history",
function (data, textStatus, jqXHR) {
    Events = data;
});

function drawChart() {
    viewModel.floors([]);
    var floorData = {};
    var roomsByFloor = {};
    for(let event of Events){
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


    var options = {
        title: 'Room occupacy by time',
        curveType: 'function',
        legend: {positon : 'bottom'},
        vAxis: { format:'#,###%'}
    };
    
    for(var floor in floorData){
        var chart = new google.charts.Line(document.getElementById('curve_chart_'+floor));
        chart.draw(floorData[floor], google.charts.Line.convertOptions(options));
    }
}
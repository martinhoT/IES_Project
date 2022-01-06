/*<![CDATA[*/
google.charts.load('current', {'packages':['corechart']});
    var events = /*[[${status_events}]]*/ "";
    console.log(events);
    function drawChart() {
        var data = google.visualization.arrayToDataTable([events]);
        
        var options = {
            title: 'Company Performance',
            curveType: 'function',
            legend: { position: 'bottom' }
        };
        
        var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));
        
        chart.draw(data, options);
    }

/*]]>*/
/*
Copyright 2016 the project authors as listed in the AUTHORS file.
All rights reserved. Use of this source code is governed by the
license that can be found in the LICENSE file.
*/

/*Global Data*/
var heapChart, respChart, thruChart, reclChart, reqsChart; 
var intervalCall;
var delay=1000;
var nThreads = 1;
var t0;
var threads = [];
var requestQueue = [];
var isOn = false;
var heapData=[];
var reclData=[];
var reqsData=[];
var actionPieChart;
var visPoints = 100;

/*Startup Callbacks*/

//Setup graph dimensions
$(function(){
	setupGraphDim();
});


//Get initial timestamp
$(function() {
	t0 = Date.now();
});

//On, Off, Init buttons
$(function() {
    $( "#onButton" ).click(function(){
      if(!isOn){
    	  start();
    	  isOn = true;
    	  $( "#onButton" ).html("Stop");
      } else {
    	  stop();
    	  isOn = false;
    	  $( "#onButton" ).html("Start");
      }
    });
    
    $( "#initButton" ).click(function(){
    	if(isOn){
    		$( "#onButton" ).click();
    	}
    	$( "#initButton" ).html("Initializing...");
    	$.get({
    		url: "/InitGraph",
    		success: function(val){
    			$( "#initButton" ).html("Initialize Again");
			},
			error: function(xhr, ajaxOptions, thrownError) {
				$( "#initButton" ).html("Initialization Failed");
			}
    	});
    });
    
  });

//Piechart for action ratios
$(function() {
	var data = [
        {
            value: 0,
            color:"#F7464A",
            highlight: "#FF5A5E",
            label: "Read"
        },
        {
            value: 0,
            color: "#46BFBD",
            highlight: "#5AD3D1",
            label: "Write"
        },
        {
            value: 25,
            color: "#FDB45C",
            highlight: "#FFC870",
            label: "Reference Change"
        },
        {
            value: 25,
            color: "#0000AA",
            highlight: "#0000BB",
            label: "Allocate"
        },
        {
            value: 25,
            color: "#FD005C",
            highlight: "#FF0099",
            label: "Add Root"
        },
        {
            value: 25,
            color: "#00B45C",
            highlight: "#00C870",
            label: "Remove Root"
        }
    ]
	actionPieChart = new Chart($("#actionRatios").get(0).getContext("2d")).Pie(data);
});


//Initialize sliders
$(function() {
	Chart.defaults.global.scaleFontSize=14;
	
    $( "#canvasDiv" ).tabs();
    $( "#controlDiv" ).tabs();
    
    $( "#requestWaitSlider" ).slider({
    	min: 0,
    	max: 2000,
    	range: "min",
    	value: delay,
    	slide: function(ev, ui){
    		delay = ui.value;
    		
    		threads.forEach(function(thread){
    			clearTimeout(thread);
    		});
    		for(i=0;i<nThreads;i++){
            	threads[i] = setInterval(performCall, ui.value);
            }
    		
    		$( "#requestWaitLabel" ).val( ui.value );
    	}
    });
    $( "#requestWaitLabel" ).val($( "#requestWaitSlider" ).slider( "value" ) );
    
    $( "#requestThreadsSlider" ).slider({
    	min: 1,
    	max: 16,
    	range: "min",
    	value: 1,
    	slide: function(ev, ui){
    		$( "#requestThreadsLabel" ).val( ui.value );
    		nThreads = ui.value;
    		            		
    		threads.forEach(function(thread){
    			clearTimeout(thread);
    		});
    		for(i=0;i<nThreads;i++){
            	threads[i] = setInterval(performCall, delay);
            }
    	}
    });
    $( "#requestThreadsLabel" ).val($( "#requestThreadsSlider" ).slider( "value" ) );
    
    
    $( "#pointsSlider" ).slider({
    	min: 10,
    	max: 1000,
    	range: "min",
    	value: visPoints,
    	slide: function(ev, ui){
    		$( "#pointsLabel" ).val( ui.value );
    		visPoints = ui.value;

    	}
    });
    $( "#pointsLabel" ).val($( "#pointsSlider" ).slider( "value" ) );
    
    
    $( "#eq > span > div" ).each(function() {
        var value = parseInt( $( this ).text(), 10 );
        $( this ).empty().slider({
          value: value,
          min:0,
          max:100,
          range: "min",
          animate: true,
          orientation: "vertical",
          slide: function(ev, ui){
        	  $( this ).prev().html(ui.value);
        	  actionPieChart.segments[$(this).attr("id")].value=ui.value;
        	  actionPieChart.update();
        	  
        	  //Send request to change settings
        	  $.get("\ChangeSettings?" + $(this).attr("param") + "=" + ui.value);
          }
        });
      });
});

$(function(){
    $( "#actionsNumberSlider" ).slider({
    	min: 0,
    	max: 10000,
    	value: 1000,
    	range: "min",
    	slide: function(ev, ui){
    		$( "#actionsNumberLabel" ).html( ui.value );
    		
    		//Send request to change settings
      	  	$.get("\ChangeSettings?ACTIONS_PER_REQUEST=" + ui.value);
    	}
    });
    $( "#actionsNumberLabel" ).html($( "#actionsNumberSlider" ).slider( "value" ) );
    
    $( "#localNumberSlider" ).slider({
    	min: 0,
    	max: 100,
    	value: 50,
    	range: "min",
    	slide: function(ev, ui){
    		$( "#localNumberLabel" ).html( ui.value );
    		
    		//Send request to change settings
      	  	$.get("\ChangeSettings?LOCAL_ACTION_RATIO=" + (ui.value/100.0));
    	}
    });
    $( "#localNumberLabel" ).html($( "#localNumberSlider" ).slider( "value" ) );
});

$(function(){
	$( "#payloadNumberSlider" ).slider({
    	min: 0,
    	max: 8192,
    	values: [8, 24, 4196],
    	slide: function(ev, ui){
    		var values = $( "#payloadNumberSlider" ).slider( "values" );
   		
    		$( "#payloadMinNumberLabel" ).html( values[0] );
    		$( "#payloadMedNumberLabel" ).html( values[1] );
    		$( "#payloadMaxNumberLabel" ).html( values[2] );
    		
    		//Send request to change settings
      	  	$.get("\ChangeSettings?MIN_PAYLOAD_SIZE=" + values[0]+"&MED_PAYLOAD_SIZE="+values[1]+"&MAX_PAYLOAD_SIZE="+values[2]);
    	}
    });
	var values = $( "#payloadNumberSlider" ).slider( "values" );
	$( "#payloadMinNumberLabel" ).html( values[0] );
	$( "#payloadMedNumberLabel" ).html( values[1] );
	$( "#payloadMaxNumberLabel" ).html( values[2] );
});

$(function(){
	$( "#refsNumberSlider" ).slider({
    	min: 0,
    	max: 8192,
    	values: [8, 24, 4196],
    	slide: function(ev, ui){
    		var values = $( "#refsNumberSlider" ).slider( "values" );
   		
    		$( "#refsMinNumberLabel" ).html( values[0] );
    		$( "#refsMedNumberLabel" ).html( values[1] );
    		$( "#refsMaxNumberLabel" ).html( values[2] );
    		
    		//Send request to change settings
    		$.get("\ChangeSettings?MIN_REFS=" + values[0]+"&MED_REFS="+values[1]+"&MAX_REFS="+values[2]);
    	}
    });
	var values = $( "#refsNumberSlider" ).slider( "values" );
	$( "#refsMinNumberLabel" ).html( values[0] );
	$( "#refsMedNumberLabel" ).html( values[1] );
	$( "#refsMaxNumberLabel" ).html( values[2] );
});
	
//Helping functions
function start(){
	requestQueue=[];
    for(i=0;i<nThreads;i++){
    	threads[i] = setInterval(performCall, delay);
    }
    
    drawCall = setInterval(draw, 1000);
}

function stop(){
	for(i=0;i<nThreads;i++){
		clearInterval(threads[i]);
	}
	clearInterval(drawCall);
}

function performCall(){
	
	requestQueue.push(Date.now());
	
	$.get({
		url: "/GraphAction", 
		success: function(val){
			val = val.split("\n")[1];
			
			t = Math.floor((Date.now() - t0)/1000);
			dt = Date.now() - requestQueue.shift();
		
			console.log(t, val);
			updateHeapValues(t, dt/1000, Number(val)/(1<<20));
		},
		error: function(xhr, ajaxOptions, thrownError) {
			requestQueue.pop();
		}
	});
	
	//updateHeapValues(Math.floor((Date.now() - t0)/1000), (Date.now()- requestQueue.shift())/1000, Math.random()*10+10);
}

var prevVal = -1;
var prevReqs = 0;
var reqs=0;
var gcs=0;
function updateHeapValues(t, dt, val){
	reqs++;
	if(!heapData[t]){
		heapData[t]=[];
		heapData[t].count=1;
		heapData[t].avg=val;
		heapData[t].resp=dt;

	} else {
		heapData[t].count++;
		heapData[t].avg += (val-heapData[t].avg)/heapData[t].count;
		heapData[t].resp += (dt-heapData[t].resp)/heapData[t].count;
	}
	
	if(prevVal!=-1){
		if(val<prevVal){
			gcs++;
			reclData[gcs] = prevVal - val;
			reqsData[gcs] = reqs - prevReqs;
			prevReqs = reqs;			
			
			
			if(!reclChart){
				//Recl
				var ctx4 = document.getElementById("recl").getContext("2d");
				var data = {
					labels: [1],
				    datasets: [
				        {
				            label: "Reclaimed",
				            fillColor: "rgba(247,179,179,0.2)",
				            strokeColor: "rgba(247, 179,179,1)",
				            pointColor: "rgba(247, 179,179,1)",
				            pointStrokeColor: "#f7b3b3",
				            pointHighlightFill: "#f7b3b3",
				            pointHighlightStroke: "rgba(0,0,0,1)",
				            responsive:true,
				            data: [reclData[1]]
				        }
				    ]
				};
				reclChart = new Chart(ctx4).Line(data);
				
				//Reqs
				var ctx5 = document.getElementById("reqs").getContext("2d");
				var data = {
					labels: [1],
				    datasets: [
				        {
				            label: "Requests",
				            fillColor: "rgba(179,179,247,0.2)",
				            strokeColor: "rgba(179,179,247,1)",
				            pointColor: "rgba(179,179,247,1)",
				            pointStrokeColor: "#b3b3f7",
				            pointHighlightFill: "#b3b3f7",
				            pointHighlightStroke: "rgba(0,0,0,1)",
				            responsive:true,
				            data: [reqsData[1]]
				        }
				    ]
				};
				reqsChart = new Chart(ctx5).Line(data);
			} else {
				reclChart.addData([reclData[gcs]], gcs);
				reqsChart.addData([reqsData[gcs]], gcs);
			}
			
		}
	}
	prevVal = val;
}

function draw(){
	t = Math.floor((Date.now() - t0)/1000) - 1;
	
	if(!heapData[t]){
		heapData[t]=[];
		heapData[t].count=0;
		heapData[t].avg = -1;
		heapData[t].resp = -1;
		return;
	}
	
	if(!heapChart){
		//Heap
		var ctx = document.getElementById("heap").getContext("2d");
		var data = {
			labels: [t],
		    datasets: [
		        {
		            label: "Heap Size",
		            fillColor: "rgba(38,179,247,0.2)",
		            strokeColor: "rgba(38,179,247,1)",
		            pointColor: "rgba(38,179,247,1)",
		            pointStrokeColor: "#26b3f7",
		            pointHighlightFill: "#26b3f7",
		            pointHighlightStroke: "rgba(0,0,0,1)",
		            responsive:true,
		            data: [heapData[t].avg]
		        }
		    ]
		};
		heapChart = new Chart(ctx).Line(data);
		
		//Resp
		var ctx2 = document.getElementById("resp").getContext("2d");
		var data = {
			labels: [t],
		    datasets: [
		        {
		            label: "Response Time",
		            fillColor: "rgba(179,247,38,0.2)",
		            strokeColor: "rgba(179,247,38,1)",
		            pointColor: "rgba(179,247,38 ,1)",
		            pointStrokeColor: "#b3f726",
		            pointHighlightFill: "#b3f726",
		            pointHighlightStroke: "rgba(0,0,0,1)",
		            responsive:true,
		            data: [heapData[t].resp]
		        }
		    ]
		};
		respChart = new Chart(ctx2).Line(data);
		
		//Thru
		var ctx3 = document.getElementById("thru").getContext("2d");
		var data = {
			labels: [t],
		    datasets: [
		        {
		            label: "Throughput",
		            fillColor: "rgba(247,38,179,0.2)",
		            strokeColor: "rgba(247, 38,179,1)",
		            pointColor: "rgba(247, 38,179,1)",
		            pointStrokeColor: "#f726b3",
		            pointHighlightFill: "#f726b3",
		            pointHighlightStroke: "rgba(0,0,0,1)",
		            responsive:true,
		            data: [heapData[t].count]
		        }
		    ]
		};
		thruChart = new Chart(ctx3).Line(data);
	} else {
		if(heapData[t].count > 0){
			heapChart.addData([heapData[t].avg], t);
			respChart.addData([heapData[t].resp], t);
			thruChart.addData([heapData[t].count], t);
		}
	}
	
	//Check visPoints
	while(heapChart.datasets[0].points.length > visPoints){
		heapChart.removeData();
		respChart.removeData();
		thruChart.removeData();
	}
}
		

function setupGraphDim(){
	var w = Math.floor($(window).width()*0.9);
	var h = Math.floor($(window).height()*0.97-32-290-60);
	
	console.log(w+", "+h);
	
	$("#heap").attr("width", w);
	$("#heap").attr("height", h);
	
	$("#resp").attr("width", w);
	$("#resp").attr("height", h);
	
	$("#thru").attr("width", w);
	$("#thru").attr("height", h);
	
	$("#recl").attr("width", w);
	$("#recl").attr("height", h);
	
	$("#reqs").attr("width", w);
	$("#reqs").attr("height", h);
}

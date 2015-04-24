/*
    Sketchy
    Copyright (C) 2015 Matthew Havlovick
    http://www.quickdrawbot.com

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

Contact Info:

Matt Havlovick
QuickDraw
470 I St
Washougal, WA 98671

matt@quickdrawbot.com
http://www.quickdrawbot.com

This General Public License does not permit incorporating your program into
proprietary programs.  If your program is a subroutine library, you may
consider it more useful to permit linking proprietary applications with the
library.  If this is what you want to do, use the GNU Lesser General
Public License instead of this License.

*/

$( document ).ready(function() {
	
	// Setup Tabs

	$( "#main-tabs" ).tabs();
	$( "#settings-tabs" ).tabs();
	$( "#advanced-tabs" ).tabs();
	
	loadFileList();
			
	updateDrawingStatus();

	$("#settings-tab").on("click", function(e){
		loadDrawingSettings();
	});
	
	$("#settings-drawing-tab").on("click", function(e){
		loadDrawingSettings();	
	});
	
	$("#saveDrawingSettingsButton").on("click", function(e){
		saveDrawingSettings();
	});
	
	
	$("#settings-pathing-tab").on("click", function(e){
		$("#pathingProcessorClass").val("");
		loadPathingSettings();	
	});

	$("#pathingProcessorClass").on("change", function(e){
		loadPathingSettings();	
	});
	
	$("#savePathingSettingsButton").on("click", function(e){
		savePathingSettings();
	});
	
	$("#settings-plotter-tab").on("click", function(e){
		$("#plotterControllerClass").val("");
		loadPlotterSettings();	
	});

	$("#plotterControllerClass").on("change", function(e){
		loadPlotterSettings();	
	});
	
	$("#savePlotterSettingsButton").on("click", function(e){
		savePlotterSettings();
	});
	
	$("#settings-hardware-tab").on("click", function(e){
		$("#hardwareControllerClass").val("");
		loadHardwareSettings();	
	});

	$("#hardwareControllerClass").on("change", function(e){
		loadHardwareSettings();	
	});

	$("#saveHardwareSettingsButton").on("click", function(e){
		saveHardwareSettings();
	});
	
	$("#advanced-network-tab").on("click", function(e){
		loadNetworkSettings();	
	});
	
	$("#saveNetworkSettingsButton").on("click", function(e){
		saveNetworkSettings();
	});
	

	$( "#contrastSlider" ).slider({
	      value: 50,
	      min:0,
	      max:100,
	      orientation: "horizontal",
	      animate: true,
	      slide: function( event, ui ) {
	    	  $( "#contrastValue" ).html(ui.value);
	    	  $( "#contrast" ).val(ui.value);
	      }
	});	
	
	$( "#brightnessSlider" ).slider({
	      value: 50,
	      min:0,
	      max:100,
	      orientation: "horizontal",
	      animate: true,
	      slide: function( event, ui ) {
	    	  $( "#brightnessValue" ).html(ui.value);
	    	  $( "#brightness" ).val(ui.value);
	      }
	});	
	
	$( "#thresholdSlider" ).slider({
	      value: 50,
	      min:0,
	      max:100,
	      orientation: "horizontal",
	      animate: true,
	      slide: function( event, ui ) {
	    	  $( "#thresholdValue" ).html(ui.value);
	    	  $( "#threshold" ).val(ui.value);
	      }
	});	
	
	$( "#drawSpeedSlider" ).slider({
	      value: 50,
	      min:10,
	      max:300,
	      orientation: "horizontal",
	      animate: true,
	      slide: function( event, ui ) {
	    	  $( "#drawSpeedValue" ).html(ui.value + " mm/sec");
	      },
	      change: function( event, ui ) {
	    	  $( "#drawSpeedValue" ).html(ui.value + " mm/sec");
	    	  changeDrawSpeed(ui.value);
	      } 
	});	
	
	$( "#moveSpeedSlider" ).slider({
	      value: 50,
	      min:10,
	      max:300,
	      orientation: "horizontal",
	      animate: true,
	      slide: function( event, ui ) {
	    	  $( "#moveSpeedValue" ).html(ui.value + " mm/sec");
	      },
	      change: function( event, ui ) {
	    	  $( "#moveSpeedValue" ).html(ui.value + " mm/sec");
	    	  changeMoveSpeed(ui.value);
	      } 
	});	
	
	
	$("#upgradeSoftwareButton").on("click", function(e){
		upgradeSoftware();
	});
	
	
	$("#uploadUpgradeFile").change(uploadUpgradeFile);
	
	$("#uploadFile").change(uploadFile);
	
	$(".imageOption").on("change", function(e){
		$("#drawPicture").prop("disabled","disabled");
	});
	
	$("#shutdown").on("click", function(e){
		shutDown();
	});
	
	$(".restart").on("click", function(e){
		restart();
	});
	

	// Setup Drawing Dialog Window
	$( "#dialog-drawingStatus" ).dialog({
		modal: true,
		width: 500,
		autoOpen: false,
	    buttons: {
	     	"Pause" : {
				text: "Pause",
				id: "dialog-pauseButton",
				click: function() {
					if ($('#dialog-pauseButton').button('option', 'label')=='Pause'){
						setDrawingStatus("pause");
					} else {
						setDrawingStatus("resume");
					}
				}
			},
			"Cancel" : {
				text: "Cancel",
				id: "dialog-cancelButton",
				click: function() {
					setDrawingStatus("cancel");
				}
		   	}
		}
	});
	
	$( "#dialog-render" ).dialog({
		modal: true,
		width: 600,
		autoOpen: false,
		buttons: {
	     	"Render" : {
				text: "Render Image",
				id: "dialog-renderButton",
				click: function() {
					renderImage();
				}
	     	}
		}
	});
	
	$( "#dialog-status" ).dialog({
		dialogClass: "no-close",
		width: 400,
        autoOpen: false,
        closeOnEscape: true,
   	 	modal: true,
   	 	buttons: {
   	 	},
        hide: {
            effect: "highlight",
            duration: 1000
        }
    });

	
	
	$( "#dialog-message" ).dialog({
        autoOpen: false,
		width: 400,
        hide: {
          effect: "highlight",
          duration: 1000
        }
    });
	
	$( "#dialog-error" ).dialog({
        autoOpen: false,
		width: 400,
   	 	modal: true,
   	 	buttons: {
   	 		Ok: function() {
   	 			$( this ).dialog( "close" );
   	 		}
   	 	},      
        hide: {
          effect: "explode",
          duration: 250
        }
    });
	
	
	jQuery("#grid").jqGrid({
	      datatype: "local",
	      colNames:['Name', 'Image Size','',''],
	      colModel:[
	                {name:'imageName',index:'imageName', width:300, sortable:false, editable:false},
	                {name:'imageSize',index:'imageSize', width:100, editable:false},
	                {name:'filename',index:'filename', width:400, sortable:false, editable:false, hidden:true},
	                {name:'subgrid_id',index:'subgrid_id', width:400, sortable:false, editable:false, hidden:true}
	           ],
	     jsonReader : {
	    	 jsonReader : {
	    		 root:"rows",
	    		 repeatitems: false
	    	 },
	    	 subgrid: {
	    		 root: "rows",
	    		 repeatitems: false
	    	 }	    	 
	     },
	      caption:"Files",
	      height: 1000,
	      width: 700,
	      gridview: true,
	      scrollrows : true,
	      shrinkToFit: false,
	      pager: '#gridpager',
	      pgbuttons : false,
	      viewrecords : false,
	      pgtext : "",
	      pginput : false,
          beforeSelectRow: function(id){
        	  jQuery('#grid').jqGrid('resetSelection');
	          var subGridRowIds = $("#grid").getDataIDs();
	          $.each(subGridRowIds, function (index, subGridRowId) {
	        	  var subGridId = $("#grid").jqGrid("getCell", subGridRowId, "subgrid_id");
        		  $('#' + subGridId).jqGrid('resetSelection');
	          });        	  
          },	      
	      onSelectRow: function(id){ 
	    	  if(id){
	        	  var myGrid = $('#grid');
	        	  var imageName = myGrid.jqGrid ('getCell', id, 'imageName');	        	  
	        	  var imageFilename = myGrid.jqGrid ('getCell', id, 'filename');	 
	    		  onGridImageFileSelected("SOURCE", imageName, imageFilename);
	          }
	       },
	       subGrid: true,
	       subGridRowExpanded: function(subgrid_id, row_id) {
	    	   var subgrid_table_id;
	           subgrid_table_id = subgrid_id+"_t";
	           // push the subgrid ID into the dataModel on the main Grid
	           $('#grid').jqGrid('setCell', row_id, 'subgrid_id', subgrid_table_id);	
	           $("#"+subgrid_id).html("<table id='"+subgrid_table_id+"' class='scroll'></table>");
	           $("#"+subgrid_table_id).jqGrid({
	              datatype: "local",
	              colNames: ['Drawing Size','Pen Width','Contrast','Brightness','Threshold','',''],
	              colModel: [
	                        {name:"drawingSize",index:"drawingSize",width:150,align:"left"},
	    	                {name:"penWidth",index:"penWidth",width:70,align:"left"},	 
	    	                {name:'contrast',index:'contrast', width:70, editable:false},
	    	                {name:'brightness',index:'brightness', width:70, editable:false},
	    	                {name:'threshold',index:'threshold', width:70, editable:false},
	    	                {name:'filename',index:'filename', width:100, sortable:false, editable:false, hidden:true},
	    	                {name:'imageName',index:'imageName', width:100, sortable:false, editable:false, hidden:true}
	              ],
	              gridview: true,
	              height: 'auto',
	              width: 600,
	              scrollrows : true,
	              shrinkToFit: false,
	              beforeSelectRow: function(id){
	            	  // reset all selections
	            	  jQuery('#grid').jqGrid('resetSelection');
	    	          var subGridRowIds = $("#grid").getDataIDs();
	    	          $.each(subGridRowIds, function (index, subGridRowId) {
	    	        	  var subGridId = $("#grid").jqGrid("getCell", subGridRowId, "subgrid_id");
    	        		  $('#' + subGridId).jqGrid('resetSelection');
	    	          });
	              },
	              onSelectRow: function(id){
	    	          if(id){
	    	        	  var myGrid = $("#"+subgrid_table_id);
	    	        	  var imageName = myGrid.jqGrid ('getCell', id, 'imageName');	        	  
	    	        	  var imageFilename = myGrid.jqGrid ('getCell', id, 'filename');
	    	    		  onGridImageFileSelected("RENDERED", imageName, imageFilename);
	    	          }
	    	       }	              
	           }); 
	           loadRenderedFilesList(subgrid_table_id, $('#grid').jqGrid ('getCell', row_id, 'imageName'));
	       }
	 }).navGrid('#gridpager',{edit:false,add:false,del:false,search:false, refresh:false})
	   .navButtonAdd('#gridpager',{
		   caption:"Delete File", 
		   buttonicon:"ui-icon-trash",
		   id: 'deleteButton',
		   onClickButton: function(){ 
			   var imageName = _gridSelectedImage;
			   if (imageName){
				   var confirmDeleteFile = false;
				   if (imageName.lastIndexOf(".rendered")==imageName.length-9){
					   confirmDeleteFile = confirm("Are you sure you want to delete Rendered Image '" + imageName + "'?");
				   } else {
					   confirmDeleteFile = confirm("Are you sure you want to delete Image '" + imageName + "' and all it's Rendered Images?");
				   }
				   if (confirmDeleteFile){
					   deleteFile(imageName);
				   }
			   }
		   }, 
		   position:"last"})
	   .navButtonAdd('#gridpager',{
		   caption:"Upload File", 
		   buttonicon:"ui-icon-arrowthickstop-1-n",
		   id: 'uploadButton',
		   onClickButton: function(){ 
			   $('#uploadFile').click();
		   }, 
		   position:"last"})
		.navButtonAdd('#gridpager',{
		   caption:"Render Image", 
		   buttonicon:"ui-icon-gear",
		   id: 'renderButton',
		   onClickButton: function(){
			   loadRenderForm();
		   }, 
		   position:"last"})
		.navButtonAdd('#gridpager',{
		   caption:"Draw Image", 
		   buttonicon:"ui-icon-play",
		   id: 'drawButton',
		   onClickButton: function(){
			   var imageName = _gridSelectedImage;
			   drawImage(imageName);
		   }, 
		   position:"last"})		   

});

// ******* End .ready

//***** Grid globals and functions

var __gridSelectedImage;
var __updateDrawingStatusTimer;

function onGridImageFileSelected(imageType, imageName, imageFileName){
		
	  _gridSelectedImage=imageName;	
      $("#sourceImageName").val(imageName); // for populating the Render Form
      
	  $("#displayImage").attr("src", "upload/" + encodeURIComponent(imageFileName) + "?random=" + Math.random());
	
	  if (imageType==="RENDERED"){
			$("#deleteButton").removeClass("ui-state-disabled");
		    $("#renderButton").addClass("ui-state-disabled");
			$("#drawButton").removeClass("ui-state-disabled");
	  } else if (imageType==="SOURCE"){
			$("#deleteButton").removeClass("ui-state-disabled");
			$("#renderButton").removeClass("ui-state-disabled");
			$("#drawButton").addClass("ui-state-disabled");
	  } else {
			$("#deleteButton").addClass("ui-state-disabled");
			$("#renderButton").addClass("ui-state-disabled");
			$("#drawButton").addClass("ui-state-disabled");			
	  }
	  
	  
}

function showRenderForm(){
    $("#dialog-renderMessage").text("");
    $( "#renderProgressBar" ).progressbar({value: 0});
	if (!($("#dialog-render").dialog( "isOpen" )===true)) {
		  $( "#dialog-render" ).dialog( "open" );
	}
}

function showMessage(message, delay){
	if (delay==null) delay=1500;
	$("#dialog-messageText").text(message);
	if (!($("#dialog-message").dialog( "isOpen" )===true)) {
		  $( "#dialog-message" ).dialog( "open" );
	}
	
	setTimeout(function() {  
	 $( "#dialog-message" ).dialog( "close" );
	}, delay);
}


function showError(message){
	$("#dialog-errorText").text(message);
	if (!($("#dialog-error").dialog( "isOpen" )===true)) {
		  $( "#dialog-error" ).dialog( "open" );
	}
}


/**** JSON HTTP Support functions ****/

// Default Callback Handlers

function defaultSuccessCallback(message){
	showMessage("SUCCESS: " + message);
}

function defaultErrorCallback(message){
	showError("ERROR: " + message);
}

function jsonGetRequest(URL, jsonString, successCallback, errorCallback, ignoreCommunicationError){
	jsonRequest(URL, "GET", jsonString, successCallback, errorCallback, ignoreCommunicationError);
}

function jsonPostRequest(URL, jsonString, successCallback, errorCallback, ignoreCommunicationError){
	jsonRequest(URL, "POST", jsonString, successCallback, errorCallback, ignoreCommunicationError);
}

function jsonRequest(URL, method, jsonString, successCallback, errorCallback, ignoreCommunicationError){
	jQuery.ajax({
       	url: URL,
      	type: method,
      	dataType: "json",
      	data: jsonString,
      	cache: false,
       	processData: true,
       	timeout:10000,
       	success: function (data) {
       		if (data.status==="SUCCESS") {
       			if (successCallback){
       				successCallback(data);
       			} else {
       				defaultSuccessCallback(data.message);
       			}
       		} else if (data.status==="ERROR"){
       			if (errorCallback){
       				errorCallback(data);
       			} else {
       				defaultErrorCallback(data.message);
       			}
       		} else {
       			if (errorCallback){
       				errorCallback(data);
       			} else {
       				defaultErrorCallback("Unexpected Response." + data.message);
       			}       			
       		}
       	},
       	error: function (XMLHttpRequest, textStatus, errorThrown){
       		if ((ignoreCommunicationError==null) || (ignoreCommunicationError!=true)){
       			defaultErrorCallback("Server Communication Error! "+ textStatus + "(" + errorThrown + ")");
       		}
       	}
	});
	
}


/***** Hardware Settings Functions *****/

function saveHardwareSettings() {
	var hardwareSettingsForm = form2js('hardwareSettingsForm', '.', true,null,false);
	if (hardwareSettingsForm.class==null){
		showError("You must select a Hardware Controller Class before Saving!");
	} else {	
		jsonPostRequest("/servlet/SaveHardwareSettings",JSON.stringify(hardwareSettingsForm), 
			function(data){
				showMessage("Hardware Settings Saved!");
	    		loadHardwareSettings();
		    },
		    function(data){
		    	showError(data.message);	
		    	loadHardwareSettings();
		    }
		);
	}
}

function changeDrawSpeed(value) {
	jsonPostRequest("/servlet/ChangeSpeed?drawSpeed=" + value, "", 
		function(data){
	    },
	    function(data){
	    	showError(data.message);	
	    }
	);
}


function changeMoveSpeed(value) {
	jsonPostRequest("/servlet/ChangeSpeed?moveSpeed=" + value, "", 
		function(data){
	    },
	    function(data){
	    	showError(data.message);	
	    }
	);
}

function testHardwareSettings(action) {
	var hardwareSettingsForm = form2js('hardwareSettingsForm', '.', true,null,false);
	jsonPostRequest("/servlet/TestHardwareSettings?action=" + action,JSON.stringify(hardwareSettingsForm), 
		function(data){
	    },
	    function(data){
	    	showError(data.message);	
	    }
	);
}


function loadHardwareSettings() {
	// clear the div first in case there could be a connection issue, or lag
	$("#hardwarePropertiesDiv").html("");
	var className = $("#hardwareControllerClass").val();
	if (className==null) className="";
	
	jsonGetRequest("/servlet/GetHardwareSettings?class=" + className,"", function(data){
		populateHardwareSettings(data);
	});
}

function populateHardwareControllerList(data){
    $('#hardwareControllerClass').empty();
	var hardwareControllerClasses=data.hardwareControllerClasses;
	$('#hardwareControllerClass').append("<option value=''>-- Select Class --</option>");
	for (var key in hardwareControllerClasses){
	     $('#hardwareControllerClass')
         .append($("<option></option>")
         .attr("value",hardwareControllerClasses[key])
         .text(key)); 
	}
}


function populateHardwareSettings(data){
	populateHardwareControllerList(data);
	
	if (data.properties!=null){
		$('#hardwareControllerClass').val(data.properties.class);
		buildPageProperties($("#hardwarePropertiesDiv"), data.properties.metaData);
		js2form($("#hardwareSettingsForm")[0],  data.properties);
	}
}


/***** Plotter Settings Functions *****/

function savePlotterSettings() {
	var plotterSettingsForm = form2js('plotterSettingsForm', '.', true,null,false);
	if (plotterSettingsForm.class==null){
		showError("You must select a Plotter Controller Class before Saving!");
	} else {	
		jsonPostRequest("/servlet/SavePlotterSettings",JSON.stringify(plotterSettingsForm), 
			function(data){
				showMessage("Plotter Settings Saved!");
	    		loadPlotterSettings();
		    },
		    function(data){
		    	showError(data.message);	
		    	loadPlotterSettings();
		    }
		);
	}
}

function loadPlotterSettings() {
	// clear the div first in case there could be a connection issue, or lag
	$("#plotterPropertiesDiv").html("");
	var className = $("#plotterControllerClass").val();
	if (className==null) className="";
	
	jsonGetRequest("/servlet/GetPlotterSettings?class=" + className,"", function(data){
		populatePlotterSettings(data);
	});
}

function populatePlotterControllerList(data){
    $('#plotterControllerClass').empty();
	var plotterControllerClasses=data.plotterControllerClasses;
	$('#plotterControllerClass').append("<option value=''>-- Select Class --</option>");
	for (var key in plotterControllerClasses){
	     $('#plotterControllerClass')
         .append($("<option></option>")
         .attr("value",plotterControllerClasses[key])
         .text(key)); 
	}
}


function populatePlotterSettings(data){
	populatePlotterControllerList(data);
	
	if (data.properties!=null){
		$('#plotterControllerClass').val(data.properties.class);
		buildPageProperties($("#plotterPropertiesDiv"), data.properties.metaData);
		js2form($("#plotterSettingsForm")[0],  data.properties);
	}
}


/***** Pathing Settings Functions *****/

function savePathingSettings() {
	var pathingSettingsForm = form2js('pathingSettingsForm', '.', true,null,false);

	if (pathingSettingsForm.class==null){
		showError("You must select a Pathing Processor Controller Class before Saving!");
	} else {
		jsonPostRequest("/servlet/SavePathingSettings",JSON.stringify(pathingSettingsForm), 
			function(data){
				showMessage("Pathing Settings Saved!");
	    		loadPathingSettings();
		    },
		    function(data){
		    	showError(data.message);	
		    	loadPathingSettings();
		    }
		);
	}
}

function loadPathingSettings() {
	// clear the div first in case there could be a connection issue, or lag
	$("#pathingPropertiesDiv").html("");
	var className = $("#pathingProcessorClass").val();
	if (className==null) className="";
	
	jsonGetRequest("/servlet/GetPathingSettings?class=" + className,"", function(data){
		populatePathingSettings(data);
	});
}

function populatePathingControllerList(data){
    $('#pathingProcessorClass').empty();
	var pathingProcessorClasses=data.pathingProcessorClasses;
	$('#pathingProcessorClass').append("<option value=''>-- Select Class --</option>");
	for (var key in pathingProcessorClasses){
	     $('#pathingProcessorClass')
         .append($("<option></option>")
         .attr("value",pathingProcessorClasses[key])
         .text(key)); 
	}
}


function populatePathingSettings(data){
	populatePathingControllerList(data);
	if (data.properties!=null){
		$('#pathingProcessorClass').val(data.properties.class);
		buildPageProperties($("#pathingPropertiesDiv"), data.properties.metaData);
		js2form($("#pathingSettingsForm")[0],  data.properties);
	}
}


function loadRenderForm(){
	jsonGetRequest("/servlet/GetRenderImageAttributes","", function(data){
		showRenderForm();
		populateRenderForm(data);
	});
}


function populateRenderForm(data){
	var selectedOption = $('#drawingSize option:selected').val();
	$('#drawingSize').html("");
	$('#penWidth').html("");
	if (data!=null){
		$.each(data.drawingSizes, function(key, value) {   
		     $('#drawingSize')
		         .append($("<option></option>")
		         .attr("value",value)
		         .text(value)); 
		});
		
		$.each(data.penSizes, function(key, value) {   
		     $('#penWidth')
		         .append($("<option></option>")
		         .attr("value",key)
		         .text(value)); 
		});
		$("#penWidth option[value='" + data.penWidth +"']").prop('selected', true);
	}
	$("#drawingSize option[value='" + selectedOption +"']").prop('selected', true);

}


/***** Drawing Settings Functions *****/

function saveDrawingSettings() {
	var drawingSettingsForm = form2js('drawingSettingsForm', '.', true,null,false);
	jsonPostRequest("/servlet/SaveDrawingSettings",JSON.stringify(drawingSettingsForm), 
		function(data){
			showMessage("Drawing Settings Saved!");
    		loadDrawingSettings();
	    },
	    function(data){
	    	showError(data.message);	
	    	loadDrawingSettings();
	    }
	);
}

function loadDrawingSettings() {
	// clear the div first in case there could be a connection issue, or lag
	$("#drawingPropertiesDiv").html("");
	jsonGetRequest("/servlet/GetDrawingSettings","", function(data){
		populateDrawingSettings(data);
	});
}

function populateDrawingSettings(data){
	if (data.properties!=null){
		buildPageProperties($("#drawingPropertiesDiv"), data.properties.metaData);
		js2form($("#drawingSettingsForm")[0], data.properties);
	}
}


function loadNetworkSettings() {
	$("#networkSSID").val("");
	$("#networkPassword").val("");
	jsonGetRequest("/servlet/ManageNetworkSettings","", function(data){
		$("#networkSSID").val(data.ssid);
	});
}

function saveNetworkSettings() {
	var networkSettingsForm = form2js('networkSettingsForm', '.', true,null,false);
	jsonPostRequest("/servlet/ManageNetworkSettings",JSON.stringify(networkSettingsForm), 
		function(data){
			showMessage("Network Settings Saved!  Reboot Required.");
    		loadNetworkSettings();
	    },
	    function(data){
	    	showError(data.message);	
    		loadNetworkSettings();
	    }
	);
}


function upgradeSoftware(){
	jsonPostRequest("/servlet/UpgradeSoftware","", function(data){
   		$("#upgradeMessage").html("The Software has been upgraded, but a reboot is required!");
		showMessage("Sketchy has been Upgraded!");
	});
}

function restart(){
	jsonPostRequest("/servlet/Restart","", function(data){
		clearTimeout(__updateDrawingStatusTimer);
   		showMessage("The Raspberry Pi is now Restarting. Please refresh the browser in 60 seconds!", 60000);
	});
}

function shutDown(){
	jsonPostRequest("/servlet/Shutdown","", function(data){
		clearTimeout(__updateDrawingStatusTimer);
   		showMessage("The Raspberry Pi is Shutting Down. Please wait 60 seconds before disconnecting power!", 60000);
	});
}

function renderImage(){
	var filename = $("#name").val();
	jsonPostRequest("/servlet/RenderImage",JSON.stringify(getRenderParameters()), function(data){
		updateRenderStatus();
	});
}


function drawImage(imageName){
	jsonGetRequest("/servlet/DrawImage?imageName="+imageName, "", function(data){});	
}

function setDrawingStatus(action){
	
	jQuery.ajax({
       	url: "/servlet/SetDrawingStatus?action=" + action,
      	type: "GET",
      	dataType: "text",
      	data: "",
       	processData: true,
       	success: function (res) {
       		// don't close here.. let the status update close the dialog box
   		}
	});
}

function deleteFile(filename){
	jsonGetRequest("/servlet/DeleteImage","imageName=" + encodeURIComponent(filename), 
		function(data) {
			$("#displayImage").attr("src", "");
		    $("#sourceImageName").val(""); // for populating the Render Form
			loadFileList();
	   	}
	);		
}

function loadFileList(imageName){

	$("#displayImage").attr("src", "");
	// if filename is given.. put it in the selectFile
	__gridSelectImageName=imageName;
	
	$("#grid").jqGrid("clearGridData", true);

	jsonGetRequest("/servlet/GetImageFiles","", 
       	function(data) {
			$("#deleteButton").addClass("ui-state-disabled");
			$("#renderButton").addClass("ui-state-disabled");
			$("#drawButton").addClass("ui-state-disabled");
			
			$(data.rows).each(function(index, element) {
       			$("#grid").addRowData( index, element);
       			
       			if (__gridSelectImageName){
       				if (__gridSelectImageName===element.imageName){
       					jQuery('#grid').jqGrid('setSelection',index); 
       				} else if (__gridSelectImageName.indexOf(element.imageName)==0){
       					$("#grid").jqGrid ('expandSubGridRow', index);
       				}
       			}

       		});
   		}
	);	
}

function loadRenderedFilesList(subGridId, imageName){
	$("#" + subGridId).jqGrid("clearGridData", true);
	
	jsonGetRequest("/servlet/GetRenderedImageFiles?imageName="+imageName,"", 
       	function(data) {
       		$(data.rows).each(function(index, element) {
       			$("#" + subGridId).addRowData( index, element);
   				if (__gridSelectImageName===element.imageName){
   					$("#" + subGridId).jqGrid('setSelection',index); 
   				}
       		});
   		}
	);	
}


function updateRenderStatus() {
	jsonGetRequest("/servlet/GetRenderStatus","", 
		function(data) {
			if (data.renderStatus){
				if ((data.renderStatus==="INIT") || (data.renderStatus==="RENDERING")){
				    $("#dialog-renderMessage").text(data.renderMessage);
				    $( "#renderProgressBar" ).progressbar({value: data.progress});
		    		setTimeout('updateRenderStatus()',1000);
				} else if (data.renderStatus==="ERROR"){ 
					showError(data.renderMessage);
		    		setTimeout('updateRenderStatus()',1000);
				} else {
					$("#dialog-render" ).dialog( "close" );
					if (data.renderStatus==="CANCELLED"){
						showError(data.renderMessage);
					} else if (data.renderStatus==="COMPLETED"){
			    		var imageName = data.imageName;
			    		$("#drawPicture").prop("disabled","");
			    		loadFileList(imageName);
					}
				}
			} else {
				$("#dialog-render" ).dialog( "close" ); // make sure it's closed
			}
		},null,true
	);
}


function updateDrawingStatus() {
	jsonGetRequest("/servlet/GetDrawingStatus","", 
		function(data, message){
			if (data!=null){
				$("#dialog-drawingMessage").html(data.drawingStatusMessage);
				if ((data.drawingStatus==="") || 
					(data.drawingStatus==="CANCELLED") || 
					(data.drawingStatus==="COMPLETED")) {
					if ($("#dialog-drawingStatus").dialog( "isOpen" )===true) {
						$("#dialog-drawingStatus").dialog( "close" );
					}
				} else if ((data.drawingStatus==="INIT") ||
							(data.drawingStatus==="DRAWING") || 
							(data.drawingStatus==="ERROR")) {
					if (!($("#dialog-drawingStatus").dialog( "isOpen" )===true)) {
						// update the values only if first opening it
						$("#drawSpeedSlider").slider("value" , data.drawSpeed);
						$("#moveSpeedSlider").slider("value" , data.moveSpeed);
						$( "#dialog-drawingStatus" ).dialog( "open" );
					}
			    	
					if ($('#dialog-pauseButton').button('option', 'label')!=="Pause"){ 
			    		$('#dialog-pauseButton').button('option', 'label', 'Pause');
			    	}
				    $( "#drawingProgressBar" ).progressbar({value: data.progress});
				} else if (data.drawingStatus==="PAUSED") {
					if (!($("#dialog-drawingStatus").dialog( "isOpen" )===true)) {
						// update the values only if first opening it
						$("#drawSpeedSlider").slider("value" , data.drawSpeed);
						$("#moveSpeedSlider").slider("value" , data.moveSpeed);
						$( "#dialog-drawingStatus" ).dialog( "open" );
					}
					if ($('#dialog-pauseButton').button('option', 'label')!=="Resume"){
						$('#dialog-pauseButton').button('option', 'label', "Resume");
					}
				}
			}
		}, 
		function(data){
			showError(data.message);
	    },true
	);
	__updateDrawingStatusTimer=setTimeout('updateDrawingStatus()',1000);
}

function addListFile(filename){
	var displayName = filename;
	if (displayName.length>30){
		displayName=displayName.substr(0,35) + "...";
	}

	var itemHtml = "<option value=\"" + filename + "\">" + displayName + "</option>";

	$('#fileList').append(itemHtml);
}

function uploadFile(){
	if (($("#uploadFile"))[0].files.length > 0) {
		var file = ($("#uploadFile"))[0].files[0];
		$("#uploadFile").val("");
		var ext = file.name.split('.').pop();
		if (ext!=file.name) {
          	formdata = new FormData();
            formdata.append("file", file);
            $("#dialog-statusMessage").html("Please Wait. Uploading File: " + file.name);
        	if (!($("#dialog-status").dialog( "isOpen" )===true)) {
        		$( "#dialog-status" ).dialog( "open" );
        	}
           	jQuery.ajax({
               	url: "/imageUpload/upload",
              	type: "POST",
               	data: formdata,
               	processData: false,               	
               	contentType: false,
               	success: function (json) {
               		var data = JSON.parse(json);
               		if (data.status==="SUCCESS") {
                      	loadFileList(data.imageName);
               		} else if (data.status==="ERROR"){
           				defaultErrorCallback(data.message);
               		} else {
           				defaultErrorCallback("Unexpected Response." + data.message);
               		}
                	if (($("#dialog-status").dialog( "isOpen" )===true)) {
                		$( "#dialog-status" ).dialog( "close" );
                	}
               	},
               	error: function (XMLHttpRequest, textStatus, errorThrown){
        			defaultErrorCallback(textStatus + "(" + errorThrown + ")");
                	if (($("#dialog-status").dialog( "isOpen" )===true)) {
                		$( "#dialog-status" ).dialog( "close" );
                	}        			
               	}
           	});
        } else {
        	showError('Not a valid file!');
        }
   	}
}

function uploadUpgradeFile(){
	$("#upgradeMessage").html="";
	$("#upgradeSoftwareButton").hide();
	
	if (($("#uploadUpgradeFile"))[0].files.length > 0) {
		var file = ($("#uploadUpgradeFile"))[0].files[0];
		var ext = file.name.split('.').pop();
		if (ext!=file.name) {
			formdata = new FormData();
            formdata.append("file", file);
            $("#dialog-statusMessage").html("Please Wait. Uploading File: " + file.name);
        	if (!($("#dialog-status").dialog( "isOpen" )===true)) {
        		$( "#dialog-status" ).dialog( "open" );
        	}            
           	jQuery.ajax({
               	url: "/upgradeUpload/upload",
              	type: "POST",
               	data: formdata,
               	processData: false,               	
               	contentType: false,
               	success: function (json) {
               		var data = JSON.parse(json);
               		if (data.status==="SUCCESS") {
               			$("#upgradeMessage").html("Ready to upgrade to version " + data.version);
               			$("#upgradeSoftwareButton").show();
               		} else if (data.status==="ERROR"){
           				defaultErrorCallback(data.message);
               		} else {
           				defaultErrorCallback("Unexpected Response." + data.message);
               		}
                	if (($("#dialog-status").dialog( "isOpen" )===true)) {
                		$( "#dialog-status" ).dialog( "close" );
                	}                   		
               	},
               	error: function (XMLHttpRequest, textStatus, errorThrown){
        			defaultErrorCallback(textStatus + "(" + errorThrown + ")");
                	if (($("#dialog-status").dialog( "isOpen" )===true)) {
                		$( "#dialog-status" ).dialog( "close" );
                	}          			
               	}
           	});
        } else {
        	showError('Not a valid upgrade file!');
        }
   	}
}


function getRenderParameters(){
	return form2js('renderForm', '.', true,null,false);
}


function showRenderDialog(){
    $( "#renderProgressBar" ).progressbar({value: 0});
	updateRenderStatus();
}

function showDrawingDialog(){
	updateDrawingStatus();
}


function formButtonClicked(buttonId, buttonValue){
	testHardwareSettings(buttonId);
}

function buildPageProperties(propertiesDiv, metaData){
	
	var html="<input type='hidden' name='class'/>";
	html+="<div style='display:inline-block; vertical-align:top'><table>";
	var metaDataGroups = metaData.metaDataGroups;

	for (var groupIdx=0;groupIdx<metaDataGroups.length;groupIdx++){
		var metaDataGroup=metaDataGroups[groupIdx];
		var groupName = metaDataGroup.name;
		var metaDataProperties = metaDataGroup.metaDataProperties;
		html+="<tr><td><fieldset><legend>";
		html+=groupName;
		html+="</legend><table>";
		for (var propertyIdx=0;propertyIdx<metaDataProperties.length;propertyIdx++){
			var metaDataProperty=metaDataProperties[propertyIdx];
			html+="<tr>";
			if (metaDataProperty.type==='Boolean'){
				html+="<td>"+metaDataProperty.name+"</td>";
				html+="<td><input type='checkbox' value='true' name='"+metaDataProperty.id+"'></input></td>";
			} else if (metaDataProperty.type==='String' || metaDataProperty.type==='Decimal' || metaDataProperty.type==='Number'){
				html+="<td>"+metaDataProperty.name+"</td>";
				html+="<td><input type='text' size='30' name='"+metaDataProperty.id+"'></input></td>";
			} else if (metaDataProperty.type==='List'){
				html+="<td>"+metaDataProperty.name+"</td>";
				html+="<td><select style='width: 250px;' name='"+metaDataProperty.id+"'>";

				for (key in metaDataProperty.listValues){
					var label=metaDataProperty.listValues[key];
					html+="<option value='" + key + "'>"+label+"</option>";
				}
				html+="</select></td>";
			} else if (metaDataProperty.type==='Button'){
				html+="<td colspan='2'>";
				html+="<input type='button' id='"+ metaDataProperty.id + "' value='" + metaDataProperty.name + "' onClick='formButtonClicked(\"" + metaDataProperty.id + "\",\"" + metaDataProperty.name + "\");'/>";
				html+="</td>";
			}
			html+="</tr>";
		}
		html+="</table></fieldset></td></tr>";
	}

	html+="</table></div>";
	html+="<div style='display:inline-block; margin: 20px 0 0 20px;'>";
	if (metaData.helpImage!==null){
		html+="<img src='" + metaData.helpImage + "' height='600'/>";
	}
	html+="</div>";
	propertiesDiv.html(html);
}
	
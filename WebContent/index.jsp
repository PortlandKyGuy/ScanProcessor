<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Insert title here</title>
<link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
<script   src="https://code.jquery.com/jquery-2.2.2.min.js"  ></script>
<script src="https://code.jquery.com/ui/1.11.4/jquery-ui.min.js" ></script>
<!-- <script src="https://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.js"></script> -->
<script>



function updateDirectory() {
	var path = $("#file").val();
	var newPath = path.substring(0,path.lastIndexOf("\/")+1);
	alert("path=" + path + "\nnewPath=" + newPath);
}

$(function() {
	var dialog, saveFileDialog, form, saveFileNameForm
		,custKeyName = $("#custom-key-name")
		,saveFileName = $("#new-file-name");
	
	function addNewPair() {
		addNewPairElement(custKeyName.val(), null, true);
		
		dialog.dialog("close");
	}
	
	function addNewPairElement(name, id, prependName) {
		var inputName = name;
		if(prependName)
			inputName = "use_" + name;

		var htmlStr = "<label for='" + inputName + "'>" + name + "</label>";
		htmlStr += "<input type='text' name='" + inputName + "' ";
		if(id != null && id.length >0 )
			htmlStr += " id='" + id + "' ";
		htmlStr += ">";
		htmlStr += "<br />";
		
		$("#form-elements").append(htmlStr);
		return inputName;
	}
	
	function saveConfigFile() {
		//add filename element
		addNewPairElement("save-file-as", "save-file-as", false);
		$("#save-file-as").val(saveFileName.val());
		
		//update the form action to include a cmd.
		$("#mainForm").attr("action", "process?cmd=saveconfigfile");
		
		saveFileDialog.dialog("close");
	}
	
    dialog = $( "#dialog-form" ).dialog({
        autoOpen: false,
        height: 300,
        width: 375,
        modal: true,
        
        buttons: {
          "Create Key/Val pair": addNewPair,
          Cancel: function() {
            dialog.dialog( "close" );
          }
        },
        close: function() {
         form[ 0 ].reset();
         // allFields.removeClass( "ui-state-error" );
        }
      });
    
    //Add event handler to the dialog form for new key/value pair
    form = dialog.find( "form" ).on( "submit", function( event ) {
        event.preventDefault();
        addNewPair();
      });
    
    //Add onclick event for adding a new key/value pair.
    $( "#add-new-pair" ).button().on( "click", function() {
        dialog.dialog( "open" );
    });
    
    //Add onclick event for adding a new key/value pair.
    $( "#perm-save-config" ).button().on( "click", function() {
        saveFileDialog.dialog( "open" );
    });
    
    saveFileDialog = $( "#dialog-filesave" ).dialog({
        autoOpen: false,
        height: 300,
        width: 375,
        modal: true,
        
        buttons: {
          "Save File": saveConfigFile,
          Cancel: function() {
        	  saveFileDialog.dialog( "close" );
          }
        },
        close: function() {
         form[ 0 ].reset();
         //allFields.removeClass( "ui-state-error" );
        }
      });
    
    //Add event handler to the saveFileNameForm 
    saveFileNameForm = saveFileDialog.find( "form" ).on( "submit", function( event ) {
        event.preventDefault();
        saveConfigFile();
		$("form#mainForm").submit();
      });
   
    
    
    //get the filepath when the file changes
    $('input:file').change(
    	    function(e){
    	        var name = e.target.files[0].name;
    	       // alert(name);
    	        var newPath = name.substring(0,name.lastIndexOf("/")+1);
    	        if(newPath == null || newPath.length == 0) 
    	        	newPath = name.substring(0,name.lastIndexOf("\\")+1);
    	        //alert(newPath);
    	        $("#dir").val(newPath);
    	    });
    
    //Load the pre-loaded config items   	
        $.ajax({url: "process?cmd=getpreconfigs", success: function(result){
        	var retStr = "";
        	var jsonArr = JSON.parse(result);
         	//for(jObj in jsonArr) {
        	$.each(jsonArr, function(index, value) {
        		var jObj = value; //JSON.parse(value);
        		retStr += "<button class='pre_conf_file' value='" + jObj.file_name + "'>";
        		retStr += jObj.file_name;
        		retStr += "</button>";
        	});
        	//}
        	
   			//alert(jsonArr);
        	
            $("#pre-loaded").html(retStr);
            
            setupPreConfEvents();
            
        }});
    
    function setupPreConfEvents() {
        $( ".pre_conf_file" ).button().on( "click", function() {
        	
        	//var fname = $(this).value();
        	var fname = $(this).attr('value');
        	
        	//alert(fname);
        	$.ajax({url: "process?cmd=getspecificconfig", data: {"file_name":fname}, success: function(result){
            	console.log(result);
            	var jObj = JSON.parse(result);
            	//set the dir folder
            	$("#dir").val(jObj.dir);
            	
             	var configDetails = jObj.config_details;
             	for (key in configDetails) {
             		console.log("key=" + key);
             		var htmlElementExists = $("#" + key).length;
             		if(htmlElementExists) {
             			$("#" + key).val(configDetails[key]);
             		} else if (configDetails[key].length > 0 ) {
             			console.log(key + " doesn't exist. adding");
             			var newElement = addNewPairElement(key, key, true);
             			$("#" + key).val(configDetails[key]);
             		}
             	}
            }});
        });
    }

});

</script>
<style>
input {
	width: 350px;
}

label, input {
	margin-bottom: 5px;
}

#pre-loaded {
	margin-top: 10px;
}

.pre_conf_file{
	display: inline-block;
	margin: 5px;
	word-wrap: break-word;
	width: 150px;
	height: 150px;
}

.main_btn {
	display: inline-block;
	margin: 5px;
	word-wrap: break-word;
	width:200px;
	height:100px;
	background-color: #444c74;
	}

</style>
</head>
<body>
<div id="dialog-form" title="Add key/val pair">
  <form>
    <fieldset>
      <label for="key">key</label><br />
      <input type="text" name="custom_key_name" id="custom-key-name" class="text ui-widget-content ui-corner-all"><br />
      <!-- Allow form submission with keyboard without duplicating the dialog button -->
      <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
    </fieldset>
  </form>
</div>

<div id="dialog-filesave" title="Config File Name">
  <form>
    <fieldset>
      <label for="new-file-name">New File Name</label><br />
      <input type="text" name="new-file-name" id="new-file-name" class="text ui-widget-content ui-corner-all"><br />
      <!-- Allow form submission with keyboard without duplicating the dialog button -->
      <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
    </fieldset>
  </form>
</div>

<div id="content">
	<div id="guidelines">
		<p>Rules:
		<ul>
			<li>If pattern is not in a specific config file, then the type will be used to pull from the filename_patterns.txt file. If this is not available, then 'date+individual' will be used.</li>
			<li>Derived Dir is the director</li>
		</ul>
	</div>
	<div id="mainFormSection">
		<!-- The file is not part of the form submission -->
		Select a file to derive the directory from <input type="file" name="file" id="file"><br />
	<form action="process" id="mainForm" method="post" >
		<div id="form-elements">
			<label for="dir">Derived Dir</label>: <input type="text" name="dir" id="dir"><br />
			<label for="use_type">Type</label>: <input type="text" name="use_type" id="type"><br />
			<label for="use_pattern">Pattern</label>: <input type="text" name="use_pattern" id="pattern" value="date+individual"><br />
			<label for="use_individual">Individual</label>: <input type="text" name="use_individual" id="individual"><br />
			<label for="use_medical">Medical</label>: <input type="text" name="use_medical" id="medical"><br />
		</div>
		<input type="submit" name="submit" value="Modify Scan Runner Script"><br />
	</form>
	<button id="add-new-pair" class="main_btn">Add new Key/Value pair</button>
	<button id="perm-save-config" class="main_btn">Permanently save this config</button>
	</div>
	<div id="pre-loaded" >
	</div>
</div>
</body>
</html>
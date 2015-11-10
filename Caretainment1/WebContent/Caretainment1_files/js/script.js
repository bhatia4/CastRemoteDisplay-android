window.onload = function() {
//	setTimeout( function () { handleMessage("select-entertainment"); }, 3500 );
//	setTimeout( function () { handleMessage("expand-entertainment"); }, 1500 );
//	setTimeout( function () { handleMessage("engage-entertainment-item-2"); },4500 );
//	setTimeout( function () { handleMessage("select-entertainment-item-2"); }, 5500 );
//	setTimeout( function () { handleMessage("select-health-item-3"); }, 6500 );
//	setTimeout( function () { handleMessage("select-activities-item-4"); }, 7500 );
//	setTimeout( function () { handleMessage("select"); }, 8500 );
	
	cast.receiver.logger.setLevelValue(0);
    window.castReceiverManager = cast.receiver.CastReceiverManager.getInstance();
    console.log('Starting Receiver Manager');
    
    // handler for the 'ready' event
    castReceiverManager.onReady = function(event) {
      console.log('Received Ready event: ' + JSON.stringify(event.data));
      window.castReceiverManager.setApplicationState("Application status is ready...");
    };
    
    // handler for 'senderconnected' event
    castReceiverManager.onSenderConnected = function(event) {
      console.log('Received Sender Connected event: ' + event.data);
      console.log(window.castReceiverManager.getSender(event.data).userAgent);
    };
    
    // handler for 'senderdisconnected' event
    castReceiverManager.onSenderDisconnected = function(event) {
      console.log('Received Sender Disconnected event: ' + event.data);
      if (window.castReceiverManager.getSenders().length == 0) {
        window.close();
      }
    };
    
    // handler for 'systemvolumechanged' event
    castReceiverManager.onSystemVolumeChanged = function(event) {
      console.log('Received System Volume Changed event: ' + event.data['level'] + ' ' +
          event.data['muted']);
    };
    // create a CastMessageBus to handle messages for a custom namespace
    window.messageBus =
      window.castReceiverManager.getCastMessageBus(
          'urn:x-cast:com.google.cast.sample.helloworld');
    // handler for the CastMessageBus message event
    window.messageBus.onMessage = function(event) {
      console.log('Message [' + event.senderId + ']: ' + event.data);
      // display the message from the sender
      handleMessage(event.data);
      // inform all senders on the CastMessageBus of the incoming message event
      // sender message listener will be invoked
      window.messageBus.send(event.senderId, event.data);
    };
    // initialize the CastReceiverManager with an application status message
    window.castReceiverManager.start({statusText: "Application is starting"});
    console.log('Receiver Manager started');
};
  
// utility function to display the text message in the input field
function handleMessage(message) {
    console.log(message);
    if (message=="select-entertainment" || message=="select-health" || message=="select-activities")
    	selectMainBlock(message.split("-")[1]);
    else
    if (message=="select-entertainment-item-1" || message=="select-entertainment-item-2"
    	|| message=="select-health-item-1" || message=="select-health-item-2" || message=="select-health-item-3" || message=="select-health-item-4" || message=="select-health-item-5"
    	|| message=="select-activities-item-1" || message=="select-activities-item-2" || message=="select-activities-item-3" || message=="select-activities-item-4")
    	selectItem(message);
    else
    if (message=="select")
    	selectMainBlock();
    else
    if (message=="expand-entertainment" || message=="expand-health" || message=="expand-activities")
    {
    	selectItem();
    	expandMainBlock(message.split("-")[1]);
    }
    else
    if (message=="engage-entertainment-item-1")
    {
    	$("#mainblock").addClass("hide");
    	$("#entertainment-video-1").get(0).load();
    	$("#entertainment-video-1").addClass("hide");
    	
    	var message_split = message.split("-");
    	var video_index = message_split[message_split.length-1];
    	var video_tag_id = message_split[1]+"-video-"+video_index;
    	
    	$("#videoblock").removeClass("hide");
    	$("#"+video_tag_id).removeClass("hide");
    	$("#"+video_tag_id).get(0).play();
    }
    else
    if (message=="expand")
    	expandMainBlock();
    else
    	document.getElementById("message").innerHTML=message;
    window.castReceiverManager.setApplicationState(message);
};

function selectMainBlock(id)
{
	$("#entertainment").removeClass("selected");
	$("#health").removeClass("selected");
	$("#activities").removeClass("selected");
	
	if (id !=null)
	{
		switch (id) {
		  case "entertainment": case "health": case "activities":
			$("#"+id).addClass("selected");
		    break;
		}
	}
}

function selectItem(id)
{
	if (id !=null)
	{
		var section = id.split("-")[1];
		
		for(var itemNum = 1; ($("#"+section+"-item-"+itemNum).length); itemNum++)
			$("#"+section+"-item-"+itemNum).removeClass("selected");
		
		$("#"+id.substring(id.indexOf("-")+1)).addClass("selected");
	}
	else
	{
		$("#entertainment-item-1").removeClass("selected");
		$("#entertainment-item-2").removeClass("selected");
		$("#health-item-1").removeClass("selected"); 
		$("#health-item-2").removeClass("selected"); 
		$("#health-item-3").removeClass("selected");
		$("#health-item-4").removeClass("selected"); 
		$("#health-item-5").removeClass("selected");
		$("#activities-item-1").removeClass("selected");
		$("#activities-item-2").removeClass("selected");
		$("#activities-item-3").removeClass("selected");
		$("#activities-item-4").removeClass("selected");
	}
}

function expandMainBlock(id)
{
	$("#entertainment-1").css("display","block");
	$("#entertainment-2").css("display","none");
	$("#health-1").css("display","block");
	$("#health-2").css("display","none");
	$("#activities-1").css("display","block");
	$("#activities-2").css("display","none");

	$("#entertainment").removeClass("empty");
	$("#health").removeClass("empty");
	$("#activities").removeClass("empty");
	$("#entertainment").removeClass("rowspanned");
	$("#health").removeClass("rowspanned");
	$("#activities").removeClass("rowspanned");
	$("#entertainment").removeClass("colspanned");
	$("#health").removeClass("colspanned");

	$("#row1").removeClass("rowspanned");
	$("#row2").removeClass("empty");

	if (id !=null)
	{
		switch (id) {
		  case "entertainment":
				$("#"+id).addClass("colspanned");
				$("#"+id).addClass("rowspanned");
				$("#"+id+"-1").css("display","none");
				$("#"+id+"-2").css("display","block");
				$("#"+id).addClass("selected");
				$("#health").addClass("empty");
				$("#activities").addClass("empty");
				$("#row1").addClass("rowspanned");
				$("#row2").addClass("empty");
			    break;

		  case "health": 
				$("#"+id).addClass("colspanned");
				$("#"+id).addClass("rowspanned");
				$("#"+id+"-1").css("display","none");
				$("#"+id+"-2").css("display","block");
				$("#"+id).addClass("selected");
				$("#entertainment").addClass("empty");
				$("#activities").addClass("empty");
			    break;

		  case "activities":
				$("#"+id).addClass("colspanned");
				$("#"+id).addClass("rowspanned");
				$("#"+id+"-1").css("display","none");
				$("#"+id+"-2").css("display","block");
				$("#"+id).addClass("selected");
				$("#entertainment").addClass("empty");
				$("#health").addClass("empty");
			    break;
		}
	}
	selectMainBlock();
}
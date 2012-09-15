<script type="text/javascript">
	var sHolder = {};
	var snd = new Audio("${g.createLink(uri: '/sound/message.ogg')}");
	var uUUID = "${chatUser.uuid}";
	var client = new Faye.Client('${ps.clientUrl(endpoint: 'chat')}');
		
	$(document).ready(function() {		
		initRoomList();

		// public subscribe
		
		client.subscribe('/public', function(data) {				
			var broadcast = JSON.parse(data);
			console.log(broadcast)
			if(broadcast.command == 'updateRooms'){
				$("#availableRooms").html(broadcast.data.html);
				$("span#userCount").text(broadcast.data.userCount + " Online")
				initRoomList();
			}else if(broadcast.command == 'removeRoom'){
				removeRoomTab(broadcast.data.room)					
			}else if(broadcast.command == 'nameChanged'){
				var uid = broadcast.data.uuid;
				var name = broadcast.data.name;
				$("span.userName-" + uid).text(name);
			}else if(broadcast.command == 'colorChanged'){
				var uid = broadcast.data.uuid;
				var color = broadcast.data.color;
				$(".message-" + uid).css("color", "#" + color);
			}							
		});

		// private subscribe
		client.subscribe('/session/'+uUUID, function(data) {				
			var broadcast = JSON.parse(data);
			var command = broadcast.command;
			data = broadcast.data;									
			if(command == 'info'){
				addMessage(data.room, data.message);
			}												
		});

		// colors
		$("a.changeColor").click(function(){
			chat("changeColor", {color: $(this).attr('rel')}, function(data){
			});
			return false;
		});

		// enter welcome room
		enterRoom('${welcomeRoom.uuid}');			
	});
	
	function initRoomList() {		
		$("a.roomLink").unbind().click(function() {
			enterRoom($(this).attr("rel"));
			return false;
		});

		// write message event
		hookReturn("#addRoom", function(obj){
			chat('createRoom', {roomName: $(obj).val()}, function(data){
				if(data.success){
					enterRoom(data.room.uuid);
				}
				$(obj).val('');					
			});								
			return false;
		});
	}

	function chat(action, data, callback){
		$.post('${g.createLink(uri: "/chat/")}' + action, data, callback);
	}

	function hookReturn(selector, callback) {
		$(selector).keypress(function(e) {
			if (e.which == 13) {
				return callback(this);
			}
			return true;
		});
	}

	function leaveRoom(uuid, callback) {
		chat('leaveRoom', {
			room : uuid
		}, function(data) {
			if (data.success) {
				removeRoomTab(uuid);
				sHolder[uuid].cancel();
				if (callback != null) {
					callback();
				}
			}
		});
	}

	function removeRoomTab(uuid) {
		$("#openRooms a[href=#openRoom-" + uuid + "]").parent("li").remove();
		$("#openRoom-" + uuid).remove();
		$("#openRooms a:first").tab("show");
	}

	function enterRoom(uuid) {
		var orl = $("a[href=#openRoom-" + uuid + "]");
		if (orl.length > 0) {
			orl.tab("show");
			return;
		}

		chat(
				'enterRoom',
				{
					room : uuid
				},
				function(data) {
					// add room html
					var html = data.roomHtml;
					var roomSelector = "#openRoom-" + uuid;
					$("#openRooms > ul").append($(html).find('li'));
					$("#openRooms > div").append($(html).find("div.tab-pane"));
					$("a[href=#openRoom-" + uuid + "]").tab("show");

					$("#openRoom-" + uuid + " .users").html(data.usersHtml);

					// subscribe					
					var subscription = client.subscribe('/room/' + uuid,
							function(data) {
								var json = JSON.parse(data);
								var data = json.data;
								var command = json.command;
								console.log(json);
								if (command == 'info') {
									addMessage(uuid, data.message);
									snd.play();
								} else if (command == 'message') {
									addMessage(uuid, data.message);
									if (data.sender.uuid != uUUID) {
										snd.play();
									}
								} else if (command == 'updateUsers') {
									var roomUUID = data.room;
									$("#openRoom-" + roomUUID + " .users")
											.html(data.html);
								}
							});

					sHolder[uuid] = subscription;

					$(roomSelector + " form.sendForm textarea.message")
							.keyup(
									function(e) {
										while ($(this).outerHeight() < this.scrollHeight
												+ parseFloat($(this).css(
														"borderTopWidth"))
												+ parseFloat($(this).css(
														"borderBottomWidth"))) {
											$(this)
													.height(
															$(this).height() + 1);
										}
										;
									});

					// write message event
					hookReturn(
							roomSelector + " form.sendForm textarea.message",
							function(obj) {
								if ($(obj).parent("form").hasClass("sendRoom")) {
									var msg = $(obj).val();
									if (msg != '') {
										chat('sendMessage', {
											room : uuid,
											message : msg
										});
										$(obj).val('');
									}
								}
								return false;
							});

					// close room hook
					$("a[href=#openRoom-" + uuid + "] span.closeRoom").click(
							function() {
								leaveRoom(uuid);
								return false;
							});

					// delete room hook
					$(roomSelector + " a.deleteRoom").click(function() {
						leaveRoom(uuid, function() {
							chat("deleteRoom", {
								room : uuid
							}, function(data) {
								if (data.success) {
								}
							});
						})

						return false;
					});

					$(roomSelector + " input.userNameInput").blur(
							function() {
								chat("changeName", {
									name : $(this).val()
								});
								$(this).hide();
								$(roomSelector + " span.changeName").val(
										$(this).val()).show();
							});
					hookReturn(roomSelector + " input.userNameInput", function(
							obj) {
						chat("changeName", {
							name : $(obj).val()
						});
						$(obj).hide();
						$(roomSelector + " span.changeName").val($(obj).val())
								.show();
						return false;
					});

					$(roomSelector + " form span.changeName").click(
							function() {
								$(this).hide();
								$(this).parent().find("input.userNameInput")
										.show().focus();
								return false;
							});

					chat('onRoomEntered', {
						room : uuid
					});
				});
	}

	function addMessage(uuid, message) {
		var id = "#messages-" + uuid;
		$(id).append(message);
		$(id).scrollTop($(id)[0].scrollHeight);
	}
</script>
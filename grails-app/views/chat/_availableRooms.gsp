<h5>Available Rooms <span class="pull-right badge badge-success" id="userCount">1234 Online</span></h5>
<div class="well">
	<ul class="nav navList">
		<g:each in="${rooms }" var="room">
			<li><a class="roomLink" id="openRoom-${room.uuid }"
				rel="${room.uuid }" href="#"> ${room.name } (${room.users?.size() ?: 0})
			</a></li>
		</g:each>		
	</ul>
	<div class="input-prepend">
		<span class="add-on"><i class="icon icon-plus-sign"></i></span> 
		<input name="roomName" id="addRoom" class="span2" id="prependedInput"
			size="16" type="text" placeholder="Roomname">
	</div>
</div>
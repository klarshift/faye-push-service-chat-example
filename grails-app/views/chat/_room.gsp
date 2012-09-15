<ul class="nav nav-tabs">
	<li><a href="#openRoom-${room.uuid }" data-toggle="tab"> ${room.name }
			<span class="closeRoom"><i class="icon icon-remove-sign"></i></span>
	</a></li>
</ul>

<div class="tab-content">

	<div class="tab-pane" id="openRoom-${room.uuid }">
		<div class="pull-right">
			<div class="controls">
				<g:if test="${room.moderator == chatUser }">
					<g:link class="deleteRoom btn btn-success btn-small" href="#">Delete Room</g:link>
				</g:if>
			</div>

			<div class="clear"></div>

			<div class="users pull-right"></div>
		</div>


		<div class="messages roomMessages" id="messages-${room.uuid }">

		</div>

		<g:form class="sendForm sendRoom horizontal">
			<div>
				<i class="icon icon-user"></i>
				<input type="text"
					class="userNameInput userNameInput-${chatUser.uuid }" value="${chatUser.name }" />
					
					<span class="changeName userName userName-${chatUser.uuid }"> ${chatUser.name }
				</span>
			</div>
			<g:textArea name="message" id="messageInput-${room.uuid }" class="message" />
		</g:form>
	</div>

</div>
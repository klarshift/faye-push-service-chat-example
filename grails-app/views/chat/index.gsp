<!doctype html>
<html>
<head>
<meta name="layout" content="main" />
</head>
<body>
	<div class="row">
		<ps:ifOnline endpoint="chat">
			<!-- open rooms container -->
			<div class="span9">
				<div id="openRooms" class="tabbable">
					<ul class="nav nav-tabs"></ul>
					<div class="tab-content"></div>
				</div>
			</div>


			<div class="span3">

				<!-- available rooms container -->
				<div id="availableRooms">
					<g:render template="availableRooms" model="${[rooms: chatRooms] }" />
				</div>

				<h5>Settings</h5>
				<div id="settings">
					<div class="well">
						<g:each in="${chatColors}" var="color">
							<a href="#" rel="${color }" class="changeColor" style="display: block; float: left; width: 16px; height: 16px; margin-right: 10px; background: #${color}"></a>
						</g:each>
						<div class="clear"></div>
					</div>
				</div>
			</div>
		</ps:ifOnline>
		<ps:ifOffline endpoint="chat">
			<div class="span12">
				<div class="well">
					Sorry, but currently this service is not available.
				</div>
			</div>
			
		</ps:ifOffline>
	</div>

	<ps:ifOnline endpoint="chat">
		<ps:init endpoint="chat" />
		<g:render template="jsClient" />
	</ps:ifOnline>
</body>
</html>

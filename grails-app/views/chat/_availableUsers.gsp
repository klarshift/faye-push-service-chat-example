
<ul class="nav navList">
	<li class="nav-header">Users</li>
	<g:each in="${users }" var="user">
		<li><g:if test="${room.moderator?.uuid == user.uuid }">
				<i class="icon icon-star"></i>
			</g:if> <g:else>
				<i class="icon icon-user"></i>
			</g:else> <span class="userName userName-${ user.uuid}">
			${user.name }
		</span></li>
	</g:each>
</ul>

package chat

import grails.gsp.PageRenderer

import com.klarshift.grails.plugins.pushservice.FayeEndpoint


/**
 * chat service
 * @author timo
 *
 */
class ChatService {
	PageRenderer groovyPageRenderer
	Random random = new Random()
	def colors = ['113F8C', '61AE24', 'E54028', 'F18D05', '616161']
	def pushService
	
	public void cleanup(){
		long now = new Date().getTime()-1000*15
		Date tDate = new Date(now)
		def inactiveUsers = ChatUser.withCriteria {
			lt('lastAction', tDate)
		}
		
		inactiveUsers.each{ChatUser u -> kickUser(u)}
	}
	
	private void kickUser(ChatUser user){
		def joinedRooms = getJoinedRooms(user)
		joinedRooms.each{ ChatRoom r ->
			roomInfo(r, "User `$user` kicked out due no action ...")
			leaveRoom(user, r)
			
			if(r.moderator == user){
				deleteRoom(user, r)
			}
		}
		
		deleteUser(user)		
	}
	
	private void deleteUser(ChatUser user){
		privatePublish(user, "sessionExpired", [:])
		user.delete(flush: true)
	}
	
	private void triggerUserAction(ChatUser user){
		user.lastAction = new Date()
		user.save(flush: true)
	}
	
	private void triggerRoomAction(ChatRoom room){
		room.lastAction = new Date()
		room.save(flush: true)
	}

	/**
	 * publish something to chat channel
	 * @param channel
	 * @param data
	 */
	private void publish(String channel, data){		
		pushService.publish("chat", channel, data)	
	}
			
	private void broadcast(String command, data){
		publish("/public", [command: command, data: data])
	}
	
	private void roomBroadcast(String command, ChatRoom room, data){
		publish(getChannelNameForRoom(room), [command: command, data: data])
	}
	
	public void roomInfo(ChatRoom room, String message){
		message = groovyPageRenderer.render(template: '/chat/systemMessage', model: [message: message])
		roomBroadcast("info", room, [message: message])
	}
	
	public void privateInfo(ChatUser user, ChatRoom room, String message){
		message = groovyPageRenderer.render(template: '/chat/systemMessage', model: [message: message])
		privatePublish(user, "info", [message: message, room: room.uuid])
	}
	
	public void privatePublish(ChatUser user, String command, data){
		publish("/session/"+user.uuid, [command: command, data: data])		
	}
	
	public getAvailableRooms(){
		ChatRoom.findAllWhere([open: true])
	}
	
	public void updateRooms(){
		String roomsHtml = groovyPageRenderer.render(template: '/chat/availableRooms', model: [rooms: availableRooms])
		broadcast('updateRooms', [html: roomsHtml, userCount: ChatUser.count()])
	}
	
	public void updateUsers(ChatRoom room){
		def users = room.users?.collect{ ChatUser user ->
			return [uuid: user.uuid, name: user.name]
		} ?: []
		String html = groovyPageRenderer.render(template: '/chat/availableUsers', model: [room: room, users: room.users])
		roomBroadcast("updateUsers", room, [room: room.uuid, users: users, html: html])
	}
	
	/**
	 * send a message
	 * @param user
	 * @param room
	 * @param message
	 */
	public boolean sendMessage(ChatUser sender, ChatRoom room, String message){
		if(sender == null || room == null){
			return false
		}
		
		message = cleanString(message)
		
		if(message.length() > 0){
			triggerUserAction(sender)
			message = groovyPageRenderer.render(template: '/chat/message', model: [user: sender, message: message])
			roomBroadcast("message", room, [sender: [name: sender.name, uuid: sender.uuid], message: message])
		}		
			
			
		return true
	}	
	
	/**
	 * get all rooms a given user has joined
	 * @param user
	 * @return
	 */
	public List<ChatRoom> getJoinedRooms(ChatUser user){
		return ChatRoom.createCriteria().list{
			users{
				eq('id', user.id)
			}			
		}
	}
	
	/**
	 * change user name
	 * @param user
	 * @param name
	 * @return
	 */
	public boolean changeName(ChatUser user, String name){
		String oldName = user.name
		name = cleanString(name)
		if(name.size() > 0 && user.name != name){
			triggerUserAction(user)
			
			user.name = name
			user.save(flush: true)
			
			// make an info for every room
			// the user has joined
			getJoinedRooms(user).each{ ChatRoom r ->
				roomInfo(r, "User `$oldName` has changed its name to `$name`")
			}
			
			// public name change notification
			broadcast("nameChanged", [uuid: user.uuid, name: name])
		}else{
			return false
		}
	}
	
	/**
	 * change users color
	 * @param user
	 * @param color
	 * @return
	 */
	public boolean changeColor(ChatUser user, String color){
		color = color.replace('#', '')
		if(user.color == color)return false
		user.color = color
		user.save(flush: true)
		
		triggerUserAction(user)
		
		// 
		getJoinedRooms(user).each{ ChatRoom r ->
			roomInfo(r, "User `${user.name}` has changed its color")
		}
		
		// public name change notification
		broadcast("colorChanged", [uuid: user.uuid, color: color])
		
		return true
	}
	
	/**
	 * clean a string
	 * remove html tags and trim
	 * @param s
	 * @return
	 */
	public String cleanString(String s){ s.replaceAll("<(.|\n)*?>", '').trim() }
	
	/**
	 * get room by uuid
	 * @param uuid
	 * @return
	 */
	public ChatRoom getRoom(String uuid){
		return ChatRoom.findWhere([uuid: uuid])
	}
	
	
	/**
	 * create a new chat user
	 * @param sessionId
	 * @return
	 */
	public ChatUser createUser(String sessionId){
		String name = "user_" + Math.abs(random.nextInt())
		String color = colors[random.nextInt(colors.size()-1)]
		return new ChatUser(sessionId: sessionId, name: name, uuid: UUID.randomUUID().toString(), color: color)
	}
	
	/**
	 * get the welcome room
	 * @return
	 */
	public ChatRoom getWelcomeRoom(){
		return ChatRoom.findByName("Welcome")
	}
	
	/**
	 * create a chat room
	 * @param name
	 * @param moderator
	 * @return
	 */
	public ChatRoom createRoom(String name, ChatUser moderator){
		// create the room
		ChatRoom room = new ChatRoom(name: name, uuid: UUID.randomUUID().toString(), moderator: moderator)
		
		// create the channel
		if(moderator)
			triggerUserAction(moderator)
		
		if(room.save(flush: true)){
			updateRooms()
			return room
		}
		
		return null
	}
	
	/**
	 * get a rooms channel name
	 * @param room
	 * @return
	 */
	private String getChannelNameForRoom(room){
		if(room instanceof ChatRoom)
			return "/room/${room.uuid}"
		else return "/room/${room}"
	}
	
	/**
	 * user enters a room
	 * @param user
	 * @param room
	 */
	public boolean enterRoom(ChatUser user, ChatRoom room){
		// add user to room
		room.addToUsers(user).save(flush: true, failOnError: true)		
		
		// inform room about new user
		roomInfo(room, "User $user has entered.")
		
		triggerUserAction(user)
		updateRooms()
		updateUsers(room)
		
		return true
	}
	
	/**
	 * enter a room
	 * @param user
	 * @param roomUUID
	 * @return
	 */
	public boolean enterRoom(ChatUser user, String roomUUID){
		// get room
		ChatRoom room = ChatRoom.findWhere([uuid: roomUUID])
		if(room){
			return enterRoom(user, room)			
		}
		return false
	}
	
	/**
	 * leave a room
	 * @param user
	 * @param room
	 */
	public boolean leaveRoom(ChatUser user, ChatRoom room){
		room.removeFromUsers(user).save(flush: true, failOnError: true)		
		roomInfo(room, "User $user has left.")
		updateRooms()
		return true
	}
	
	
	
	public boolean leaveRoom(ChatUser user, String roomUUID){
		// get room
		ChatRoom room = ChatRoom.findWhere([uuid: roomUUID])
		if(room){
			return leaveRoom(user, room)
		}
		return false
	}
	
	public boolean deleteRoom(ChatUser user, ChatRoom room){
		// only the moderator of the room is allowed
		// to delete the room
		if(user != room.moderator){
			return false
		}
		
		// remove references from room
		def users = room.users
		users.each {ChatUser u -> room.removeFromUsers(u)}	
		room.moderator = null
				
		// delete
		room.delete(flush: true, failOnError: true)
		broadcast("removeRoom", [room: room.uuid])
		updateRooms()
		return true		
	}
}

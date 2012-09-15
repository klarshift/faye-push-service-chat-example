package chat

import grails.converters.JSON
import grails.gsp.PageRenderer

/**
 * chat controller
 * @author timo
 *
 */
class ChatController {
	PageRenderer groovyPageRenderer
	def chatService
	
	/**
	 * chat index
	 * @return
	 */
    def index() {	
		[welcomeRoom: chatService.getWelcomeRoom(), chatRooms: chatService.getAvailableRooms(), chatColors: chatService.colors]
	}
	
	/**
	 * enter a room
	 * @return
	 */
	def enterRoom(){
		ChatRoom room = chatService.getRoom(params.room)
		if(chatService.enterRoom(request.chatUser, room)){			
			String roomHtml = groovyPageRenderer.render(template: '/chat/room', model: [room: room, chatUser: request.chatUser ])
			String usersHtml = groovyPageRenderer.render(template: '/chat/availableUsers', model: [room: room, users: room.users ])
			return render([success: true, room: room, roomHtml: roomHtml, usersHtml: usersHtml] as JSON)
		}else{
			return render([success: false] as JSON)
		}
	}
	
	/**
	 * leave a room
	 * @return
	 */
	def leaveRoom(){
		ChatRoom room = chatService.getRoom(params.room)
		if(chatService.leaveRoom(request.chatUser, room)){			
			return render([success: true, room: room] as JSON)
		}else{
			return render([success: false] as JSON)
		}
	}
	
	/**
	 * on enter callback
	 * @return
	 */
	def onRoomEntered(){
		ChatRoom room = chatService.getRoom(params.room)
		if(room){
			String info = "Welcome to room `$room`"
			if(room.moderator){
				info += " (moderated by `${room.moderator}`)"
			}
			chatService.privateInfo(request.chatUser, room, info)
			return render([success: true] as JSON)
		}
		return render([success: false] as JSON)				
	}
	
	/**
	 * delete a room
	 * @return
	 */
	def deleteRoom(){
		ChatRoom room = chatService.getRoom(params.room)
		if(chatService.deleteRoom(request.chatUser, room)){
			return render([success: true] as JSON)
		}else{
			return render([success: false] as JSON)		
		}
	}
	
	/**
	 * create a room
	 * @return
	 */
	def createRoom(){
		ChatRoom room = chatService.createRoom(params.roomName, request.chatUser)
		if(room){
			if(chatService.enterRoom(request.chatUser, room)){
				String html = groovyPageRenderer.render(template: '/chat/room', model: [room: room, chatUser: request.chatUser ])
				return render([success: true, room: room, html: html] as JSON)
			}
		}
		return render([success: false] as JSON)
	}
	
	/**
	 * change user name
	 * @return
	 */
	def changeName(){
		if(chatService.changeName(request.chatUser, params.name)){
			return render([success: true] as JSON)			
		}else{
			return render([success: false] as JSON)
		}
	}
	
	/**
	 * change user color
	 * @return
	 */
	def changeColor(){
		if(chatService.changeColor(request.chatUser, params.color)){
			return render([success: true] as JSON)
		}else{
			return render([success: false] as JSON)
		}
	}
	
	/**
	 * send message
	 * @return
	 */
	def sendMessage(){		
		// send to room
		if(params.room){
			ChatRoom room = ChatRoom.findWhere([uuid: params.room])			
			chatService.sendMessage(request.chatUser, room, params.message)
			return render([success: true] as JSON)
		}
		return render([success: false] as JSON)
	}
	
}

import chat.ChatRoom

import com.klarshift.grails.plugins.pushservice.FayeChannel
import com.klarshift.grails.plugins.pushservice.FayeEndpoint

class BootStrap {
	def pushService
	def chatService

    def init = { servletContext ->
		// create faye services
		FayeEndpoint chatEndpoint = pushService.createEndpoint("chat", "http://localhost:8000")
		FayeChannel publicChannel = pushService.createChannel(chatEndpoint, '/public')		
		chatEndpoint.save(flush: true, failOnError: true) 
		
		// create chat rooms
		["Welcome"].each{ String roomName ->
			ChatRoom room = chatService.createRoom(roomName, null)		
			room.save(flush: true, failOnError: true)
		}
    }
    def destroy = {
    }
}

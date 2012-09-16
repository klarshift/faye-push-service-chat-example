import com.klarshift.grails.plugins.pushservice.FayeEndpoint

class BootStrap {
	def pushService
	def chatService
	def grailsApplication

    def init = { servletContext ->
		// create chat endpoint
		FayeEndpoint chatEndpoint = pushService.createEndpoint("chat", grailsApplication.config.chat.pushHost)
				
		// create chat room
		chatService.createRoom("Welcome", null)				
    }
    def destroy = {
    }
}

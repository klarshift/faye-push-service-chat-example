package chat

class ChatFilters {
	def chatService

	def filters = {		
		chatUserFilter(controller: '*', action: '*'){
			before = {
				ChatUser user = ChatUser.findBySessionId(session.id)
				if(!user){
					user = chatService.createUser(session.id)	
					user.save(flush: true, failOnError: true)
					
					chatService.enterRoom(user, chatService.getWelcomeRoom())
				}
				
				request.chatUser = user 
			}
			after = { Map model ->
				if(model)
					model.chatUser = request.chatUser
			}
			afterView = { Exception e ->
			}
		}
	}
}

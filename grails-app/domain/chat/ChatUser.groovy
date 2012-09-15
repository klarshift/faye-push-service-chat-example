package chat

class ChatUser {
	String uuid
	String name
	String sessionId	
	String color

    static constraints = {
		name unique: true
		color nullable: true
    }
	
	public String toString(){
		return name
	}
}

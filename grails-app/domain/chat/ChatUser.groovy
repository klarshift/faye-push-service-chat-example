package chat

class ChatUser {
	String uuid
	String name
	String sessionId	
	String color
	Date lastAction

    static constraints = {
		name unique: true
		color nullable: true
		lastAction nullable: true
    }
	
	public String toString(){
		return name
	}
}

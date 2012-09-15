package chat

/**
 * char room
 * @author timo
 *
 */
class ChatRoom {
	String uuid
	String name
	Integer maxUsers = 0
	Boolean open = true
	
	static belongsTo = [moderator : ChatUser]
	static hasMany = [users : ChatUser]

    static constraints = {
		name unique: true
		moderator nullable: true
    }
	
	public String toString(){
		return name
	}
}

package iryrwarosh;

public abstract class Message {
	public World world;
	
	private String text;
	public String text(){ return text; }
	
	public Message(World world, String text){
		this.world = world;
		this.text = text;
	}

	abstract public boolean involves(Creature player);
}

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

	protected static String addArticle(String article, String word){
		if (Character.isUpperCase(word.charAt(0)))
			return word;
		else
			return article + " " + word;
	}
}

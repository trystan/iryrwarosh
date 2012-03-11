package iryrwarosh;

public class Evaded extends Message {
	public Creature attacker;
	public Creature evader;
	
	public Evaded(Creature attacker, Creature evader){
		super(evader.glyph() + " evaded " + attacker.glyph());
		this.attacker = attacker;
		this.evader = evader;
	}

}

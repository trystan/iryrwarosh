package iryrwarosh;

public class Attacked extends Message {
	public Creature attacker;
	public Creature attacked;
	
	public Attacked(World world, Creature attacker, Creature attacked, String specialType){
		super(world, addArticle("The", attacker.name()) + " attacked " + addArticle("the", attacked.name()) + (specialType==null ? "" : " " + specialType) );
		this.attacker = attacker;
		this.attacked = attacked;
	}

	@Override
	public boolean involves(Creature player) {
		return attacker == player || attacked == player;
	}
}

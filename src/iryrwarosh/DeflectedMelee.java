package iryrwarosh;

public class DeflectedMelee extends Message {
	public Creature attacker;
	public Creature attacked;
	
	public DeflectedMelee(World world, Creature attacker, Creature attacked){
		super(world, addArticle("The", attacked.name()) + " deflects the attack.");
		this.attacker = attacker;
		this.attacked = attacked;
	}

	@Override
	public boolean involves(Creature player) {
		return attacker == player || attacked == player;
	}

}

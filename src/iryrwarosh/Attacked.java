package iryrwarosh;

public class Attacked extends Message {
	public Creature attacker;
	public Creature attacked;
	public boolean isSpecial;
	
	public Attacked(World world, Creature attacker, Creature attacked, String specialType){
		super(world, "The " + attacker.name() + (specialType==null ? "" : " " + specialType) + " attacked the " + attacked.name());
		this.attacker = attacker;
		this.attacked = attacked;
		this.isSpecial = specialType != null;
	}

	@Override
	public boolean involves(Creature player) {
		return attacker == player || attacked == player;
	}
}

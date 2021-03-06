package iryrwarosh;

public enum Trait {
	WALKER("walker"), SWIMMER("swimmer"), FLIER("flier"), JUMPER("jumper"),
	EXTRA_HP("healty"), STRONG_ATTACK("strong"), EXTRA_EVADE("evasive"),EXTRA_DEFENSE("shelled"),
	DOUBLE_ATTACK("double attack"), DOUBLE_MOVE("double move"),
	SPIKED("spiked"), REGENERATES("regenerates"), POISONOUS("poisonous"), CAMOUFLAGED("camouflaged"), 
	AGGRESSIVE("aggressive"), HIDER("hider"), TERRITORIAL("territorial"), ROCK_SPITTER("rock spitter"),
	SOCIAL("social"), LOOTLESS("lootless"),
	EVADE_ATTACK("evasive attacker"), COUNTER_ATTACK("counter attack"),
	REACH_ATTACK("long reach"), DETECT_CAMOUFLAGED("perceptive"), 
	DEFLECT_RANGED("deflects projectiles"), DEFLECT_MELEE("blocks melee"),
	HUNTER("hunter"), MYSTERIOUS("mysterious"), KNOCKBACK("knockback"), SLOWING_ATTACK("slowing attack");

	private String description;
	public String description() { return description; }
	
	Trait(String description){
		this.description = description;
	}
	
	public static Trait getRandom() {
		return Trait.values()[(int)(Math.random() * Trait.values().length)];
	}
}

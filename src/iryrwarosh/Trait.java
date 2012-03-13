package iryrwarosh;

public enum Trait {
	WALKER("walker"), SWIMMER("swimmer"), FLYER("flier"),
	EXTRA_HP("healty"), STRONG_ATTACK("strong"), EXTRA_EVADE("evasive"),EXTRA_DEFENSE("shelled"),
	DOUBLE_ATTACK("double attack"), DOUBLE_MOVE("double move"),
	SPIKED("spiked"), REGENERATES("regenerates"), POISONOUS("poisonous"), CAMOUFLAGED("camouflaged"), 
	AGGRESSIVE("aggressive"), HIDER("hider"), TERRITORIAL("territorial"), ROCK_SPITTER("rock spitter"),
	SOCIAL("social"), FEARFUL("fearful"), LOOTLESS("lootless"),
	EVADE_ATTACK("evasive attacker"), CIRCLE_ATTACK("circular attacker"), COUNTER_ATTACK("counter attack"),
	REACH_ATTACK("long reach"), COMBO_ATTACK("combo attack"), DETECT_CAMOUFLAGED("perceptive"), 
	DEFLECT_RANGED("deflects"), HUNTER("hunter"), MYSTERIOUS("mysterious");

	private String description;
	public String description() { return description; }
	
	Trait(String description){
		this.description = description;
	}
	
	public static Trait getRandom() {
		return Trait.values()[(int)(Math.random() * Trait.values().length)];
	}
}

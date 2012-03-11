package iryrwarosh;

public enum CreatureTrait {
	WALKER("walker"), SWIMMER("swimmer"), FLYER("flyer"),
	EXTRA_HP("hp+"), EXTRA_ATTACK("attack+"), EXTRA_EVADE("evasion+"),
	DOUBLE_ATTACK("double attack"), DOUBLE_MOVE("double move"),
	SPIKED("spiked"), REGENERATES("regenerates"), POISONOUS("poisonous"), CAMOUFLAGED("camouflaged"), 
	AGGRESSIVE("aggresive"), HIDER("hider"), TERRITORIAL("territorial"), ROCK_SPITTER("rock spitter"),
	SOCIAL("social");

	private String description;
	public String description() { return description; }
	
	CreatureTrait(String description){
		this.description = description;
	}
	
	
	public static CreatureTrait getRandom() {
		CreatureTrait candidate = WALKER;
		
		while (candidate == WALKER || candidate == SWIMMER || candidate == FLYER){
			candidate = CreatureTrait.values()[(int)(Math.random() * CreatureTrait.values().length)];
		}
		
		return candidate;
	}
}

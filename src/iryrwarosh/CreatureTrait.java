package iryrwarosh;

public enum CreatureTrait {
	WALKER("walker"), SWIMMER("swimmer"), FLYER("flyer"),
	EXTRA_HP("hp+"), EXTRA_ATTACK("attack+"), EXTRA_DEFENSE("defense+"), EXTRA_EVADE("evasion+"),
	DOUBLE_ATTACK("double attack"), DOUBLE_MOVE("double move"),
	SPIKED("spiked"), REGENERATES("regenerates"), POISONOUS("poisonous"), CAMOUFLAGED("camouflaged");

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

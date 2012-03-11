package iryrwarosh;

public enum CreatureTrait {
	WALKER, SWIMMER, FLYER,
	EXTRA_HP, EXTRA_ATTACK, EXTRA_DEFENSE, EXTRA_EVADE,
	DOUBLE_ATTACK, DOUBLE_MOVE;

	
	
	
	public static CreatureTrait getRandom() {
		CreatureTrait candidate = WALKER;
		
		while (candidate == WALKER || candidate == SWIMMER || candidate == FLYER){
			candidate = CreatureTrait.values()[(int)(Math.random() * CreatureTrait.values().length)];
		}
		
		return candidate;
	}
}

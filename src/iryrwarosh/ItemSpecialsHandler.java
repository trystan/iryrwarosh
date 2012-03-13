package iryrwarosh;

public class ItemSpecialsHandler implements Handler {
	@Override
	public void handle(Message message) {
		if (Attacked.class.isAssignableFrom(message.getClass()))
			onAttacked((Attacked)message);
		
		if (Evaded.class.isAssignableFrom(message.getClass()))
			onEvaded((Evaded)message);
		
		if (Moved.class.isAssignableFrom(message.getClass()))
			onMoved((Moved)message);
	}

	private void onMoved(Moved m) {
		checkDistantAttacks(m);
	}

	private void checkDistantAttacks(Moved m) {
		for (Point p : m.creature.position.neighbors()){
			Creature other = m.world.creature(p.x, p.y);
			if (other != null 
					&& !m.creature.isFriend(other) 
					&& other.hasTrait(Trait.REACH_ATTACK) && Math.random() < 0.5){
				other.attack(m.world, m.creature, "with a long reach");
			}
		}
	}

	private void onEvaded(Evaded m) {
		checkEvadeAttack(m);
	}

	private void checkEvadeAttack(Evaded m) {
		if (m.evader.hasTrait(Trait.EVADE_ATTACK) && Math.random() < 0.5){
			m.evader.attack(m.world, m.attacker, "while evading");
		}
	}

	private void onAttacked(Attacked m) {
		if (m.isSpecial)
			return;
		
		checkComboAttack(m);
		checkCircleAttack(m);
		checkCounterAttack(m);
	}

	private void checkCounterAttack(Attacked m) {
		if (m.attacked.hasTrait(Trait.COUNTER_ATTACK) && Math.random() < 0.5) {
			m.attacked.attack(m.world, m.attacker, "with a counter attack");
		}
	}

	private void checkCircleAttack(Attacked m) {
		if (m.attacker.hasTrait(Trait.CIRCLE_ATTACK) && Math.random() < 0.5){
			for (Point p : m.attacker.position.neighbors()){
				Creature other = m.world.creature(p.x, p.y);
				
				if (other == null || other == m.attacked)
					continue;
				
				m.attacker.attack(m.world, other, "with a circle attack");
			}
		}
	}

	private void checkComboAttack(Attacked m) {
		if (m.attacker.hasTrait(Trait.COMBO_ATTACK) && Math.random() < 0.5 && m.attacked.hp() > 0) {
			m.attacker.attack(m.world, m.attacked, "with a combo attack");
		}
	}
}
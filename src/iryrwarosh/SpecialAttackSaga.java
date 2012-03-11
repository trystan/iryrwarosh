package iryrwarosh;

public class SpecialAttackSaga implements Handler {
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
					&& other.distantAttackPercent() > Math.random() * 100)
				other.attack(m.world, m.creature, "reach");
		}
	}

	private void onEvaded(Evaded m) {
		checkEvadeAttack(m);
	}

	private void checkEvadeAttack(Evaded m) {
		if (m.evader.evadeAttackPercent() > Math.random() * 100)
			m.evader.attack(m.world, m.attacker, "quicked");
	}

	private void onAttacked(Attacked m) {
		if (m.isSpecial)
			return;
		
		checkComboAttack(m);
		checkCircleAttack(m);
		checkFinishingAttack(m);
		checkCounterAttack(m);
	}

	private void checkCounterAttack(Attacked m) {
		if (m.attacked.counterAttackPercent() > Math.random() * 100)
			m.attacked.attack(m.world, m.attacker, "counter");
	}

	private void checkFinishingAttack(Attacked m) {
		if (m.attacker.finishingAttackPercent() > Math.random() * 100 && m.attacked.hp() > 0){
			if ((m.attacker.attack - m.attacked.defense) * 2 >= m.attacked.hp()){
				m.attacker.finishingKill(m.world, m.attacked);
			}
		}
	}

	private void checkCircleAttack(Attacked m) {
		if (m.attacker.circleAttackPercent() > Math.random() * 100){
			for (Point p : m.attacker.position.neighbors()){
				Creature other = m.world.creature(p.x, p.y);
				
				if (other == null || other == m.attacked)
					continue;
				
				m.attacker.attack(m.world, other, "circle");
			}
		}
	}

	private void checkComboAttack(Attacked m) {
		if (m.attacker.comboAttackPercent() > Math.random() * 100 && m.attacked.hp() > 0)
			m.attacker.attack(m.world, m.attacked, "combo");
	}
}
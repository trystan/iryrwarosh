package iryrwarosh;

public class ItemSpecialsSaga implements Handler {
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
					&& !m.creature.isFriendlyTo(other) 
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
		
		checkKnockbackAttack(m);
		checkCounterAttack(m);
	}

	private void checkCounterAttack(Attacked m) {
		if (m.attacked.hasTrait(Trait.COUNTER_ATTACK) && Math.random() < 0.5) {
			m.attacked.attack(m.world, m.attacker, "with a counter attack");
		}
	}

	private void checkKnockbackAttack(Attacked m) {
		if (m.attacker.hasTrait(Trait.KNOCKBACK)){
			int dx = m.attacked.position.x - m.attacker.position.x;
			int dy = m.attacked.position.y - m.attacker.position.y;
			if (m.attacked.canEnter(m.world.tile(m.attacker.position.x+dx, m.attacker.position.y+dy))){
				m.attacked.position.x += dx;
				m.attacked.position.y += dy;
			}
		}
	}
}
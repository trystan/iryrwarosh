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
					&& other.hasTrait(Trait.REACH_ATTACK) && Math.random() < 0.75){
				other.attack(m.world, m.creature, "with it's long reach");
			}
		}
	}

	private void onEvaded(Evaded m) {
		checkEvadeAttack(m);
	}

	private void checkEvadeAttack(Evaded m) {
		if (m.evader.hasTrait(Trait.EVADE_ATTACK) && Math.random() < 0.75){
			m.evader.attack(m.world, m.attacker, "while evading");
		}
	}

	private void onAttacked(Attacked m) {
		checkKnockbackAttack(m);
		checkCounterAttack(m);
	}

	private void checkCounterAttack(Attacked m) {
		if (m.attacked.hasTrait(Trait.COUNTER_ATTACK) && m.attacked.hearts() > 0 && Math.random() < 0.75) {
			m.attacked.attack(m.world, m.attacker, "with a counter attack");
		}
	}

	private void checkKnockbackAttack(Attacked m) {
		if (m.attacker.hasTrait(Trait.KNOCKBACK)){
			int dx = m.attacked.position.x - m.attacker.position.x;
			int dy = m.attacked.position.y - m.attacker.position.y;
			
			for (int i = 0; i < 2; i++) {
				if (m.attacked.canEnter(m.world.tile(m.attacked.position.x+dx, m.attacked.position.y+dy))
						&& m.world.creature(m.attacked.position.x+dx, m.attacked.position.y+dy) == null){
					m.attacked.position.x += dx;
					m.attacked.position.y += dy;
				}
			}
		}
	}
}
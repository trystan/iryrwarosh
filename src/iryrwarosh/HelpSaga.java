package iryrwarosh;

public class HelpSaga implements Handler {

	@Override
	public void handle(Message message) {
		if (EquipedWeapon.class.isAssignableFrom(message.getClass()))
			onEquiped((EquipedWeapon)message);
	}

	private void onEquiped(EquipedWeapon message) {
		String help = message.weapon.name();
		
		if (message.weapon.comboAttackPercent > 0)
			help += " " + message.weapon.comboAttackPercent + "% chance of a combo attack.";

		if (message.weapon.evadeAttackPercent > 0)
			help += " " + message.weapon.evadeAttackPercent + "% chance of attacking when evading.";

		if (message.weapon.circleAttackPercent > 0)
			help += " " + message.weapon.circleAttackPercent + "% chance of attacking in a circle.";

		if (message.weapon.finishingAttackPercent > 0)
			help += " " + message.weapon.finishingAttackPercent + "% chance of a coup de grace.";

		if (message.weapon.distantAttackPercent > 0)
			help += " " + message.weapon.distantAttackPercent + "% chance of attacking when a neighbor moves.";

		if (message.weapon.counterAttackPercent > 0)
			help += " " + message.weapon.counterAttackPercent + "% chance of counter attacking.";
		
		MessageBus.publish(new Note(message.world, message.creature, help));
	}
}

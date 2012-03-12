package iryrwarosh;

public class HelpSaga implements Handler {

	@Override
	public void handle(Message message) {
		if (EquipedItem.class.isAssignableFrom(message.getClass()))
			onEquiped((EquipedItem)message);
	}

	private void onEquiped(EquipedItem message) {
		String help = message.item.name();
		
		if (Weapon.class.isAssignableFrom(message.item.getClass())){
			Weapon weapon = (Weapon)message.item;
			
			if (weapon.comboAttackPercent > 0)
				help += " " + weapon.comboAttackPercent + "% chace of combo attack.";
		
			if (weapon.evadeAttackPercent > 0)
				help += " " + weapon.evadeAttackPercent + "% chance of attacking when evading.";
		
			if (weapon.circleAttackPercent > 0)
				help += " " + weapon.circleAttackPercent + "% chance of attacking in a circle.";
		
			if (weapon.finishingAttackPercent > 0)
				help += " " + weapon.finishingAttackPercent + "% chance of a coup de grace.";
		
			if (weapon.distantAttackPercent > 0)
				help += " " + weapon.distantAttackPercent + "% chance of attacking a moving neighbor.";
		
			if (weapon.counterAttackPercent > 0)
				help += " " + weapon.counterAttackPercent + "% chance of counter attacking.";
			
			if (weapon.isImuneToSpikes)
				help += " Spiked monsters can't counter.";
		}
		
		MessageBus.publish(new Note(message.world, message.creature, help));
	}
}

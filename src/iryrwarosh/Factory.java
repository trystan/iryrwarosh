package iryrwarosh;

import asciiPanel.AsciiPanel;

public class Factory {

	public Weapon knuckes(){
		Weapon w = new Weapon("Knuckes", ')', AsciiPanel.white);
		w.comboAttackPercent = 33;
		return w;
	}
	
	public Weapon knife(){
		Weapon w = new Weapon("Knife", ')', AsciiPanel.white);
		w.evadeAttackPercent = 66;
		return w;
	}
	
	public Weapon club(){
		Weapon w = new Weapon("Club", ')', AsciiPanel.white);
		w.circleAttackPercent = 50;
		return w;
	}
	
	public Weapon sword(){
		Weapon w = new Weapon("Sword", ')', AsciiPanel.white);
		w.finishingAttackPercent = 80;
		return w;
	}
	
	public Weapon spear(){
		Weapon w = new Weapon("Spear", ')', AsciiPanel.white);
		w.distantAttackPercent = 75;
		return w;
	}
	
	public Weapon staff(){
		Weapon w = new Weapon("Staff", ')', AsciiPanel.white);
		w.counterAttackPercent = 75;
		return w;
	}

	public Weapon weapon() {
		switch ((int)(Math.random() * 6)){
		case 0: return knuckes();
		case 1: return knife();
		case 2: return club();
		case 3: return sword();
		case 4: return spear();
		default: return staff();
		}
	}
}

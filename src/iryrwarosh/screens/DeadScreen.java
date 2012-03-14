package iryrwarosh.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class DeadScreen implements Screen {
	private Screen previous;
	private String causeOfDeath;
	
	public DeadScreen(Screen previous, String causeOfDeath){
		this.previous = previous;
		this.causeOfDeath = causeOfDeath;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		previous.displayOutput(terminal);
		
		terminal.writeCenter("-- " + causeOfDeath + ". Press [enter] to restart --", 2, AsciiPanel.brightWhite);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		return key.getKeyCode() == KeyEvent.VK_ENTER ? new ChooseStartingItemsScreen() : this;
	}
}

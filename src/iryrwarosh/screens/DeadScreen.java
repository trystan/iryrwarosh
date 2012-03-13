package iryrwarosh.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class DeadScreen implements Screen {
	private Screen previous;
	
	public DeadScreen(Screen previous){
		this.previous = previous;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		previous.displayOutput(terminal);
		
		terminal.writeCenter("-- You have died. Press [enter] to restart --", 2, AsciiPanel.brightWhite);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		return key.getKeyCode() == KeyEvent.VK_ENTER ? new ChooseStartingItemsScreen() : this;
	}
}

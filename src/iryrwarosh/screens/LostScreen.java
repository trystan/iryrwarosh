package iryrwarosh.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class LostScreen implements Screen {
	private Screen previous;
	
	public LostScreen(Screen previous){
		this.previous = previous;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		previous.displayOutput(terminal);
		
		terminal.writeCenter("-- You failed and a rival is the new ruler of old-school Hyrule! --", 2, AsciiPanel.brightWhite);
		terminal.writeCenter("-- Press [enter] to restart --", 3, AsciiPanel.brightWhite);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		return key.getKeyCode() == KeyEvent.VK_ENTER ? new ChooseStartingItemsScreen() : this;
	}
}

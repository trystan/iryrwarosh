package iryrwarosh.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class PlayScreen implements Screen {

	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.write("You are having fun.", 1, 1);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		
		return this;
	}
}

package iryrwarosh.screens;

import iryrwarosh.World;
import iryrwarosh.Worldgen;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class DeadScreen implements Screen {

	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.write("You have died.", 1, 1, AsciiPanel.brightWhite);
		terminal.writeCenter("-- press [enter] to restart --", 22);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		return key.getKeyCode() == KeyEvent.VK_ENTER ? newGame() : this;
	}
	
	private PlayScreen newGame(){
		World world = new Worldgen(48 / 3, 24 / 3).build();
		return new PlayScreen(world);
	}
}

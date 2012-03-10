package iryrwarosh.screens;

import iryrwarosh.World;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class PlayScreen implements Screen {
	private World world;
	
	public PlayScreen(World world){
		this.world = world;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.write("You are having fun.", 1, 1);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyCode()){
		case KeyEvent.VK_M: return new WorldMapScreen(this, world.map());
		}
		return this;
	}
}

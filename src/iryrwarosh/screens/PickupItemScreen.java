package iryrwarosh.screens;

import iryrwarosh.Creature;
import iryrwarosh.Item;
import iryrwarosh.World;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class PickupItemScreen implements Screen {
	private Screen previous;
	private World world;
	private Creature player;
	
	public PickupItemScreen(Screen previous, World world, Creature player){
		this.previous = previous;
		this.world = world;
		this.player = player;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		previous.displayOutput(terminal);
		
		terminal.clear(' ', 1, 20, 31, 3);
		terminal.write("What do you want to swap with?", 1, 20);
		terminal.write(" [z] ", 1, 21);
		terminal.write(player.leftHand().name(), player.leftHand().color());
		terminal.write(" [x] ", 1, 22);
		terminal.write(player.rightHand().name(), player.rightHand().color());
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		Item item = world.item(player.position.x, player.position.y);
		
		switch (key.getKeyChar()){
		case 'z': player.swapLeftHand(world, item); return previous;
		case 'x': player.swapRightHand(world, item); return previous;
		}
		return previous;
	}
}

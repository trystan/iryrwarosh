package iryrwarosh.screens;

import iryrwarosh.Creature;
import iryrwarosh.World;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class LookAtScreen implements Screen {
	private Screen previous;
	private World world;
	private int lookX;
	private int lookY;
	private int startX;
	private int startY;
	
	public LookAtScreen(Screen previous, World world, int startX, int startY){
		this.previous = previous;
		this.world = world;
		this.startX = startX;
		this.startY = startY;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		previous.displayOutput(terminal);
		
		terminal.write('X', lookX, lookY+1, AsciiPanel.magenta, world.tile(startX + lookX, startY + lookY).background());
		
		Creature c = world.creature(startX + lookX, startY + lookY);
		
		if (c != null){
			String text = c.name() + " (" + c.describeTraits() + ")";
			
			if (c.weapon() != null)
				text += " weilding a " + c.weapon().name();
			
			terminal.write(text, 1, 23);
		} else {
			terminal.write(world.tile(startX + lookX, startY + lookY).name(), 1, 23);
		}
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyCode()){
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_H: moveBy(-1, 0); break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_L: moveBy( 1, 0); break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_K: moveBy( 0,-1); break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_J: moveBy( 0, 1); break;
        case KeyEvent.VK_Y: moveBy(-1,-1); break;
        case KeyEvent.VK_U: moveBy( 1,-1); break;
        case KeyEvent.VK_B: moveBy(-1, 1); break;
        case KeyEvent.VK_N: moveBy( 1, 1); break;
        case KeyEvent.VK_ESCAPE:
        case KeyEvent.VK_ENTER: return previous;
		}
		
		return this;
	}

	private void moveBy(int x, int y) {
		lookX += x;
		lookY += y;
	}

}

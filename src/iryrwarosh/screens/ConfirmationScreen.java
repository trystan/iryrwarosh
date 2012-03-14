package iryrwarosh.screens;

import iryrwarosh.MessageBus;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class ConfirmationScreen implements Screen {
	private PlayScreen previous;
	private Screen next;
	private String confirmation;
	
	public ConfirmationScreen(PlayScreen previous, Screen next, String confirmation){
		this.previous = previous;
		this.next = next;
		this.confirmation = confirmation;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		previous.displayOutput(terminal);
		
		terminal.write(confirmation + " [y/n]", 1, 20);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyCode()){
        case KeyEvent.VK_Y:
        	MessageBus.unsubscribe(previous);
        	return next;
        case KeyEvent.VK_N: 
        	return previous;
    	default:
    		return previous;
		}
	}
}

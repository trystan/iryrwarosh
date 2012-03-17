package iryrwarosh.screens;

import iryrwarosh.Common;
import iryrwarosh.GainedFame;
import iryrwarosh.Message;
import iryrwarosh.SaidOutLoud;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import asciiPanel.AsciiPanel;

public class MessageLogScreen implements Screen {
	private Screen previous;
	private List<Message> messages = new ArrayList<Message>();
	private int scroll;
	
	public MessageLogScreen(Screen previous){
		this.previous = previous;
	}
	
	public void record(Message message){
		messages.add(message);
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.setDefaultForegroundColor(Common.guiForeground);
		terminal.setDefaultBackgroundColor(Common.guiBackground);
		terminal.clear();
		
		terminal.writeCenter("Message Log", 0);
		for (int i = 0; i < 22; i++){
			if (scroll + i >= messages.size())
				break;
			
			Message m = messages.get(scroll + i); 
			Color color = (GainedFame.class.isAssignableFrom(m.getClass())) ? AsciiPanel.brightYellow : Common.guiForeground;
			
			if (SaidOutLoud.class.isAssignableFrom(m.getClass())) 
				color = ((SaidOutLoud)m).creature.color();
			
			terminal.write(clean(m.text()), 1, i+2, color, null);
		}
	}
	
	private String clean(String text){
		return text.replace("player has", "player have")
		           .replace("The player's", "Your").replace("the player's", "your").replace("player's", "your")
			       .replace("The player", "You").replace("the player", "you").replace("player", "you");
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyCode()){
        case KeyEvent.VK_UP:
        case KeyEvent.VK_NUMPAD8:
        case KeyEvent.VK_K: scrollUp(); break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_NUMPAD2:
        case KeyEvent.VK_J: scrollDown(); break;
        default: return previous;
		}
		
		return this;
	}

	private void scrollUp(){
		scroll--;
		fixScrolling();
	}
	
	private void scrollDown(){
		scroll++;
		fixScrolling();
	}
	
	public void scrollToEnd(){
		scroll = messages.size();
		fixScrolling();
	}

	private void fixScrolling() {
		if (scroll > messages.size() - 8)
			scroll = messages.size() - 8;
		if (scroll < 0)
			scroll = 0;
	}
}

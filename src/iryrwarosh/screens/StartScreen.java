package iryrwarosh.screens;

import iryrwarosh.ArmosSaga;
import iryrwarosh.CreatureAiHandler;
import iryrwarosh.Handler;
import iryrwarosh.LootSaga;
import iryrwarosh.MapExplorationHandler;
import iryrwarosh.Message;
import iryrwarosh.MessageBus;
import iryrwarosh.ItemSpecialsSaga;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class StartScreen implements Screen {

	public StartScreen(){
		MessageBus.subscribe(new Handler(){

			@Override
			public void handle(Message message) {
				//System.out.println(message.text());
			}
			
		});

		MessageBus.subscribe(new MapExplorationHandler());
		MessageBus.subscribe(new ItemSpecialsSaga());
		MessageBus.subscribe(new LootSaga());
		MessageBus.subscribe(new ArmosSaga());
		MessageBus.subscribe(new CreatureAiHandler());
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.writeCenter("I rule, you rule, we all rule old-school Hyrule", 1, AsciiPanel.brightWhite);
		terminal.writeCenter("a 2012 seven day roguelike by Trystan Spangler", 2);
		terminal.writeCenter("-- press [enter] to start --", 22);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		return key.getKeyCode() == KeyEvent.VK_ENTER ? new ChooseStartingItemsScreen() : this;
	}
}

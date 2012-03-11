package iryrwarosh.screens;

import iryrwarosh.ArmosSaga;
import iryrwarosh.Handler;
import iryrwarosh.HelpSaga;
import iryrwarosh.LootSaga;
import iryrwarosh.Message;
import iryrwarosh.MessageBus;
import iryrwarosh.SpecialAttackSaga;
import iryrwarosh.World;
import iryrwarosh.Worldgen;

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

		MessageBus.subscribe(new HelpSaga());
		MessageBus.subscribe(new SpecialAttackSaga());
		MessageBus.subscribe(new LootSaga());
		MessageBus.subscribe(new ArmosSaga());
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.writeCenter("I rule, you rule, we all rule old-school Hyrule", 1, AsciiPanel.brightWhite);
		terminal.writeCenter("a 2012 seven day roguelike by Trystan Spangler", 2);
		terminal.writeCenter("-- press [enter] to start --", 22);
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

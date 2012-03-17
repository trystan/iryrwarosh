package iryrwarosh.screens;

import iryrwarosh.Common;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class HelpScreen implements Screen {
	private Screen previous;
	private String[] lines;
	private int scroll;
	
	public HelpScreen(Screen previous){
		this.previous = previous;
		
		this.lines = new String[]{
			"I rule, you rule, we all rule old-school Hyrule",
			"",
			"KEYS:",
			" movement         vi keys, keypad, arrows",
			" use first item   [z]",
			" use second item  [x]",
			" look around      [space]",
			" pickup item      [g] or [,]",
			" view world map   [m]",
			" message log      [']",
			" help             [?]",
			" quit             [q] or [escape]",
			"",
			"",
			"WHAT'S GOING ON HERE?",
			"",
			" The land of Hyrule has been overrun by strange monsters and needs a famous",
			"hero to rule. You are one of 5 people competing to see who will be the next",
			"ruler; the first to have 100% of the fame they need will be declared the new",
			"ruler. You gain fame by exploring new areas, discovering lost artifacts, ",
			"defeating giant monsters, defeating bosses, and even defeating your other ",
			"rivals. It's an all-out competition beween you, your rivals, and everything ",
			"that's trying to kill you.",
			"",
			"",
			"WHAT'S TRYING TO KILL ME?",
			"",
			" Goblins wander around with weapons; examine them before getting to close.",
			" Monsters come is 8 different varieties, each with sevearl random traits.",
			" Giant monsters are even tougher and have an additional random trait.",
			" Strange and powerful creatures guard powerful items and power ups.",
			" Zoras are native aquatic animals. New monsters have nearly wiped them out.",
			" Rivals will try to kill you if they think they can get you out of their way.",
			"",
			"",
			"WHAT CAN I DO ABOUT IT?",
			"",
			" First off: pay attention. Press [space] to examine your surroundings. As ",
			"you learn what each weapon and trait can do, you will learn who to avoid and",
			"who to attack. most creatures just want to mull about and live their life ",
			"but some just don't play well with others. Secondly, your rivals are smart ",
			"and ruthless so you don't have much time to waste if you want to rule. You",
			"can focus on exploring and finding artifacts or on cleansing the land of the",
			"strongest monsters - no one gets famous by standing in place. Each rival also",
			"has their own items and personality so pay attention to that. The third thing",
			"you should do is use your surroundings. If you can place something between you",
			"and what's trying to kill you, then you'll probably live longer. Don't forget",
			"about evasion eigher. Evasion is affected by any items you have, potions you",
			"have collected, and your immediate surroundings. A high evasion means that you",
			"will bave a better chance of not being hit. Some weapons also get a free attack",
			"when you evade too. Of course, other creatures can evade too. The final thing",
			"is to keep trying. There are many different items you can start with and many",
			"more you can find during your travels. You may start next to a power up or ",
			"surrounded by poisonous, flying, spiked monsters. It's just the luck of the ",
			"draw.",
			"",
			"",
			"I WANT SPOILERS!",
			"",
			" Well, here's a few things that you can find:",
			"",
			" Rings of regeneration will slowly regenerate hearts.",
			" A bag of imps can summon imps to help you out. For a price.",
			" Bombs. Pay a few rupees to place then run away.",
			" Spike armor will have your attackers impaing themselves on you.",
			" Camouflaged monsters can only be seen from a few spaces away.",
			" Reaching monsters (and weapons) can hit anyone who steps near them.",
			" Social monsters will call for help if they are threatened.",
			" Some monsters can move twice as fast or attack twice as fast as you.",
		};
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.setDefaultForegroundColor(Common.guiForeground);
		terminal.setDefaultBackgroundColor(Common.guiBackground);
		terminal.clear();
		
		for (int i = 0; i < 22; i++){
			if (scroll + i >= lines.length)
				break;
			
			terminal.write(lines[scroll + i], 0, 1+i);
		}
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
	
	private void fixScrolling() {
		if (scroll > lines.length - 8)
			scroll = lines.length - 8;
		if (scroll < 0)
			scroll = 0;
	}

}

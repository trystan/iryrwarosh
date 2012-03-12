package iryrwarosh;

import java.applet.Applet;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import asciiPanel.AsciiPanel;

public class AppletMain extends Applet implements KeyListener {
	private static final long serialVersionUID = 1L;
	
	private AsciiPanel terminal;
	private iryrwarosh.screens.Screen screen;
	
	public AppletMain(){
		super();
		terminal = new AsciiPanel();
		add(terminal);
		screen = new iryrwarosh.screens.StartScreen();
		addKeyListener(this);
		repaint();
	}
	
	@Override
	public void init(){
		super.init();
		this.setSize(terminal.getWidth(), terminal.getHeight());
	}

	@Override
	public void repaint(){
		terminal.clear();
		screen.displayOutput(terminal);
		super.repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		screen = screen.respondToUserInput(e);
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) { }

	@Override
	public void keyTyped(KeyEvent e) { }
}

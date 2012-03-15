package iryrwarosh;

import java.util.Hashtable;
import java.util.Set;

public class FameHandler implements Handler {
	private Hashtable<Creature, Integer> fame;
	
	public FameHandler(){
		fame = new Hashtable<Creature, Integer>();
	}
	
	public Set<Creature> getFamousPeople(){
		return fame.keySet();
	}
	
	public int getFame(Creature creature){
		if (fame.containsKey(creature))
			return fame.get(creature);
		else
			return 0;
	}
	
	
	@Override
	public void handle(Message message) {
		if (Killed.class.isAssignableFrom(message.getClass()))
			handle((Killed)message);
		
		if (ExploredNewLocation.class.isAssignableFrom(message.getClass()))
			handle((ExploredNewLocation)message);
		
		if (ExploredNewBiome.class.isAssignableFrom(message.getClass()))
			handle((ExploredNewBiome)message);
		
		if (DiscoveredLostArtifact.class.isAssignableFrom(message.getClass()))
			handle((DiscoveredLostArtifact)message); 
	}

	private void handle(Killed message){
		if (message.attacker.glyph() != '@')
			return;
		
		if (message.attacked.glyph() == '@')
			gainFame(message.attacker, 15);
		else if (message.attacked.isMiniboss())
			gainFame(message.attacker, 5);
		else if (message.attacked.glyph() == 'M')
			gainFame(message.attacker, 1);
	}

	private void handle(ExploredNewBiome message){
		if (message.creature.glyph() != '@')
			return;
		
		gainFame(message.creature, 8);
	}
	
	private void handle(ExploredNewLocation message){
		if (message.creature.glyph() != '@')
			return;
		
		gainFame(message.creature, 2);
	}
	
	private void handle(DiscoveredLostArtifact message){
		if (message.creature.glyph() != '@')
			return;
		
		gainFame(message.creature, 15);
	}
	
	private void gainFame(Creature creature, int amount){
		if (fame.containsKey(creature))
			fame.put(creature, amount + fame.get(creature));
		else
			fame.put(creature, amount);
	}
}

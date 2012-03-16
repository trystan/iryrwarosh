package iryrwarosh;

import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

public class FameHandler implements Handler {
	private Hashtable<Creature, Integer> fame;
	
	public FameHandler(){
		fame = new Hashtable<Creature, Integer>();
	}
	
	public List<Creature> getFamousPeople(){
		List<Creature> list = Collections.list(fame.keys());
		
		Collections.sort(list, new Comparator<Creature>(){
			@Override
			public int compare(Creature c1, Creature c2) {
				return ((Integer)getFame(c2)).compareTo(getFame(c1));
			}
		});
		
		return list;
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
			gainFame(message.attacker, 15, message.attacker.name() + " has killed " + message.attacked.name());
		else if (message.attacked.isMiniboss())
			gainFame(message.attacker, 5, message.attacker.name() + " has killed " + message.attacked.name());
		else if (message.attacked.glyph() == 'M')
			gainFame(message.attacker, 1, message.attacker.name() + " has killed a giant monster");
	}

	private void handle(ExploredNewBiome message){
		if (message.creature.glyph() != '@')
			return;
		
		gainFame(message.creature, 8, null);
	}
	
	private void handle(ExploredNewLocation message){
		if (message.creature.glyph() != '@')
			return;
		
		gainFame(message.creature, 2, null);
	}
	
	private void handle(DiscoveredLostArtifact message){
		if (message.creature.glyph() != '@')
			return;
		
		gainFame(message.creature, 15, message.creature.name() + " has discovered a " + message.item.name());
	}
	
	private void gainFame(Creature creature, int amount, String string){
		if (fame.containsKey(creature))
			fame.put(creature, amount + fame.get(creature));
		else
			fame.put(creature, amount);
		
		if (string != null)
			MessageBus.publish(new GainedFame(null, creature, string));
	}
}

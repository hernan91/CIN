import java.util.Random;

import org.moeaframework.core.Variable;

public class SensorsVar implements Variable{
	private Location[] locations;
	private int lowerBoundX;
	private int upperBoundX;
	private int lowerBoundY;
	private int upperBoundY;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SensorsVar(SensorsVar sensorSet) {
		this.lowerBoundX = sensorSet.lowerBoundX;
		this.upperBoundX = sensorSet.upperBoundX;
		this.lowerBoundY = sensorSet.lowerBoundY;
		this.upperBoundY = sensorSet.upperBoundY;
		Location[] locations = new Location[sensorSet.size()];
		for(int i=0; i<locations.length; i++) {
			int x = sensorSet.getLocations()[i].getPosX();
			int y = sensorSet.getLocations()[i].getPosY();
			locations[i] = new Location(x,y);
		}
		this.locations = locations;
	}
	
	public SensorsVar(int maxSensors, int lowerBoundX, int upperBoundX, int lowerBoundY, int upperBoundY) {
		this.locations = new Location[maxSensors];
		this.lowerBoundX = lowerBoundX;
		this.upperBoundX = upperBoundX;
		this.lowerBoundY = lowerBoundY;
		this.upperBoundY = upperBoundY;
		for(int i=0; i<locations.length; i++) {
			locations[i] = new Location(0,0);
		}
	}
	
	public Location[] getLocations() {
		return locations;
	}

	public void setLocations(Location[] locations) {
		this.locations = locations;
	}

	@Override
	public Variable copy() {
		return new SensorsVar(this);
	}

	@Override
	public void randomize() {
		for(int i=0; i<size(); i++) {
			Random rand = new Random();
			int x = (rand.nextInt(upperBoundX - lowerBoundX) + 1) + lowerBoundX;
			int y = (rand.nextInt(upperBoundY - lowerBoundY) + 1) + lowerBoundY;
			locations[i].setPosX(x);
			locations[i].setPosY(y);
		}
	}
	
	public int size() {
		return locations.length;
	}
	
}

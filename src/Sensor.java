import java.util.ArrayList;
import java.util.List;


public class Sensor {
	private Location location; //Stores the value of the coordinate
	private boolean deployed; //Stores whether the sensor is enabled or not
	private int active; //Stores whether the sensor is active or not.
	private int distanceToHECN; //Stores the distance to the HECN
	private List<Integer> hopRelayNeighbors; //Stores the list of relay neighbors
	private List<Integer> childrenRelayNeighbors; //Stores the list of relay neighbors
	private List<Integer> energyRelayNeighbors; //Stores the list of relay energy neighbors
	private double trafficLoad; //Stores the traffic load of the sensor
	private double energyConsumed;

	public Sensor(Location location, boolean status) {
		this.location = location;
		this.deployed = status;//(PseudoRandom.randDouble() < 0.5) ? true:false;
		if(deployed) {
			this.distanceToHECN = 0;
			this.hopRelayNeighbors = new ArrayList<Integer>();
			this.childrenRelayNeighbors = new ArrayList<Integer>();
			this.energyRelayNeighbors  = new ArrayList<Integer>();
			this.trafficLoad = 0.0;
			//this.energyConsumed = 0.0;
			this.active = 0;
		}
	}
	
	public Sensor(Sensor sensor) {
		this.location = sensor.location;
		this.deployed = sensor.deployed;
		this.distanceToHECN = sensor.distanceToHECN;
		this.hopRelayNeighbors = sensor.hopRelayNeighbors;
		this.childrenRelayNeighbors = sensor.childrenRelayNeighbors;
		this.energyRelayNeighbors = sensor.energyRelayNeighbors;
		this.trafficLoad = sensor.trafficLoad;
		this.energyConsumed = sensor.energyConsumed;
	}

	public int isActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public double getEnergyConsumed() {
		return energyConsumed;
	}

	public void setEnergyConsumed(double energyConsumed) {
		this.energyConsumed = energyConsumed;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public boolean isDeployed() {
		return deployed;
	}

	public void setDeployed(boolean deployed) {
		this.deployed = deployed;
	}

	public int getDistanceToHECN() {
		return distanceToHECN;
	}

	public void setHopsDistanceToHECN(int distanceToHECN) {
		this.distanceToHECN = distanceToHECN;
	}

	public List<Integer> getHopRelayNeighbors() {
		return hopRelayNeighbors;
	}

	public void setHopRelayNeighbors(List<Integer> hopRelayNeighbors) {
		this.hopRelayNeighbors = hopRelayNeighbors;
	}

	public List<Integer> getChildrenRelayNeighbors() {
		return childrenRelayNeighbors;
	}

	public void setChildrenRelayNeighbors(List<Integer> childrenRelayNeighbors) {
		this.childrenRelayNeighbors = childrenRelayNeighbors;
	}

	public List<Integer> getEnergyRelayNeighbors() {
		return energyRelayNeighbors;
	}

	public void setEnergyRelayNeighbors(List<Integer> energyRelayNeighbors) {
		this.energyRelayNeighbors = energyRelayNeighbors;
	}

	public double getTrafficLoad() {
		return trafficLoad;
	}

	public void setTrafficLoad(double trafficLoad) {
		this.trafficLoad = trafficLoad;
	}

	public double getEneryConsumed() {
		return energyConsumed;
	}

	public void setEneryConsumed(double eneryConsumed) {
		this.energyConsumed = eneryConsumed;
	}
}
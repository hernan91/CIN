
public class SensorInformation {
	private int energyRadius;
	private int sensingRadius;
	private int commRadius;
	
	public SensorInformation(int energyRadius, int sensingRadius, int commRadius) {
		super();
		this.energyRadius = energyRadius;
		this.sensingRadius = sensingRadius;
		this.commRadius = commRadius;
	}

	public int getEnergyRadius() {
		return energyRadius;
	}

	public void setEnergyRadius(int energyRadius) {
		this.energyRadius = energyRadius;
	}

	public int getSensingRadius() {
		return sensingRadius;
	}

	public void setSensingRadius(int sensingRadius) {
		this.sensingRadius = sensingRadius;
	}

	public int getCommRadius() {
		return commRadius;
	}

	public void setCommRadius(int commRadius) {
		this.commRadius = commRadius;
	}	
}

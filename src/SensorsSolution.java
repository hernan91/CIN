import java.util.ArrayList;
import org.moeaframework.core.Solution;

public class SensorsSolution extends Solution{
	private static final long serialVersionUID = 1L;
	private ArrayList<Location> locationsList;
	
	public SensorsSolution(int numberOfVariables, int numberOfObjectives, int numberOfConstraints) {
		super(numberOfVariables, numberOfObjectives, numberOfConstraints);
	}

	public SensorsSolution(Solution solution) {
		super(solution);
	}

	public ArrayList<Location> getLocationsList() {
		return locationsList;
	}

	public void setLocationsList(ArrayList<Location> locationsList) {
		this.locationsList = locationsList;
	}
	
	public SensorsSolution deepCopy() {
		Solution sol = this.deepCopy();
		SensorsSolution solution = (SensorsSolution) sol;
		solution.setLocationsList(this.getLocationsList());
		return solution;
	}
	
	@Override
	public SensorsSolution copy() {
		Solution solution = super.copy();
		SensorsSolution sensorsSolution = new SensorsSolution(solution);
		sensorsSolution.setLocationsList(this.getLocationsList());
		return sensorsSolution;
		
	}
}
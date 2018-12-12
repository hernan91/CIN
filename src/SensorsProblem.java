import java.util.ArrayList;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

/**
 * Por ahora la idea es concentrarse en hacerlo funcionar, no modularizarlo
 * @author hernan
 *
 */
public class SensorsProblem implements Problem{
	private SensorsFieldDimensions sfDimensions;
	private Location hecnLocation;
	private SensorInformation sInf;
	private ArrayList<Location> energyPoints;
	private int maxSensors;
	//public static String attrName = "arrayLocations";

	
	public SensorsProblem(SensorsFieldDimensions sfDimensions, Location hecnLocation, ArrayList<Location> energyPoints,
			SensorInformation sInf, int maxSensors) {
		this.sfDimensions = sfDimensions;
		this.hecnLocation = hecnLocation;
		this.energyPoints = energyPoints;
		this.sInf = sInf;
		this.maxSensors = maxSensors;
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(getNumberOfVariables(), getNumberOfObjectives(), getNumberOfConstraints());
		solution.setVariable(0, EncodingUtils.newBinary(maxSensors));
		for(int i=1; i<=getNumberOfVariables(); i=i+2) {
			if(i==101) {
				System.err.println("Hola");
			}
			solution.setVariable(i, EncodingUtils.newInt(0, sfDimensions.getGridSizeX()));
			solution.setVariable(i+1, EncodingUtils.newInt(0, sfDimensions.getGridSizeY()));
		}
		return solution;
	}
	
	@Override
	public void evaluate(Solution solution) {
		SensorsSolution sensorsSolution = new SensorsSolution(solution);
		// compute the active sensors
		int numberOfDeployedSensors = sensorsSolution.getNumberOfDeployedSensors();
		ObjectiveFunction objFunc = new ObjectiveFunction(this);
		double[] coverageEnergy = objFunc.sensorCoverageEnergy(sensorsSolution);

		// uncovered region
		double uncovered = coverageEnergy[0];
		// energy
		double energy = coverageEnergy[1];

		solution.setObjective(0, (double) numberOfDeployedSensors);
		solution.setObjective(1, energy);

		// compute and set constraints
		//double[] constraint = new double[this.getNumberOfConstraints()];

		uncovered = -uncovered;

//		double total = 0.0;
//		int number = 0;
//		for (int i = 0; i < this.getNumberOfConstraints(); i++) {
//			if (constraint[i] < 0.0) {
//				number++;
//				total += constraint[i];
//			}
//		}
		
		solution.setConstraint(0, uncovered);
			
		//ESTO IBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa
		//solution.setOverallConstraintViolation(total);
		//solution.setNumberOfViolatedConstraint(number);

//		ArrayList<Location> sensorsLocations = SensorsSolution.generateArrayList(solution);
//		ObjectiveFunction objFunc = new ObjectiveFunction(this.sfDimensions, 2);
//		double f1 = objFunc.getFitness(sensorsLocations);
//		solution.setObjective(0, -f1);
	}

	public SensorsFieldDimensions getSfDimensions() {
		return sfDimensions;
	}

	public void setSfDimensions(SensorsFieldDimensions sfDimensions) {
		this.sfDimensions = sfDimensions;
	}

	public Location getHecnLocation() {
		return hecnLocation;
	}

	public void setHecnLocation(Location hecnLocation) {
		this.hecnLocation = hecnLocation;
	}

	public SensorInformation getsInf() {
		return sInf;
	}

	public void setsInf(SensorInformation sInf) {
		this.sInf = sInf;
	}

	public ArrayList<Location> getEnergyPoints() {
		return energyPoints;
	}

	public void setEnergyPoints(ArrayList<Location> energyPoints) {
		this.energyPoints = energyPoints;
	}

	public int getMaxSensors() {
		return maxSensors;
	}

	public void setMaxSensors(int maxSensors) {
		this.maxSensors = maxSensors;
	}

	@Override
	public String getName() {
		return "SensorsProblem";
	}

	@Override
	public int getNumberOfVariables() {
		return getMaxSensors()*2+1;
	}

	@Override
	public int getNumberOfObjectives() {
		return 2;
	}

	@Override
	public int getNumberOfConstraints() {
		return 1;
	}

	@Override
	public void close() {
		
	}
}
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.moeaframework.core.Solution;

import jmetal.util.PseudoRandom;

public class ObjectiveFunction{
	private SensorsProblem problem;
	private SensorsSolution solution;
	private SensorsFieldDimensions sfDim;
	private int sensingRadius;
	private int commRadius;
	private int energyRadius;
	private short[][] terrain;
	private int currentDistanceToHECN;
	private int remainingSensorsToExplore;
	Location sensCoord;
	int sqAreaBetweenSensors;
	double coverage;
	double maxLoad;
	double sumSqDist;
	ArrayList<Location> energyPoints;
	double maxEnergy;
	//private int deployedSensors;
	
	
	public ObjectiveFunction(SensorsProblem problem) {
		this.problem = problem;
		this.sfDim = this.problem.getSfDimensions();
		this.sensingRadius = this.problem.getsInf().getSensingRadius();
		this.commRadius = this.problem.getsInf().getCommRadius();
		this.energyRadius = this.problem.getsInf().getEnergyRadius();
		this.terrain = new short[sfDim.getGridSizeX()][sfDim.getGridSizeY()];
		this.remainingSensorsToExplore = 0;
		this.currentDistanceToHECN = 0;
		this.coverage = 0;
		this.energyPoints = this.problem.getEnergyPoints();
		
		//this.deployedSensors = this.solution.getNumberOfDeployedSensors();
	}
	
	double[] sensorCoverageEnergy(SensorsSolution sensorsSolution) {
		//short[][] terrain = new short[sfDim.getGridSizeX()][sfDim.getGridSizeY()];
		
		double result[] = new double[2];
		initTerrainGrid();
		activateSensorsInHECNsCommRadio();
		calculateCoverage();
		calculateEnergySpentByHecnsNeighborsChilds();
		calculateEnergySpentByHecnsNeighbor();
		result[0] = 100.0 - (100.0 * coverage) / (sfDim.getGridSizeX() * sfDim.getGridSizeY());
		// The second result is now the maximum energy consumed in a node
		result[1] = maxEnergy;
		return result;
		
		
		// Initializing the states of sensors, trafficLoad and the hopRelayNeighbors
		// Ya realizado en la clase Sensor
		///////////////activateSensorsInHECNsCommRadio();
		// Sensor activation by HECN
		// Se calculan las areas de cobertura de los sensores y el HECN.
		// Se calcula la cobertura de la distancia entre un sensor y el HECN.
		// Si se determina que el sensor esta dentro del area del HECN, se activa.
		

		// Sensor coverage calculation
		///////////////////////////////calculateCoverage();

//	  for (int l = 0; l < terrainSideSize_; l++)
//	  {
//		  for (int c = 0; c < terrainSideSize_; c++)
//		  {
//			  System.out.print(terrain_[l][c] + " ");
//		  }
//		  System.out.println();
//		  
//	  }
//	  System.exit(-1);

		// Computation of the traffic load for each sensor in the network, starting from
		// the farthest nodes from the HECN
		/////////////////////////////calculateTraffic();
		// Now to calculate the energy spent by the HECN immediate neighbors...
		//////////////////////////calculateEnergy();
		// First result: UNCOVERED TERRAIN (to minimize)
//	  System.out.println("cover = " + coverage);
//		result[0] = 100.0 - (100.0 * coverage) / (sfDim.getGridSizeX() * sfDim.getGridSizeY());
//		// The second result is now the maximum energy consumed in a node
//		result[1] = maxEnergy;
//		return result;
	}
	
	private ArrayList<Sensor> getSensorsList(){
		return solution.getSensorsList();
	}
	
	//Metodo para calcular si un punto se encuentra dentro del area de cobertura de otro
	private int areaBetweenTwoLocations(Location loc1, Location loc2) {
		double c1 = Math.pow(loc1.getPosX() - loc2.getPosX(), 2);
		double c2 = Math.pow(loc1.getPosY() - loc2.getPosY(), 2);
		return (int) (c1+c2);
	}
	
	private int getSqCommRadius() {
		return (int) Math.pow(commRadius, 2);
	}
	
	private int getSqSensorsRatio() {
		return (int) Math.pow(sensingRadius, 2);
	}
	 
	/**
	 * Activates sensors that are in the HECNS comm Ratio and updates variables
	 * currentDistanceToHECN and remainingSensorsToExplore;
	 */
	private void activateSensorsInHECNsCommRadio() {
		for (int i = 0; i < solution.getNumberOfDeployeableSensors(); i++) {
			Sensor s = getSensorsList().get(i);
			if (s.isDeployed()) {
				//Aca calcula si el sensor actual esta dentro del area de cobertura del HECN
				if (isSensorInHECNsCommRatio(s)) {
					s.setActive(1); // mark sensor as active
					s.setHopsDistanceToHECN(1); // distance to HECN = 1 hop
					// -> already cleared hop_relay_neighbors[i].resize(0);
					remainingSensorsToExplore++; //un sensor que ha sido activado, pero no ha sido explorado
					currentDistanceToHECN = 1;
				}
			}
		}
	}
	
	private boolean isSensorInHECNsCommRatio(Sensor sensor) {
		float sqDistance = areaBetweenTwoLocations(problem.getHecnLocation(), sensor.getLocation());
		float sqCommRadius = getSqCommRadius();
		return sqCommRadius >= sqDistance;
	}
	
	private void initTerrainGrid() {
		// init terrain grid
		for (int i = 0; i < sfDim.getGridSizeX(); i++) {
			for (int j = 0; j < sfDim.getGridSizeY(); j++) {
				terrain[i][j] = 0;
			}
		}
	}
	/**
	 * Hasta que no queden sensores en el plano que no hallan
	 * Para cada sensor:
	 * Se fija si el sensor esta desplegado y 
	 */
	private void calculateCoverage() {

		while (remainingSensorsToExplore > 0) {
			for (int i = 0; i < solution.getNumberOfDeployeableSensors(); i++) {
				Sensor s = getSensorsList().get(i);
				if (s.isDeployed()) {
					// if the sensor is at the present distance from the HECN...
					if (s.getDistanceToHECN() == currentDistanceToHECN) {
						if (s.isActive() == 1) {
							s.setActive(2); // mark sensor as placed
							remainingSensorsToExplore--; // one less sensor remaining
							sensCoord = new Location(s.getLocation().getPosX(), s.getLocation().getPosY());
							// activate inactive sensors within communication range
							for (int j = 0; j < solution.getNumberOfDeployeableSensors(); j++) {
								Sensor jthSensor = getSensorsList().get(j);
								if (jthSensor.isDeployed()) {
									if ((jthSensor.isActive() == 0) || (jthSensor.getDistanceToHECN() == currentDistanceToHECN + 1)) {
										sqAreaBetweenSensors = areaBetweenTwoLocations(sensCoord, jthSensor.getLocation());
										if (getSqCommRadius() >= sqAreaBetweenSensors) { // If the sensor is within communication range
											if (jthSensor.isActive() == 0) { // If the sensor is within communication
																				// range
																				// ..then there is one more sensor
																				// remaining
												remainingSensorsToExplore++;
											}
											// Mark sensor as active...
											jthSensor.setActive(1);
											// and set its distance
											jthSensor.setHopsDistanceToHECN(currentDistanceToHECN + 1);
											jthSensor.getHopRelayNeighbors().add(i);
											s.getChildrenRelayNeighbors().add(j);
										}
									} else if (jthSensor.getDistanceToHECN() > currentDistanceToHECN + 1) {
										sqAreaBetweenSensors = areaBetweenTwoLocations(sensCoord, jthSensor.getLocation());
										if (getSqCommRadius() >= sqAreaBetweenSensors) { // If the sensor is within communication range
																			// Mark sensor as active...
											jthSensor.setActive(1);
											// and set its distance
											jthSensor.setHopsDistanceToHECN(currentDistanceToHECN + 1);
											jthSensor.getHopRelayNeighbors().clear();
											jthSensor.getHopRelayNeighbors().add(i);
										}
										System.out.println("Error: encontrado hijo demasiado lejano.");
										System.exit(-1);
									}
								} // if deployed inactive
							} // for
							for (int x = sensCoord.getPosX() - sensingRadius; x <= sensCoord.getPosX() + sensingRadius; x++) {
								if ((x >= 0) && (x < sfDim.getGridSizeX())) {// x value within range
									int yRange = (int) Math.sqrt((double) getSqSensorsRatio() - (sensCoord.getPosX() - x) * (sensCoord.getPosX() - x));
									for (int y = sensCoord.getPosY() - yRange; y <= sensCoord.getPosY() + yRange; y++) {
										if ((y >= 0) && (y < sfDim.getGridSizeY())) { // y value within range
											terrain[x][y]++;
											if (terrain[x][y] == 1) {
												coverage++;
											}
										}
									}
								}
							}
						} else {
							System.out.println("wsnEval error: inactive sensor placed");
							System.exit(-1);
						}
					}
				}
			}
			currentDistanceToHECN++;
		}
	}
		
	private void calculateEnergySpentByHecnsNeighborsChilds() {
		while (currentDistanceToHECN > 1) {
			for (int i = 0; i < solution.getNumberOfDeployeableSensors(); i++) {
				Sensor ithSensor = getSensorsList().get(i);
				if (ithSensor.isDeployed()) {
					if (ithSensor.getDistanceToHECN() == currentDistanceToHECN) {
						// add the traffic generated by the node i itself
						double traffic = ithSensor.getTrafficLoad();
						ithSensor.setTrafficLoad(traffic + 1.0);
						// update maximum traffic load if necessary
						maxLoad = Math.max(maxLoad, ithSensor.getTrafficLoad());
						// compute sum (1 / dist)
						sumSqDist = 0.0;
						Integer k;
						for (int j = 0; j < ithSensor.getHopRelayNeighbors().size(); j++) {
							k = ithSensor.getHopRelayNeighbors().get(j);
							Sensor kthSensor = getSensorsList().get(k);
							sqAreaBetweenSensors = areaBetweenTwoLocations(ithSensor.getLocation(), kthSensor.getLocation());
							sumSqDist += (1.0 / sqAreaBetweenSensors);
						}
						// increase the load of all relay nodes of i by their corresponding quantity
						// related to distances
						for (int j = 0; j < ithSensor.getHopRelayNeighbors().size(); j++) {
							k = ithSensor.getHopRelayNeighbors().get(j);
							Sensor kthSensor = getSensorsList().get(k);
							sqAreaBetweenSensors = areaBetweenTwoLocations(ithSensor.getLocation(), kthSensor.getLocation());
							double kthTraffic = kthSensor.getTrafficLoad();
							kthTraffic += ithSensor.getTrafficLoad() * ((1.0 / sqAreaBetweenSensors) / sumSqDist);
							kthSensor.setTrafficLoad(kthTraffic);
						}
						// calculate the energy consumed to SEND the data
						// if (sumSqDist != 0.0)
						// {
						double nodeEnergy = ithSensor.getTrafficLoad()
								* ((double) ithSensor.getHopRelayNeighbors().size() / sumSqDist);
						Iterator<Location> it = energyPoints.iterator();
						while (it.hasNext()) {
							Location tmp = it.next();
							if (areaBetweenTwoLocations(ithSensor.getLocation(), tmp) < energyRadius) {
								nodeEnergy = 0;
							}
						}
						maxEnergy = Math.max(maxEnergy, nodeEnergy);
						// }
					}
				}
			}
			currentDistanceToHECN--;
		}

	}

	private void calculateEnergySpentByHecnsNeighbor() {
		for (int i = 0; i < solution.getNumberOfDeployeableSensors(); i++) {
			Sensor ithSensor = getSensorsList().get(i);
			if (ithSensor.isDeployed()) {
				if (ithSensor.getDistanceToHECN() == 1) // Immediate neighbors of the HECN: their distance to the HECN
														// is 1
				{
					// add the traffic generated by the node i itself
					double traffic = ithSensor.getTrafficLoad();
					ithSensor.setTrafficLoad(traffic + 1.0);
					// update maximum traffic load if necessary
					maxLoad = Math.max(maxLoad, ithSensor.getTrafficLoad());

					// calculate the energy consumed to SEND the data
					if (sumSqDist != 0.0) {
						sqAreaBetweenSensors = areaBetweenTwoLocations(ithSensor.getLocation(), problem.getHecnLocation());
						double nodeEnergy = ithSensor.getTrafficLoad() * (sqAreaBetweenSensors);
						Iterator<Location> it = energyPoints.iterator();
						while (it.hasNext()) {
							Location tmp = it.next();
							if (areaBetweenTwoLocations(ithSensor.getLocation(), tmp) < energyRadius) {
								nodeEnergy = 0;
							}
						}
						maxEnergy = Math.max(maxEnergy, nodeEnergy);
					}
				}
			}
		}
	}
	

	/**
	 * Evaluates the constraint overhead of a solution
	 * 
	 * @param solution The solution
	 * @throws JMException
	 */
	public void evaluateConstraints(Solution solution) {
		System.out.println("Constraints are evaluated together with the objectives!");
		System.exit(-1);
	} // evaluateConstraints

	public int localImprovement(Solution solution, double threshold) {
		// First, evaluate the solution so as to initialize all the variables properly
		// -> if evaluation is ensured before, this could be skipped
		int evaluations = 0;

		Pair<Integer, Boolean> results;

		do {
			results = changeOnePair(solution, threshold);
			evaluations += results.getFirst();
		} while (results.getSecond());

		return evaluations;

	}

	public Pair<Integer, Boolean> changeOnePair(Solution sol, double threshold) {

		// First, evaluate the solution so as to initialize all the variables properly
		// -> if evaluation is ensured before, this could be skipped
		int evaluations = 0;
		// already evaluated... this.evaluate(solution);
		// DecisionVariables network = solution.getDecisionVariables();
		boolean changed = false;
		Solution rec = sol.deepCopy();
		SensorsSolution recovery = new SensorsSolution(sol);
		SensorsSolution solution = new SensorsSolution(sol);

		for (int i = 0; (i < solution.getNumberOfDeployeableSensors()) && !changed; i++) {
			Sensor s = solution.getSensorsList().get(i);
			if (s.isDeployed()) {
				List<Integer> relayChildren = s.getChildrenRelayNeighbors();
				for (int j = 0; (j < relayChildren.size()) && !changed; j++) {
					Sensor child = solution.getSensorsList().get(relayChildren.get(j));
					if (areaBetweenTwoLocations(s.getLocation(), child.getLocation()) < threshold) {
						if (changeThisPair(sol, i, j)) {
							problem.evaluate(sol);
							evaluations++;
							// cagada condition
							/**
							if (recovery.getOverallConstraintViolation() > solution.getOverallConstraintViolation()) {
								System.out.println("La cagamos!" + recovery.getOverallConstraintViolation() + " "
										+ solution.getOverallConstraintViolation());
								System.out.println(recovery.getDecisionVariables());
								System.out.println();
								System.out.println(solution.getDecisionVariables());
								// changeThisPairP(recovery,i,j);
								System.exit(-1);
							}
							*/
							System.out.println("Chequeo de constraints");
							// recover condition
							if (rec.getObjective(1) < sol.getObjective(1)) {
								// solution = recovery;
								//Hernan agrega
								sol = rec.deepCopy();
								problem.evaluate(sol);
//								Variable[] network = new Variable[recovery.getNumberOfDeployedSensors()];
//								for (int r = 0; r < recovery.getNumberOfDeployedSensors(); r++) {
//									network[r] = new SensorsSolution(recovery).getSensorsList().get(r).deepCopy();
//								}
//								solution.setDecisionVariables(network);
								
//							  System.out.println("Recovery done 1 " +  i + " " + j);
								// break;
								// List<Integer> auxRelayChildren = new ArrayList<Integer>(relayChildren);
								List<Integer> auxRelayChildren = new ArrayList<Integer>(
										((Sensor) recovery.getSensorsList().get(i)).getChildrenRelayNeighbors());
								relayChildren = ((Sensor) solution.getSensorsList().get(i)).getChildrenRelayNeighbors();
								if (!relayChildren.equals(auxRelayChildren)) {
									System.out.println("La cagamos otra vez a! " + i + " " + j);
									System.out.println(auxRelayChildren + " " + relayChildren);
									System.exit(-1);
								}
							} else {
								changed = true;
								// System.out.println(i + " " + j);
							}
						} else {
							// solution = recovery;
							sol = rec.deepCopy();
							problem.evaluate(sol);
//							
//							Variable[] network = new Variable[recovery.getDecisionVariables().length];
//							for (int r = 0; r < recovery.getDecisionVariables().length; r++) {
//								network[r] = recovery.getSensorsList().get(r).deepCopy();
//							}
//							solution.setDecisionVariables(network);
							//this.evaluate(solution);
//						  System.out.println("Recovery done 2 " +  i + " " + j);
							// break;
//						List<Integer> auxRelayChildren = new ArrayList<Integer>(relayChildren);
							List<Integer> auxRelayChildren = new ArrayList<Integer>(
									((Sensor) recovery.getSensorsList().get(i)).getChildrenRelayNeighbors());
							relayChildren = ((Sensor) solution.getSensorsList().get(i)).getChildrenRelayNeighbors();
							if (!relayChildren.equals(auxRelayChildren)) {
								System.out.println("La cagamos otra vez a! " + i + " " + j);
								System.out.println(auxRelayChildren + " " + relayChildren);
								System.exit(-1);
							}
						}
					}
				}
			}
		}

		Pair<Integer, Boolean> p = new Pair<Integer, Boolean>(evaluations, changed);
		return p;
	}

	private boolean changeThisPair(Solution solution, int sensorA, int sensorB){
		// initialize the new working terrain
		short[][] workingTerrain = new short[sfDim.getGridSizeX()][sfDim.getGridSizeY()];
		for (int i = 0; i < sfDim.getGridSizeX(); i++) {
			for (int j = 0; j < sfDim.getGridSizeY(); j++) {
				workingTerrain[i][j] = 0;
			}
		}

		// idenfify all pairs of nodes with distance below threshold
		// double threshold = 15.0;
		ArrayList<Sensor> network = new SensorsSolution(solution).getSensorsList();

		boolean stageOneSucceeded = false;
		boolean stageTwoSucceeded = false;
		Sensor s = network.get(sensorA);
		List<Integer> relayChildren = s.getChildrenRelayNeighbors();
		// this.evaluate(solution);
		Sensor child = network.get(relayChildren.get(sensorB));

//	  look for "1" in the terrain
		int numberOfDeployments = 0;
		numberOfDeployments += lookForOnesAndDeploy(s, workingTerrain);
		numberOfDeployments += lookForOnesAndDeploy(child, workingTerrain);

		// it does only matter if any deployment has been performed

		if (numberOfDeployments > 0) {
			// TODO: optimizable
//		  System.out.println("numberOfDeployments > 0");
			for (int x = 0; x < sfDim.getGridSizeX(); x++) {
				for (int y = 0; y < sfDim.getGridSizeY(); y++) {
					if (workingTerrain[x][y] == numberOfDeployments) {
//					  System.out.println("Overlapping at (" +  x + "," + y +")");
//					  BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
//					  try {String linea = br.readLine();}
//					  catch (Exception e) {}
						stageOneSucceeded = true;
					}
				}
			}

		} else {
//		  System.out.println("All the terrain is available");
			stageOneSucceeded = true;
		}

		if (stageOneSucceeded) {
			// Phase Two
			// look for all the children from "s" and "child"
			int numberOfChildren = 0;
			// checking conectivity of "s" children
			List<Integer> problematicChildren = getProblematicChildren(s, relayChildren.get(sensorB), network);
			problematicChildren.addAll(getProblematicChildren(child, relayChildren.get(sensorB), network));
			numberOfChildren = problematicChildren.size();
			// System.out.println("Problematic Children = " + problematicChildren);

			// operates on problematic children
			for (int problem = 0; problem < numberOfChildren; problem++) {
				Sensor problemChild = network.get(problematicChildren.get(problem));
				deploy(problemChild.getLocation().getPosX(), problemChild.getLocation().getPosY(), commRadius, workingTerrain);
			}

			// conditional deployment of parents
			List<Integer> allParents = new ArrayList<Integer>(s.getHopRelayNeighbors());
			for (int parent = 0; parent < child.getHopRelayNeighbors().size(); parent++) {
				if (child.getHopRelayNeighbors().get(parent) != sensorA) {
					allParents.add(child.getHopRelayNeighbors().get(parent));
				}
			}

			for (int parent = 0; parent < allParents.size(); parent++) {
				Sensor p = (Sensor) network.get(allParents.get(parent));
				int condition = numberOfDeployments + numberOfChildren;
				conditionalDeploy(p.getLocation().getPosX(), p.getLocation().getPosY(), commRadius, condition, workingTerrain);
			}

			// TODO: optimizable
			List<Location> candidates = new ArrayList<Location>();
			for (int x = 0; x < sfDim.getGridSizeX(); x++) {
				for (int y = 0; y < sfDim.getGridSizeY(); y++) {
					if (workingTerrain[x][y] == numberOfDeployments + numberOfChildren + 1) {
						// now, we have to link the children to any parent
//					  System.out.println("(" + x + "," + y + ") -> is covered by all children and one parent");
						Location p = new Location(x, y);
						candidates.add(p);
					}
				}
			}

			if ((numberOfDeployments > 0) || (numberOfChildren > 0)) {
				if (candidates.size() > 0) {
					int randomCandidate = PseudoRandom.randInt(0, candidates.size() - 1);
					s.getLocation().setPosX(candidates.get(randomCandidate).getPosX());
					s.getLocation().setPosY(candidates.get(randomCandidate).getPosY());
					child.setDeployed(false);
					stageTwoSucceeded = true;
				}
			} else {
				s.setDeployed(false);
				child.setDeployed(false);
				stageTwoSucceeded = true;
			}
		} else {
			// System.out.println("Phase 1 a tomar por culo: skipping phase 2");
		}

		return stageTwoSucceeded;
	}

//private void printWorkingTerrain(short[][] workingTerrain) {
//	// TODO Auto-generated method stub
//	
//	for (int i = 0; i < terrainSideSize_; i++)
//	  {
//		  for (int j = 0; j < terrainSideSize_; j++)
//		  {
//			  System.out.print(workingTerrain[i][j] + " ");
//		  }
//		  System.out.println();
//	  }
//}

	private void conditionalDeploy(int X, int Y, int radius, int condition, short[][] workingTerrain) {
		for (int x = X - radius; x <= X + radius; x++) {
			if ((x >= 0) && (x < sfDim.getGridSizeX())) // x value within range
			{
				int yRange = (int) Math.sqrt((double) getSqSensorsRatio() - (X - x) * (X - x));
				for (int y = Y - yRange; y <= Y + yRange; y++) {
					if ((y >= 0) && (y < sfDim.getGridSizeY())) // y value within range
					{
						if (workingTerrain[x][y] == condition)
							workingTerrain[x][y]++;
					}
				}
			}
		}

	}

	private List<Integer> getProblematicChildren(Sensor s, Integer exceptChildID, ArrayList<Sensor> network) {
		// TODO Auto-generated method stub

		List<Integer> problematic = new ArrayList<Integer>();

		for (int i = 0; i < s.getChildrenRelayNeighbors().size(); i++) {
			Sensor child = network.get(s.getChildrenRelayNeighbors().get(i));
			if ((child.getHopRelayNeighbors().size() == 1) && (s.getChildrenRelayNeighbors().get(i) != exceptChildID))
				problematic.add(s.getChildrenRelayNeighbors().get(i));
		}

		return problematic;
	}

	private int lookForOnesAndDeploy(Sensor s, short[][] workingTerrain) {
		int deployments = 0;
		double sqSensRadius = sensingRadius * sensingRadius;

		for (int x = s.getLocation().getPosX() - sensingRadius; x <= s.getLocation().getPosX() + sensingRadius; x++) {
			if ((x >= 0) && (x < sfDim.getGridSizeX())) // x value within range
			{
				int yRange = (int) Math.sqrt((double) sqSensRadius - (s.getLocation().getPosX() - x) * (s.getLocation().getPosX() - x));
				for (int y = s.getLocation().getPosY() - yRange; y <= s.getLocation().getPosY() + yRange; y++) {
					if ((y >= 0) && (y < sfDim.getGridSizeY())) // y value within range
					{
						terrain[x][y]--;
						if (terrain[x][y] == 0) {
							deploy(x, y, sensingRadius, workingTerrain);
							deployments++;
						}
					}
				}
			}
		}
		return deployments;
	}

	private void deploy(int X, int Y, int radius, short[][] workingTerrain) {
		double sqSensRadius = radius * radius;

		for (int x = X - radius; x <= X + radius; x++) {
			if ((x >= 0) && (x < sfDim.getGridSizeX())) // x value within range
			{
				int yRange = (int) Math.sqrt((double) sqSensRadius - (X - x) * (X - x));
				for (int y = Y - yRange; y <= Y + yRange; y++) {
					if ((y >= 0) && (y < sfDim.getGridSizeY())) // y value within range
					{
						workingTerrain[x][y]++;
					}
				}
			}
		}
	}
}
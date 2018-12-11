import java.util.ArrayList;

/**
 * La estrategia a utilizar consiste en copiar todos los datos de las variables de solution (locations y statuses) y
 * pasarlos a un arrayList de sensores. Todas las modificaciones y los calculos se realizaran sobre el arraylist de
 * sensores. 
 * NOOOO. No se modifica la solucion, solo se evalua.
 * Cuando se requiera volcar los valores de las modificaciones en el campo, se lo hara con el método close().
 */

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

public class SensorsSolution{
	private final Solution solution;
	private ArrayList<Sensor> sensorsList;
	
	public SensorsSolution(Solution solution) {
		this.solution = solution;
		this.generateSensorsArrayList();
	}
	
	//Hay que cambiarla para que tome 
	private void generateSensorsArrayList(){
		Object attribute = solution.getAttribute(SensorsProblem.attrName);
		ArrayList<Sensor> sensorsList = new ArrayList<>();
		if(attribute instanceof ArrayList<?>) {
			@SuppressWarnings("unchecked")
			ArrayList<Location> locationsList = (ArrayList<Location>) attribute;
			for(int i=0; i<locationsList.size(); i++) {
				Location loc = locationsList.get(i);
				sensorsList.add(new Sensor(loc, getStatusArray()[i]));
			}
			this.sensorsList = sensorsList;
		}
		else {
			System.err.println("No se pudo castear el atributo del hashmap");
		}
	}
	
	private boolean[] getStatusArray() {
		return EncodingUtils.getBinary(solution.getVariable(0));
	}

	public ArrayList<Sensor> getSensorsList() {
		return sensorsList;
	}
	
	public int getNumberOfDeployeableSensors() {
		return getStatusArray().length;
	}
	
	public int getNumberOfDeployedSensors() {
		boolean statusArray[] = getStatusArray();
		int numberOfDeployedSensors = 0;
		for(boolean status: statusArray) {
			if(status) numberOfDeployedSensors++;
		}
		return numberOfDeployedSensors;
	}
	
	//Todos los indices se manejan como si fuese un array de Locations, se arranca en 0.
	
	/**
	 * public Location getLocation(int pos) {
		Integer posX = EncodingUtils.getInt(solution.getVariable(1+pos*2));
		Integer posY = EncodingUtils.getInt(solution.getVariable(1+pos*2+1));
		return new Location(posX, posY);
	}

	public int numberOfVariables() {
		return solution.getNumberOfVariables()-1;
	}

	public void setStatus(int pos, boolean status) {
		boolean[] statusArray = getStatusArray();
		statusArray[pos] = status;
		EncodingUtils.setBinary(solution.getVariable(0), statusArray);
	}
	
	public boolean getStatus(int pos) { ;
		return getStatusArray()[pos];
	}
	
	public void setLocation(int pos, Location loc) {
		EncodingUtils.setInt(solution.getVariable(1+pos*2), loc.getPosX());
		EncodingUtils.setInt(solution.getVariable(1+pos*2+1), loc.getPosY());
	}
	
	public static Location[] generateArray(Solution solution){
		boolean status[] = EncodingUtils.getBinary(solution.getVariable(0));
		int alleleLength = status.length;
		Location[] locations = new Location[alleleLength];
		int i = 0;
		for(int j=1; j<=alleleLength*2; j=j+2) {
			locations[i] = null;
			if(status[i]) {
				int xLoc = EncodingUtils.getInt(solution.getVariable(j));
				int yLoc = EncodingUtils.getInt(solution.getVariable(j+1));
				Location loc = new Location(xLoc, yLoc);
				locations[i] = loc;
			}
			i++;
		}
		return locations;
	}
	*/
}

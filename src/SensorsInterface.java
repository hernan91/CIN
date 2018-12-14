import java.util.ArrayList;

/**
 * La estrategia a utilizar consiste en copiar todos los datos de las variables de solution (locations y statuses) y
 * pasarlos a un arrayList de sensores. Todas las modificaciones y los calculos se realizaran sobre el arraylist de
 * sensores. 
 * NOOOO. No se modifica la solucion, solo se evalua.
 * Cuando se requiera volcar los valores de las modificaciones en el campo, se lo hara con el método close().
 */

import org.moeaframework.core.variable.EncodingUtils;

public class SensorsInterface{
	private final SensorsSolution solution;
	private ArrayList<Sensor> sensorsList;
	
	public SensorsInterface(SensorsSolution solution) {
		this.solution = solution;
		this.generateSensorsArrayList();
	}

	private void generateSensorsArrayList(){
		ArrayList<Sensor> sensorsList = new ArrayList<>();
		ArrayList<Location> locationsList = this.solution.getLocationsList();
		for(int i=0; i<locationsList.size(); i++) {
			Location loc = locationsList.get(i);
			sensorsList.add(new Sensor(loc, getStatusArray()[i]));
		}
		this.sensorsList = sensorsList;
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
}

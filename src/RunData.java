import java.util.ArrayList;
import java.util.List;

public class RunData {
	static int numberOfSeeds = 30; //20
	/**
	 * En single core, si se le pone mas evaluaciones que popSize, entonces no se puede castear el attr. Parece que se pierde.
	 * En multi thread, siempre se pierde el attr.
	 */
	static final int MAX_NUM_THREADS = 6;
	static int maxEvaluations = 1000; //10000 //
	static float[] cpRates = {0.9f};//{0.1f, 0.3f, 0.5f, 0,7f, 0.9f};
	static float[] mpRates = {0.9f};//{0.1f, 0.3f, 0.5f, 0,7f, 0.9f};
	static int popSize = 100;
	
	//static int sizeX = 250, sizeY = 250;
	static Location[] gridSizes = {
			new Location(250,250), 
			new Location(500,500),
			new Location(700,700)
	};
	static int sensingRadius = 30, commRadius = 30, maxSensors = 50; //500
	static int energyRadius = 0;
	static ArrayList<Location> energyPoints = new ArrayList<>();
	static String[] operators = {"hux+bf"}; //"hux+bf"
	//energyPoints.add(new Location(20,20));
	//No se trabajo con HECNs 
	
	
	//static SensorsFieldDimensions sfDim = new SensorsFieldDimensions(sensingRadius, sizeX, sizeY);
	static Location hecnLocation = new Location(30, 30);
	
	static List<String> algNames = List.of("SPEA2", "NSGAII", "MOCell");// 
	static SensorInformation sInf = new SensorInformation(energyRadius, sensingRadius, commRadius);
	
	static String baseOutPutDir = "/home/hernan/Documentos/CIN/SalidaPruebas";
}
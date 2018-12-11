
/**
 * 
 * No olvidarse de usar -Xmx512m CommandName al ejecutar el .jar para tener mas memoria (512mb en este caso)
 * 
 * @author hernan
 *
 */
public class Main {
	public static void main(String[] args) {
		new Runner().run();
	}
}




//OperatorFactory.getInstance().addProvider(new MyOperatorProvider());
	//SensorsFieldData sensorsFieldData = new SensorsFieldData(5, 60, 60);
	//SensorsProblem sensorsProblem = new SensorsProblem(40, 1, sensorsFieldData);
	
	
//	Instrumenter instrumenter = new Instrumenter();
//	instrumenter.withFrequency(1);
//	instrumenter.attachAll();

	//executor.withProblem("SensorsProblem");
	//executor.withProblem("UF1");
	
	//Simulated binary crossover (SBX) operator. SBX attempts to simulate the offspring distribution of binary-encoded single-point crossover on real-valued decision variables. An example of this distribution, which favors offspring nearer to the two parents, is shown below.
	//Half-uniform crossover (HUX) operator. Half of the non-matching bits are swapped between the two parents.
	//Polynomial mutation (PM) operator. PM attempts to simulate the offspring distribution of binary-encoded bit-flip mutation on real-valued decision variables. Similar to SBX, PM favors offspring nearer to the parent.
	//Bit flip mutation operator. Each bit is flipped (switched from a 0 to a 1, or vice versa) using the specified probability.
	//Real-binary-real-binary
	//Utilizo una mezcla de operadores binarios y numéricos correspondientes a la codificación del problema
//	executor.withProperty("sbx.rate", cp);
//	executor.withProperty("hux.rate", cp);
//	executor.withProperty("pm.rate", mp);
//	executor.withProperty("bf.rate", mp);
//	executor.withInstrumenter(instrumenter);
//	double numberOfDeployedSensors = 0;
//	double energy = 0;
//	analyzer.printAnalysis();
//	
	//plot.setXLim(0, 1000);
	//plot.setYLim(0, 1000);
	
//	Accumulator accumulator = instrumenter.getLastAccumulator();
//	for (int i=0; i<accumulator.size("NFE"); i++) {
//		System.out.println(accumulator.get("NFE", i) + "\t" +
//		accumulator.get("GenerationalDistance", i));
//	}
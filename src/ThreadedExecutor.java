import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

import org.moeaframework.Analyzer;
import org.moeaframework.Analyzer.AnalyzerResults;
import org.moeaframework.Executor;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.NondominatedPopulation;

public class ThreadedExecutor implements Callable<ThreadOutputData> {
	private int threadNum;
	private SensorsFieldDimensions sfDim;
	private String algName;
	private float cpRate;
	private float mpRate;
	private String operators;
	private float executionTimeInSeconds = 0;
	
	public ThreadedExecutor(int threadNum, String algName, float cpRate, float mpRate, Location gridSize, String operator) {
		this.threadNum = threadNum;
		this.algName = algName;
		this.sfDim = new SensorsFieldDimensions(RunData.sensingRadius, gridSize.getPosX(), gridSize.getPosY());
		this.cpRate = cpRate;
		this.mpRate = mpRate;
		this.operators = operator;
	}
	
	
	public ThreadOutputData call() throws Exception {
		ThreadProgress progressListener = new ThreadProgress(threadNum);
		Executor executor = new Executor()
			.withMaxEvaluations(RunData.maxEvaluations)
			.withAlgorithm(algName)
			.withProblemClass(SensorsProblem.class, sfDim, RunData.hecnLocation, RunData.energyPoints, RunData.sInf, RunData.maxSensors)
			.withProperty("populationSize", RunData.popSize)
			.withProperty("hux+bf", operators) //sbx+hux+pm+bf   "hux+bf"
			.withProperty("hux.rate", cpRate)
			.withProperty("bf.rate", mpRate)
			.withProgressListener(progressListener);
			//.distributeOnAllCores();
		long startTime = System.nanoTime();
		List<NondominatedPopulation> pops = executor.runSeeds(RunData.numberOfSeeds);
		long endTime = System.nanoTime();
		executionTimeInSeconds = (endTime - startTime)/1000000000f;  //divide by 1000000000 to get seconds.
		MetricResults statisticalResults = getStatisticalResults(pops);
		if(statisticalResults!=null) {
			List<Plot> plotList = getPlots(pops); 
			ThreadOutputData threadOutputData = new ThreadOutputData(algName, executionTimeInSeconds, operators, cpRate, mpRate, 
					sfDim.getGridSizeX(), sfDim.getGridSizeY(), statisticalResults, plotList);
			return threadOutputData;
		}
		else {
			System.err.println("No existen suficientes soluciones no dominadas para el thread "+threadNum);
			return null;
		}
	}
	
	private MetricResults getStatisticalResults(List<NondominatedPopulation> pops) {
		MetricResults metricResults = new MetricResults();
		Analyzer analyzer = getAnalyzerWithConfig();
		ArrayList<NondominatedPopulation> fullPops = new ArrayList<>();
		for(NondominatedPopulation pop: pops) {
			if(pop.size()>=2) fullPops.add(pop);
		}
		if(fullPops.size()==0) return null;
		analyzer.addAll(Integer.toString(threadNum), fullPops);
		AnalyzerResults results = analyzer.getAnalysis();
		StringTokenizer lineTokenizer = new StringTokenizer(results.toString(), "\n");
		boolean firstLine = true;
		while(lineTokenizer.hasMoreTokens()) {
			String line = lineTokenizer.nextToken();
			if(firstLine) {
				String[] data = line.split(":");
				metricResults.setName(data[0]);
				System.out.println("Nombre de la metrica: "+data[0]);
				firstLine = false;
			}
			else {
				String[] data = line.split(":");
				String name = data[0];
				Float value = Float.parseFloat(data[1]);
				metricResults.setStatistic(name, value);
				System.out.println("Dato de la metrica: "+data[0]);
				System.out.println("Valor de la metrica "+data[1]);
			}
		}
		return metricResults;
	}


	public List<Plot> getPlots(List<NondominatedPopulation> pops) {
		List<Plot> plots = new ArrayList<>();
		for(NondominatedPopulation pop: pops) {
			Plot plot = new Plot();
			System.out.println(pop.size());
			plot.add(algName, pop);
			plots.add(plot);
		}
		return plots;
	}
	
	private Analyzer getAnalyzerWithConfig() {
		return new Analyzer()
				.includeAllMetrics()
				.showStatisticalSignificance()
				.withProblemClass(SensorsProblem.class, sfDim, RunData.hecnLocation, RunData.energyPoints, RunData.sInf, RunData.maxSensors);
	}
	
	public boolean hasEnoughNondominatedSolutionsInEveryPop(List<NondominatedPopulation> pops) {
		for(NondominatedPopulation pop: pops) {
			if(pop.size()<2) return false;
		}
		return true;
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	public void writeRunGeneralData() {
//		FileWriter fileWriter = null;
//		File dir = new File(RunData.everyTimeOutPutDataDir);
//		try {
//			dir.mkdirs();
//			fileWriter = new FileWriter(new File(dir.getAbsolutePath(), "runGeneralData"+".html"));
//			fileWriter.append("<table><thead><h4>Datos adicionales del problema</h4></thead><tbody><tr><th>HecnLocation</th><td>"+RunData.hecnLocation.toString()+"</td></tr><tr><th>Energy radius</th><td>"+RunData.sInf.getEnergyRadius()+"</td></tr><tr><th>Sensing radius</th><td>"+RunData.sInf.getSensingRadius()+"</td></tr><tr><th>Communication radius</th><td>"+RunData.sInf.getCommRadius()+"</td></tr><tr><th>Número máximo de sensores</th><td>"+RunData.maxSensors+"</td></tr></tbody></table><table><thead><h4>Datos adicionales del algoritmo</h4></thead><tbody><tr><th>Tamaño de la población</th><td>"+RunData.popSize+"</td></tr><tr><th>Numero de semillas</th><td>"+RunData.numberOfSeeds+"</td></tr></tbody></table>");
//			fileWriter.append("<style>table, th, td { border: 1px solid black; border-collapse: collapse;}th, td { padding: 5px; text-align: left;}");
//		
//		} catch (Exception e) {
//			System.out.println("Error");
//			e.printStackTrace();
//		} finally {
//			try {
//				fileWriter.flush();
//				fileWriter.close();
//			} catch (IOException e) {
//				System.out.println("Error while flushing/closing fileWriter !!!");
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	public void copyFile(String inputDir, String outPutDir) {
//		Path outputPath = Paths.get(outPutDir);
//		File inputFile = new File(inputDir);
//	    Path inputPath = inputFile.toPath();
//	    try {
//			Files.copy(inputPath, outputPath, StandardCopyOption.REPLACE_EXISTING);
//		} catch (IOException e) {
//			System.err.println("No se pudo copiar el archivo");
//			e.printStackTrace();
//		}
//	}
//	
//	public void sendThisThreadMetrics(List<NondominatedPopulation> pops) {
//		copyFile("templates/dataTemplate.html", outputDirectory+"/"+threadNum+".html");
//		File threadFile = new File(outputDirectory+"/"+threadNum+".html");
//		FileWriter threadFileWriter = null;
//		boolean enaughPops = hasEnoughNondominatedSolutionsInEveryPop(pops);
//		Analyzer analyzer = new Analyzer()
//				.includeAllMetrics()
//				.showStatisticalSignificance()
//				.withProblemClass(SensorsProblem.class, sfDim, RunData.hecnLocation, RunData.energyPoints, RunData.sInf, RunData.maxSensors);
//		try {
//			threadFile.mkdirs();
//			threadFileWriter = new FileWriter(threadFile);
//			threadFileWriter.append("");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	public void sendThisThreadMetrics(List<NondominatedPopulation> pops) {
//		copyFile("templates/dataTemplate.html", outputDirectory+"/"+threadNum+".html");
//		File threadFile = new File(outputDirectory+"/"+threadNum+".html");
//		FileWriter threadFileWriter = null;
//		boolean enaughPops = hasEnoughNondominatedSolutionsInEveryPop(pops);
//		Analyzer analyzer = new Analyzer()
//				.includeAllMetrics()
//				.showStatisticalSignificance()
//				.withProblemClass(SensorsProblem.class, sfDim, RunData.hecnLocation, RunData.energyPoints, RunData.sInf, RunData.maxSensors);
//		try {
//			threadFile.mkdirs();
//			threadFileWriter = new FileWriter(threadFile);
//			threadFileWriter.append("");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}	
	
//	public void writeRunResults(List<NondominatedPopulation> pops) {
//		if(threadNum==1) {
//			writeRunGeneralData();
//		}
//		
//		FileWriter threadImgFile = null;
//		
//		File imgFile = new File(outputDirectory+"/plots"+"/"+threadNum);
//		
//		try {
//			
//			imgFile.mkdirs();
//			
//			threadImgFile = new FileWriter(new File(imgFile.getAbsolutePath(), "/"+i+".img"));
//			
//			
//			if(enaughPops) {
//				analyzer.addAll(algName, pops);
//				AnalyzerResults results = analyzer.getAnalysis();
//				StringTokenizer lineTokenizer = new StringTokenizer(results.toString(), "\n");
//				boolean first = false;
//				while(lineTokenizer.hasMoreTokens()) {
//					if(first) fileWriter.append(lineTokenizer.nextToken());
//					else {
//						fileWriter.append("<tr>");
//						String line = lineTokenizer.nextToken();
//						String[] data = line.split(":");
//						fileWriter.append("<th>"+data[0]+"</th>");
//						fileWriter.append("<td>"+data[1]+"</td>");
//						fileWriter.append("</tr>");
//					}
//				}
//				fileWriter.append("</tbody></table>");
//			}
//			else System.err.println("No existen suficientes soluciones no dominadas para "+outPutfileName);
//			fileWriter.append("<table><thead><h4>Datos adicionales de la corrida</h4></thead><tbody><tr><th>Duracion</th><td>"+executionTimeInSeconds/60+" min</td></tr><tr><th>Operadores</th><td>"+operator+"</td></tr><tr><th>Proporción de cruza</th><td>"+cpRate+"</td></tr><tr><th>Proporcion de mutación</th><td>"+mpRate+"</td></tr><tr><th>Dimensiones de grilla</th><td>"+sfDim.toString()+"</td></tr></tbody></table>");
//			int i = 1;
//			for(NondominatedPopulation pop: pops) {
//				fileWriter.append("<div class='container'> <img src='"+i+".png' alt='Avatar' class='image'> <div class='overlay'> <div class='text'>Semilla nro "+i+"</div> </div></div>");
//				Plot plot = new Plot();
//				plot.add(algName, pop);
//				plot.save(outputDirectory+"/plots/"+threadNum+"/"+i+".png");
//				i++;
//			}
//			fileWriter.append("<style>table, th, td { border: 1px solid black; border-collapse: collapse;}th, td { padding: 5px; text-align: left;}.container { position: relative; width: 50%;}.image { display: block; width: 100%; height: auto;}.overlay { position: absolute; top: 0; bottom: 0; left: 0; right: 0; height: 100%; width: 100%; opacity: 0; transition: .5s ease; background-color: #008CBA;}.container:hover .overlay { opacity: 1;}.text { color: white; font-size: 20px; position: absolute; top: 50%; left: 50%; -webkit-transform: translate(-50%, -50%); -ms-transform: translate(-50%, -50%); transform: translate(-50%, -50%); text-align: center;}</style>");
//		} catch (Exception e) {
//			System.out.println("Error");
//			e.printStackTrace();
//		} finally {
//			try {
//				fileWriter.flush();
//				fileWriter.close();
//			} catch (IOException e) {
//				System.out.println("Error while flushing/closing fileWriter !!!");
//				e.printStackTrace();
//			}
//		}
//	}
	
	//Cada poblacion corresponde con una semilla

}

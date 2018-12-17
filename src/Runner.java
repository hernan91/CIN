import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Runner {
	private String outputDir;
	
	public void run() {
		this.outputDir = RunData.baseOutPutDir;
		Set<Callable<ThreadOutputData>> callables = getCallableThreadsList();
		ArrayList<ThreadOutputData> allThreadsResults = getAllThreadResults(callables);
		writeResults(allThreadsResults);
		
	}
	
	public Set<Callable<ThreadOutputData>> getCallableThreadsList() {
		Set<Callable<ThreadOutputData>> callables = new HashSet<Callable<ThreadOutputData>>();
		int threadNum = 1;
		for(Location gridSize: RunData.gridSizes) {
			for(String algName: RunData.algNames) {
				for(String operator: RunData.operators) {
					for(float cp: RunData.cpRates) {
						for(float mp: RunData.mpRates) {
							ThreadedExecutor thread = new ThreadedExecutor(threadNum, algName, cp, mp, gridSize, operator);
							callables.add(thread);
							threadNum++;
						}
					}
				}
			}
		}
		return callables;
	}
	
	public ArrayList<ThreadOutputData> getAllThreadResults(Set<Callable<ThreadOutputData>> callables) {
		ExecutorService executor = Executors.newFixedThreadPool(RunData.MAX_NUM_THREADS);
		List<Future<ThreadOutputData>> futureListOfResults = null;
		ArrayList<ThreadOutputData> listOfThreadsResults = new ArrayList<ThreadOutputData>();
		try {
			futureListOfResults = executor.invokeAll(callables);
			for(Future<ThreadOutputData> futureThreadResult : futureListOfResults){
				ThreadOutputData threadResult = futureThreadResult.get();
				if(threadResult!=null) listOfThreadsResults.add(threadResult);
			}
			if(listOfThreadsResults.size()==0) {
				throw new Exception("No existen resultados para analizar");
			}
		} catch (InterruptedException | ExecutionException e) {
			executor.shutdown();
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			executor.shutdown();
			e.getMessage();
		}
		if(listOfThreadsResults.size()==0) {
			System.err.println("No existen resultados por falta de soluciones no dominadas");
		}
		return listOfThreadsResults;
	}
	
	public void writeResults(ArrayList<ThreadOutputData> listOfThreadsResults) {
		writeRunConfigToHtml();
		writeRunStatisticsToCsv(listOfThreadsResults);
	}
	
	public void writeRunStatisticsToCsv(ArrayList<ThreadOutputData> listOfOutputData) {
		File dir = new File(outputDir);
		FileWriter fileWriter = null;
		try {
			dir.getParentFile().mkdirs();
			fileWriter = new FileWriter(new File(dir.getAbsolutePath()+"DatosEstadisticos.csv"));
			fileWriter.append("Algname, GridSizeX, GridSizeY, Operadores, PropCruz, PropMut, Duracion, Metrica");
			for(ThreadOutputData threadData : listOfOutputData) {
				String metricName = threadData.getStatisticalResults().getName();
				HashMap<String, Float> statiscticalData = threadData.getStatisticalResults().getStatistics();
				fileWriter.append(threadData.getAlgName()+ ","+
						threadData.getGridSizeX()+ ","+
						threadData.getGridSizeY()+ ","+
						threadData.getOperadores()+ ","+
						threadData.getProporcionCruza()+ ","+
						threadData.getProporcionMutacion()+ ","+
						threadData.getDuration()+ ","+
						metricName+ ","+
						parseStatisticaResultsToCsv(statiscticalData)
						);
			}
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}
		}
	}
	
	public String parseStatisticaResultsToCsv(HashMap<String, Float> statisticalData) {
		String out = "";
		if(statisticalData!=null) {
			statisticalData.forEach((name, data)->{
				out.concat(data.toString()+", ");
			});
		}
		return out.substring(0, out.length()-2);
	}
	
	public void writeRunConfigToHtml() {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(outputDir+"/"+"Config.html", "UTF-8");
			writer.println("<table><thead><h4>Datos adicionales del problema</h4></thead><tbody><tr><th>HecnLocation</th><td>"+RunData.hecnLocation.toString()+"</td></tr><tr><th>Energy radius</th><td>"+RunData.sInf.getEnergyRadius()+"</td></tr><tr><th>Sensing radius</th><td>"+RunData.sInf.getSensingRadius()+"</td></tr><tr><th>Communication radius</th><td>"+RunData.sInf.getCommRadius()+"</td></tr><tr><th>Número máximo de sensores</th><td>"+RunData.maxSensors+"</td></tr></tbody></table><table><thead><h4>Datos adicionales del algoritmo</h4></thead><tbody><tr><th>Tamaño de la población</th><td>"+RunData.popSize+"</td></tr><tr><th>Numero de semillas</th><td>"+RunData.numberOfSeeds+"</td></tr></tbody></table>");
			writer.println("<style>table, th, td { border: 1px solid black; border-collapse: collapse;}th, td { padding: 5px; text-align: left;}");		
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}
	
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
}

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bouncycastle.jce.provider.JDKDSASigner.ecDSA;

public class Runner {
	public void run() {
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
		return listOfThreadsResults;
	}
	
	public void writeResults(ArrayList<ThreadOutputData> listOfThreadsResults) {
		//ObjectSizeFetcher.getObjectSize(listOfThreadsResults);
	}
	
	
}

import java.util.HashMap;

public class MetricResults {
	private String name; //nombre de la métrica
	private HashMap<String, Float> statistics; //<Nombre de el dato estadistico, Valor del dato estadistico>
	
	public MetricResults(String name, HashMap<String, Float> statistics) {
		super();
		this.name = name;
		this.statistics = statistics;
	}

	public MetricResults() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, Float> getStatistics() {
		return statistics;
	}

//	private void setStatistics(HashMap<String, Float> statistics) {
//		this.statistics = statistics;
//	}
	
	public void setStatistic(String name, Float value) {
		this.statistics.put(name, value);
	}
}

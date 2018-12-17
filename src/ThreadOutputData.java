import java.util.List;

import org.moeaframework.analysis.plot.Plot;

public class ThreadOutputData {
	private float algName;
	private float duration;
	private String operadores;
	private float proporcionCruza;
	private float proporcionMutacion;
	private float gridSizeX;
	private float girdSizeY;
	private MetricResults metricsStatisticalResults;
	private List<Plot> plotList;
	
	public ThreadOutputData(String algName, float duration, String operadores, float proporcionCruza, float proporcionMutacion, float gridSizeX,
			float girdSizeY, MetricResults statisticalResults, List<Plot> plotList) {
		super();
		this.duration = duration;
		this.operadores = operadores;
		this.proporcionCruza = proporcionCruza;
		this.proporcionMutacion = proporcionMutacion;
		this.gridSizeX = gridSizeX;
		this.girdSizeY = girdSizeY;
		this.metricsStatisticalResults = statisticalResults;
		this.plotList = plotList;
	}

	public float getAlgName() {
		return algName;
	}

	public void setAlgName(float algName) {
		this.algName = algName;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public String getOperadores() {
		return operadores;
	}

	public void setOperadores(String operadores) {
		this.operadores = operadores;
	}

	public float getProporcionCruza() {
		return proporcionCruza;
	}

	public void setProporcionCruza(float proporcionCruza) {
		this.proporcionCruza = proporcionCruza;
	}

	public float getProporcionMutacion() {
		return proporcionMutacion;
	}

	public void setProporcionMutacion(float proporcionMutacion) {
		this.proporcionMutacion = proporcionMutacion;
	}

	public float getGridSizeX() {
		return gridSizeX;
	}

	public void setGridSizeX(float gridSizeX) {
		this.gridSizeX = gridSizeX;
	}

	public float getGridSizeY() {
		return girdSizeY;
	}

	public void setGridSizeY(float girdSizeY) {
		this.girdSizeY = girdSizeY;
	}

	public MetricResults getStatisticalResults() {
		return metricsStatisticalResults;
	}

	public void setStatisticalResults(MetricResults statisticalResults) {
		this.metricsStatisticalResults = statisticalResults;
	}

	public List<Plot> getPlotList() {
		return plotList;
	}

	public void setPlotList(List<Plot> plotList) {
		this.plotList = plotList;
	}
}

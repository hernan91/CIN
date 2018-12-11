public class SensorsFieldDimensions {
	private int gridSizeX;
	private int gridSizeY;
	
	public SensorsFieldDimensions(int sensorRatio, int gridSizeX, int gridSizeY) {
		super();
		this.gridSizeX = gridSizeX;
		this.gridSizeY = gridSizeY;
	}

	public int getGridSizeX() {
		return gridSizeX;
	}

	public void setGridSizeX(int gridSizeX) {
		this.gridSizeX = gridSizeX;
	}

	public int getGridSizeY() {
		return gridSizeY;
	}

	public void setGridSizeY(int gridSizeY) {
		this.gridSizeY = gridSizeY;
	}
	
	public int getGridSize() {
		return gridSizeX*gridSizeY;
	}
	
	@Override
	public String toString() {
		return gridSizeX+"x"+gridSizeY;
	}
}



//clase que representa una posicion en el plano coordenado x,y discretizado en forma de grilla
public class Location implements Comparable<Location>{
	private int posX;
	private int posY;
	
	public Location(int posX, int posY) {
		super();
		this.posX = posX;
		this.posY = posY;
	}
	
	public int getPosX() {
		return posX;
	}
	
	public void setPosX(int posX) {
		this.posX = posX;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public void setPosY(int posY) {
		this.posY = posY;
	}

	@Override
	public int compareTo(Location loc2) {
		if(this.getPosX()==loc2.getPosX() && this.getPosY()==loc2.getPosY()) return 0;
		return -1;
	}	
	
	@Override
	public String toString() {
		return "("+posX+","+" "+posY+")";
	}
}

package main.cells;

import main.cells.Cell.scanLocating;

public class Cell {
	
	public enum Status {
		VISITED,
		EMPTY,
		SCANED;
	}
	
	public enum Location {
		OUTSIDE,
		INSIDE,
		EDGE;
	}
	
	public enum scanLocating {
		SCANED,
		VISITED;
	}
	
	private int r, g, b;
	private double xWorld, yWorld;
	private int xMap, yMap;
	private Status status;
	private Location location;
	private Location neighboursCheck;
	private scanLocating scanLocating;
	private scanLocating scanLocatingToLocalize;
	
	public Cell(int r, int g, int b, int xMap, int yMap, double xWorld, double yWorld) {
		this.r = r;
		this.g = g;
		this.b = b;
		
		this.xWorld = xWorld;
		this.yWorld = yWorld;
		
		this.xMap = xMap;
		this.yMap = yMap;
		
		this.status = Status.EMPTY;
	}
	
	@Override
	public String toString() {
		return "(" +r + ", " + g + ", " + b + ")" + " : " + "(" + xMap + ", " + yMap + ")" + " : " + "(" + xWorld + ", " + yWorld + ")" + " : " + status;
	}
	
	public boolean isRedInTreshhold(int goul, int difference) {
		if (r > goul - difference) {
			if (r < goul + difference) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isGreenInTreshhold(int goul, int difference) {
		if (g > goul - difference) {
			if (g < goul + difference) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isBlueInTreshhold(int goul, int difference) {
		if (b > goul - difference) {
			if (b < goul + difference) {
				return true;
			}
		}
		return false;
	}

	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}

	public double getxWorld() {
		return xWorld;
	}

	public double getyWorld() {
		return yWorld;
	}

	public int getxMap() {
		return xMap;
	}

	public int getyMap() {
		return yMap;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public scanLocating getScanLocating() {
		return scanLocating;
	}

	public void setScanLocating(scanLocating scanLocating) {
		this.scanLocating = scanLocating;
	}

	public scanLocating getScanLocatingToLocalize() {
		return scanLocatingToLocalize;
	}

	public void setScanLocatingToLocalize(scanLocating scanLocatingToLocalize) {
		this.scanLocatingToLocalize = scanLocatingToLocalize;
	}

	public Location getNeighboursCheck() {
		return neighboursCheck;
	}

	public void setNeighboursCheck(Location neighboursCheck) {
		this.neighboursCheck = neighboursCheck;
	}
}

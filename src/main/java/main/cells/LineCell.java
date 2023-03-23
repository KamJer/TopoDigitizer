package main.cells;

public class LineCell extends Cell {
	
	private double value;
	private boolean edge;
	
	public LineCell(int r, int g, int b, int xMap, int yMap, double xWorld, double yWorld) {
		super(r, g, b, xMap, yMap, xWorld, yWorld);
	}
	
	public LineCell(Cell cell, double value) {
		super(cell.getR(), cell.getG(), cell.getB(), cell.getxMap(), cell.getyMap(), cell.getxWorld(), cell.getyWorld());
		this.value = value;
	}
	
	@Override
	public String toString() {
		return super.toString() + " : " + value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public boolean isEdge() {
		return edge;
	}

	public void setEdge(boolean edge) {
		this.edge = edge;
	}
}

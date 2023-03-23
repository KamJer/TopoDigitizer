package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import main.cells.Cell;
import main.cells.Cell.Status;
import main.cells.LineCell;

public class ScanRaster {
	private int r, g, b;
	private int difference;
	private BufferedImage image;
	private Cell[][] cells;
	private List<Cell> visited = new ArrayList<>();
	private List<Cell> toVisit = new ArrayList<>();

	public ScanRaster(GeoTiff topoDigitizer, int x, int y, int r, int g, int b, int difference, double z) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.difference = difference;

		cells = new Cell[(int) topoDigitizer.getSizeX()][topoDigitizer.getSizeY()];

		double currentX = topoDigitizer.getMinX() + (topoDigitizer.getCellxSize() / 2);
		double currentY = topoDigitizer.getMinY() + topoDigitizer.getHeight() + (topoDigitizer.getCellySize() / 2);

		for (int i = 0; i < topoDigitizer.getSizeY(); i++) {
			for (int j = 0; j < topoDigitizer.getSizeX(); j++) {
				int pixel = topoDigitizer.getImage().getRGB(j, i);
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;
				double xWorld = currentX;
				double yWorld = currentY;
					
				cells[j][i] = (new Cell(red, green, blue, j, i, xWorld, yWorld));
			}
		}
		
		System.out.println("zaczynam skan!!!");
		
//		odwiedzenie pierwszej komórki
		if (cells[x][y].isRedInTreshhold(r, difference)) {
			if (cells[x][y].isGreenInTreshhold(g, difference)) {
				if (cells[x][y].isBlueInTreshhold(b, difference)) {
					visited.add(cells[x][y]);
					cells[x][y].setStatus(Status.VISITED);
				}
			}
		}

//		skanowanie otoczenia pierwszej komórki
		int xToScan = visited.get(0).getxMap() - 1;
		int yToScan = visited.get(0).getyMap() - 1;
		scanCell(xToScan, yToScan);
//		if (cells[xToScan][yToScan].getStatus() != Status.VISITED
//				|| cells[xToScan][yToScan].getStatus() != Status.SCANED) {
//			cells[xToScan][yToScan].setStatus(Status.SCANED);
////			sprawdzanie czy odcieñ czerwonego pasuje do badanego zakresu
//			if (cells[xToScan][yToScan].isRedInTreshhold(r, difference)) {
////				sprawdzanie czy odcieñ zielonego pasuje do badanego zakresu
//				if (cells[xToScan][yToScan].isGreenInTreshhold(g, difference)) {
////					sprawdzanie czy odcieñ niebieskiego pasuje do badanego zakresu
//					if (cells[xToScan][yToScan].isBlueInTreshhold(b, difference)) {
////						dodanie zbadengo piksela do tisty do odwiedzenia
//						toVisit.add(cells[xToScan][yToScan]);
//					}
//				}
//			}
//		}

		xToScan = visited.get(0).getxMap();
		yToScan = visited.get(0).getyMap() - 1;
		scanCell(xToScan, yToScan);
		
		xToScan = visited.get(0).getxMap() + 1;
		yToScan = visited.get(0).getyMap() - 1;
		scanCell(xToScan, yToScan);
		
		xToScan = visited.get(0).getxMap() - 1;
		yToScan = visited.get(0).getyMap();
		scanCell(xToScan, yToScan);
		
		xToScan = visited.get(0).getxMap() + 1;
		yToScan = visited.get(0).getyMap();
		scanCell(xToScan, yToScan);

		xToScan = visited.get(0).getxMap() - 1;
		yToScan = visited.get(0).getyMap() + 1;
		scanCell(xToScan, yToScan);

		xToScan = visited.get(0).getxMap();
		yToScan = visited.get(0).getyMap() + 1;
		scanCell(xToScan, yToScan);

		xToScan = visited.get(0).getxMap() + 1;
		yToScan = visited.get(0).getyMap() + 1;
		scanCell(xToScan, yToScan);

		int xToVisit = toVisit.get(0).getxMap();
		int yToVisit = toVisit.get(0).getyMap();
		
		while (toVisit.size() != 0) {
//			odwiedzenie komórki
			visited.add(cells[xToVisit][yToVisit]);
			cells[xToVisit][yToVisit].setStatus(Status.VISITED);
			toVisit.remove(0);

//			skanowanie otoczenia odwiedzonej komórki
			xToScan = visited.get(visited.size() - 1).getxMap() - 1;
			yToScan = visited.get(visited.size() - 1).getyMap() - 1;
			scanCell(xToScan, yToScan);

			xToScan = visited.get(visited.size() - 1).getxMap();
			yToScan = visited.get(visited.size() - 1).getyMap() - 1;
			scanCell(xToScan, yToScan);

			xToScan = visited.get(visited.size() - 1).getxMap() + 1;
			yToScan = visited.get(visited.size() - 1).getyMap() - 1;
			scanCell(xToScan, yToScan);

			xToScan = visited.get(visited.size() - 1).getxMap() - 1;
			yToScan = visited.get(visited.size() - 1).getyMap();
			scanCell(xToScan, yToScan);

			xToScan = visited.get(visited.size() - 1).getxMap() + 1;
			yToScan = visited.get(visited.size() - 1).getyMap();
			scanCell(xToScan, yToScan);

			xToScan = visited.get(visited.size() - 1).getxMap() - 1;
			yToScan = visited.get(visited.size() - 1).getyMap() + 1;
			scanCell(xToScan, yToScan);

			xToScan = visited.get(visited.size() - 1).getxMap();
			yToScan = visited.get(visited.size() - 1).getyMap() + 1;
			scanCell(xToScan, yToScan);

			xToScan = visited.get(visited.size() - 1).getxMap() + 1;
			yToScan = visited.get(visited.size() - 1).getyMap() + 1;
			scanCell(xToScan, yToScan);

			if (toVisit.size() != 0) {
				xToVisit = toVisit.get(0).getxMap();
				yToVisit = toVisit.get(0).getyMap();
			}
		}
	}

	private void scanCell(int x, int y) {
		if (cells[x][y].getStatus() != Status.VISITED && cells[x][y].getStatus() != Status.SCANED) {
//			zaznaczenie ¿e dana komórka przesz³a proces skanowania
			cells[x][y].setStatus(Status.SCANED);
//			sprawdzanie czy odcieñ czerwonego pasuje do badanego zakresu
			if (cells[x][y].isRedInTreshhold(r, difference)) {
//				sprawdzanie czy odcieñ zielonego pasuje do badanego zakresu
				if (cells[x][y].isGreenInTreshhold(g, difference)) {
//					sprawdzanie czy odcieñ niebieskiego pasuje do badanego zakresu
					if (cells[x][y].isBlueInTreshhold(b, difference)) {
//						dodanie zbadengo piksela do listy do odwiedzenia
						toVisit.add(cells[x][y]);
					}
				}
			}
		}
	}
}

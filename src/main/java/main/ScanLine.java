package main;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import analitycalGeometry.AnalitycalGeometry;
import analitycalGeometry.objects.Line;
import main.cells.Cell;
import main.cells.Cell.Location;
import main.cells.Cell.Status;
import main.cells.Cell.scanLocating;
import vector.Vector;
import main.cells.LineCell;

public class ScanLine {
	private int r, g, b;
	private double z;
	private int rozdzelczosc;
	private int difference;
	private Cell[][] cells;
	private List<Cell> visited = new ArrayList<>();
	private List<Cell> toVisit = new ArrayList<>();
	private GeoTiff geoTiff;

	public ScanLine(GeoTiff geoTiff, int x, int y, int r, int g, int b, int difference, double z, int rozdzielczosc) {
		this.geoTiff = geoTiff;
		this.r = r;
		this.g = g;
		this.b = b;
		this.difference = difference;
		this.z = z;
		this.rozdzelczosc = rozdzielczosc;

		cells = new Cell[(int) geoTiff.getSizeX()][geoTiff.getSizeY()];

		double currentX = geoTiff.getMinX() + (geoTiff.getCellxSize() / 2);
		double currentY = geoTiff.getMinY() + geoTiff.getHeight() - (geoTiff.getCellySize() / 2);

		for (int i = 0; i < geoTiff.getSizeY(); i++) {
			for (int j = 0; j < geoTiff.getSizeX(); j++) {
				int pixel = geoTiff.getImage().getRGB(j, i);
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;

				cells[j][i] = (new Cell(red, green, blue, j, i, currentX, currentY));

				currentX += geoTiff.getCellxSize();
			}
			currentX = geoTiff.getMinX() + (geoTiff.getCellxSize() / 2);
			currentY -= geoTiff.getCellySize();
		}

		System.out.println("zaczynam skan!!!");

		int xToScan;
		int yToScan;

		int xToVisit = x;
		int yToVisit = y;

		do {
//			odwiedzenie komórki
//			LineCell lineCell = new LineCell(cells[xToVisit][yToVisit], z);
			visited.add(cells[xToVisit][yToVisit]);
			cells[xToVisit][yToVisit].setStatus(Status.VISITED);
//			lineCell.setStatus(Status.VISITED);
			if (toVisit.size() != 0) {
				toVisit.remove(0);
			}

			Location[] haveNeibhours = new Location[4];
//			skanowanie otoczenia odwiedzonej komórki
			xToScan = visited.get(visited.size() - 1).getxMap() - 1;
			yToScan = visited.get(visited.size() - 1).getyMap() - 1;
			scanCell(xToScan, yToScan);
			scan(xToScan, yToScan);

			xToScan = visited.get(visited.size() - 1).getxMap();
			yToScan = visited.get(visited.size() - 1).getyMap() - 1;
			scanCell(xToScan, yToScan);
			haveNeibhours[0] = scan(xToScan, yToScan);

			xToScan = visited.get(visited.size() - 1).getxMap() + 1;
			yToScan = visited.get(visited.size() - 1).getyMap() - 1;
			scanCell(xToScan, yToScan);
			scan(xToScan, yToScan);

			xToScan = visited.get(visited.size() - 1).getxMap() - 1;
			yToScan = visited.get(visited.size() - 1).getyMap();
			scanCell(xToScan, yToScan);
			haveNeibhours[1] = scan(xToScan, yToScan);

			xToScan = visited.get(visited.size() - 1).getxMap() + 1;
			yToScan = visited.get(visited.size() - 1).getyMap();
			scanCell(xToScan, yToScan);
			haveNeibhours[2] = scan(xToScan, yToScan);

			xToScan = visited.get(visited.size() - 1).getxMap() - 1;
			yToScan = visited.get(visited.size() - 1).getyMap() + 1;
			scanCell(xToScan, yToScan);
			scan(xToScan, yToScan);

			xToScan = visited.get(visited.size() - 1).getxMap();
			yToScan = visited.get(visited.size() - 1).getyMap() + 1;
			scanCell(xToScan, yToScan);
			haveNeibhours[3] = scan(xToScan, yToScan);

			xToScan = visited.get(visited.size() - 1).getxMap() + 1;
			yToScan = visited.get(visited.size() - 1).getyMap() + 1;
			scanCell(xToScan, yToScan);
			scan(xToScan, yToScan);

			if (haveNeibhours[0] == Location.OUTSIDE || haveNeibhours[1] == Location.OUTSIDE
					|| haveNeibhours[2] == Location.OUTSIDE || haveNeibhours[3] == Location.OUTSIDE) {
				visited.get(visited.size() - 1).setLocation(Location.EDGE);
			}

			if (toVisit.size() != 0) {
				xToVisit = toVisit.get(0).getxMap();
				yToVisit = toVisit.get(0).getyMap();
			}
		} while (toVisit.size() != 0);

		System.out.println(visited.size());
		List<Cell> edge = new ArrayList<>();
		for (int i = 0; i < visited.size(); i++) {
			if (visited.get(i).getLocation() == Location.EDGE) {
				edge.add(visited.get(i));
			}
		}
		System.out.println(edge.size());

		List<Cell> selected = findIntrestingOnes(x, y, 3);

		System.out.println(selected.size());

		generateShpFromCells(selected);
	}

	private void generateShpFromCells(List<Cell> selected) {
		SimpleFeatureType type = constractShp();

		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(type);
		List<SimpleFeature> features = new ArrayList<>();

//		Przepisywanie komórek na SimpleFeature
		for (int i = 0; i < selected.size(); i++) {
			Point point = geometryFactory
					.createPoint(new Coordinate(selected.get(i).getxWorld(), selected.get(i).getyWorld()));

			featureBuilder.add(point);
			featureBuilder.add(geoTiff.getSelectedFile().getName());
			featureBuilder.add(z);
			featureBuilder.add(i);
			SimpleFeature feature = featureBuilder.buildFeature(null);
			features.add(feature);
		}

		File newFile = creatShp(geoTiff.getSelectedFile());

		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

		Map<String, Serializable> params = new HashMap<>();
		try {
			params.put("url", newFile.toURI().toURL());
			params.put("create spatial index", Boolean.TRUE);

			ShapefileDataStore newDataStore = null;
			newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
			/*
			 * TYPE is used as a template to describe the file contents
			 */
			newDataStore.createSchema(type);

			Transaction transaction = new DefaultTransaction("create");
			String typeName = newDataStore.getTypeNames()[0];
			SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
			SimpleFeatureType shapeType = featureSource.getSchema();

			System.out.println("Shape: " + shapeType);
			if (featureSource instanceof SimpleFeatureStore) {
				SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

				SimpleFeatureCollection collection = new ListFeatureCollection(type, features);
				featureStore.setTransaction(transaction);
				try {
					featureStore.addFeatures(collection);
					transaction.commit();
				} catch (Exception e) {
					transaction.rollback();
				} finally {
					transaction.close();
				}
			} else {
				System.err.println(typeName + " lack od support");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void scanCell(int x, int y) {
		if (cells[x][y].getStatus() != Status.VISITED && cells[x][y].getStatus() != Status.SCANED) {
//			System.out.println(cells[x][y]);
//			zaznaczenie ¿e dana komórka przesz³a proces skanowania
			cells[x][y].setStatus(Status.SCANED);
//			sprawdzanie czy odcienie koloru pasuj¹ do badanego zakresu
			if (cells[x][y].isRedInTreshhold(r, difference) && cells[x][y].isGreenInTreshhold(g, difference)
					&& cells[x][y].isBlueInTreshhold(b, difference)) {
//				dodanie zbadengo piksela do listy do odwiedzenia
				LineCell lineCell = new LineCell(cells[x][y], z);
				lineCell.setStatus(Status.SCANED);
				toVisit.add(lineCell);
				lineCell.setValue(z);
//				System.out.println(cells[x][y]);
			}
		}
	}

	private Location scan(int x, int y) {
//		sprawdzanie czy odcienie koloru pasuj¹ do badanego zakresu
		if (cells[x][y].isRedInTreshhold(r, difference) && cells[x][y].isGreenInTreshhold(g, difference)
				&& cells[x][y].isBlueInTreshhold(b, difference)) {
//			sprawdzanie czy dana komórka zosta³a ju¿ oceniona jako edge, jeœli tak zwróæ Location.EDGE, jeœli nie ustawia Location jako INSIDE
			if (cells[x][y].getLocation() == Location.EDGE) {
				return Location.EDGE;
			} else {
				cells[x][y].setLocation(Location.INSIDE);
				return Location.INSIDE;
			}
		}
//		Jeœli test na to czy komórka ma odpowiedni kolor siê nie powiedzie to znaczy ¿e komórka jest poza lini¹ 
		cells[x][y].setLocation(Location.OUTSIDE);
		return Location.OUTSIDE;
	}

	/**
	 * metoda do szukania punktów œrodkowych w badaniej lini
	 * 
	 * @param x - pierwszy x punktu z którego szukane s¹ punkty œrodkowe
	 * @param y - pierwszy y punktu z którego szukane s¹ punkty œrodkowe
	 * @return listê punktów
	 */
	private List<Cell> findIntrestingOnes(int x, int y, int amount) {
		List<Cell> toScan = new ArrayList<>();
		List<Cell> selected = new ArrayList<>();
		int xToScan;
		int yToScan;

		int mX = x;
		int mY = y;
		// x i y bêd¹ znieniane w taki sposób by te by³y x i y komórki, która jest
		// komórk¹ w której jest zlokalizowany pkt œrodkowy
		boolean found = false;
		for (int i = 0; i < amount; i++) {
//			skanuje otoczenie komórki by znalezæ nastêpn¹ komórkê 
			do {
				xToScan = mX - 1;
				yToScan = mY - 1;
				if (!found) {
					found = metodaToFind(xToScan, yToScan, x, y, toScan);
				}

				xToScan = mX;
				yToScan = mY - 1;
				if (!found) {
					found = metodaToFind(xToScan, yToScan, x, y, toScan);
				}

				xToScan = mX + 1;
				yToScan = mY - 1;
				if (!found) {
					found = metodaToFind(xToScan, yToScan, x, y, toScan);
				}

				xToScan = mX - 1;
				yToScan = mY;
				if (!found) {
					found = metodaToFind(xToScan, yToScan, x, y, toScan);
				}

				xToScan = mX;
				yToScan = mY;
				if (!found) {
					found = metodaToFind(xToScan, yToScan, x, y, toScan);
				}

				xToScan = mX - 1;
				yToScan = mY + 1;
				if (!found) {
					found = metodaToFind(xToScan, yToScan, x, y, toScan);
				}

				xToScan = mX;
				yToScan = mY + 1;
				if (!found) {
					found = metodaToFind(xToScan, yToScan, x, y, toScan);
				}

				xToScan = mX + 1;
				yToScan = mY + 1;
				if (!found) {
					found = metodaToFind(xToScan, yToScan, x, y, toScan);
				}
				
				if (toScan.size() != 0 && found) {
					if (found) {
						find(mX, mY, toScan, selected);
					}
					
					mX = toScan.get(0).getxMap();
					mY = toScan.get(0).getyMap();
				}
			} while (!found);
			
			x = selected.get(selected.size() - 1).getxMap();
			y = selected.get(selected.size() - 1).getyMap();
			toScan.clear();
			found = false;
		}
		return selected;
	}

	private boolean metodaToFind(int xToScan, int yToScan, int x, int y, List<Cell> toScan) {
		if (cells[xToScan][yToScan].getLocation() == Location.INSIDE
				|| cells[xToScan][yToScan].getLocation() == Location.EDGE) {
			double distance = MathUtil.pointDistance(xToScan, yToScan, x, y);
			toScan.add(cells[xToScan][yToScan]);
			if (Math.round(distance) >= rozdzelczosc) {
				return true;
			}
		}
		return false;
	}

	private void find(int x, int y, List<Cell> toScan, List<Cell> points) {
		boolean test = false;
		boolean test1 = true;
		int pointsSize;
		List<Cell> testowanie;
		List<Cell> cells = new ArrayList<>();
		List<Cell> scan = new ArrayList<>();
		int xToScan;
		int yToScan;

		int mX = x;
		int mY = y;
		do {
			if (scan.size() > 0) {
				scan.remove(0);
			}
			if (cells.size() > 2) {
				testowanie = calculateToFind(mX, mY, cells);
				pointsSize = points.size();
				for (int i = 0; i < testowanie.size(); i++) {
					if (!points.contains(testowanie.get(i))) {
						points.add(testowanie.get(i));
					}
				}
				if (pointsSize != points.size()) {
					test1 = false;
				}
//				if (cell != null) {
//					pointsSize = points.size();
//					points.add(cell);
//					if (pointsSize != points.size()) {
//						test1 = false;
//					}
//				}
			}

			xToScan = mX - 1;
			yToScan = mY - 1;
			test = findScan(xToScan, yToScan, scan, cells);

			xToScan = mX;
			yToScan = mY - 1;
			test = findScan(xToScan, yToScan, scan, cells);

			xToScan = mX + 1;
			yToScan = mY - 1;
			test = findScan(xToScan, yToScan, scan, cells);

			xToScan = mX - 1;
			yToScan = mY;
			test = findScan(xToScan, yToScan, scan, cells);

			xToScan = mX;
			yToScan = mY;
			test = findScan(xToScan, yToScan, scan, cells);

			xToScan = mX - 1;
			yToScan = mY + 1;
			test = findScan(xToScan, yToScan, scan, cells);

			xToScan = mX;
			yToScan = mY + 1;
			test = findScan(xToScan, yToScan, scan, cells);

			xToScan = mX + 1;
			yToScan = mY + 1;
			test = findScan(xToScan, yToScan, scan, cells);

			if (scan.size() != 0) {
				mX = scan.get(0).getxMap();
				mY = scan.get(0).getyMap();
			}
		} while (test1);
		
		for (int i = 0; i < this.cells.length; i++) {
			for (int j = 0; j < this.cells.length; j++) {
				this.cells[i][j].setScanLocatingToLocalize(null);
			}
		}
	}

	private boolean findScan(int x, int y, List<Cell> scan, List<Cell> cell) {
		if (cells[x][y].getScanLocatingToLocalize() != scanLocating.SCANED) {
			if (cells[x][y].getLocation() == Location.EDGE) {
				cells[x][y].setScanLocatingToLocalize(scanLocating.SCANED);
				cell.add(cells[x][y]);
//				scan.add(cells[x][y]);
				return true;
			} else if (cells[x][y].getLocation() == Location.INSIDE) {
				cells[x][y].setScanLocatingToLocalize(scanLocating.SCANED);
				scan.add(cells[x][y]);
			}
		}
		return false;
	}

	private List<Cell> calculateToFind(int x, int y, List<Cell> cell) {
		List<Cell> cells = new ArrayList<>();
		for (int i = 0; i < cell.size(); i++) {
			for (int j = i + 1; j < cell.size(); j++) {
				double distance1 = MathUtil.pointDistance(x, y, cell.get(i).getxMap(), cell.get(i).getyMap());
				double distance2 = MathUtil.pointDistance(x, y, cell.get(j).getxMap(), cell.get(j).getyMap());
				if (MathUtil.roundD(distance1, 2) == MathUtil.roundD(distance2, 2)) {
//					System.out.println(distance1 + ", " + distance2);
//					Line l1 = AnalitycalGeometry.findLine(
//							new Point2D.Double(cell.get(i).getxMap(), cell.get(i).getyMap()), new Point2D.Double(x, y));
//
//					Line l2 = AnalitycalGeometry.findLine(
//							new Point2D.Double(cell.get(j).getxMap(), cell.get(j).getyMap()), new Point2D.Double(x, y));
//
////					System.out.println("test " + l1.a() + " : " + l2.a());
////					System.out.println("test0 " + cell.get(i).getxMap() + ", " + cell.get(i).getyMap());
////					System.out.println("test0 " + cell.get(j).getxMap() + ", " + cell.get(j).getyMap());
////					double degree = AnalitycalGeometry.degree(l1, l2);
//					System.out.println(degree);
					Vector<Integer> a = new Vector<>(2);
					a.set(0, cell.get(i).getxMap() - x);
					a.set(1, cell.get(i).getyMap() - y);

					Vector<Integer> b = new Vector<>(2);
					b.set(0, cell.get(j).getxMap() - x);
					b.set(1, cell.get(j).getyMap() - y);
//
					double degree = a.rad(b);
//					System.out.println(degree + ", " + Math.PI);
					if (Math.round(degree) == Math.round(Math.PI)) {
						System.out.println(x + ", " + y + " !!! " + cell.get(i) + ", " + cell.get(j));
						cells.add(this.cells[x][y]);
					}
				}
			}
		}
		return cells;
	}

//	private List<Cell> findIntrestingOne(int x, int y) {
//		List<Cell> cellsToScan = new ArrayList<>();
//		List<Cell> cellsSelected = new ArrayList<>();
//		int cellX;
//		int cellY;
//		int Mx = x;
//		int My = y;
//		do {
//			if (cellsToScan.size() != 0) {
//				cellsToScan.remove(0);
//			}
//			
//			cellX = x - 1;
//			cellY = y - 1;
//			scanToSelect(cellsSelected, cellsToScan, cellX, cellY, Mx, My);
//			
//			cellX = x;
//			cellY = y - 1;
//			scanToSelect(cellsSelected, cellsToScan, cellX, cellY, Mx, My);
//
//			cellX = x + 1;
//			cellY = y - 1;
//			scanToSelect(cellsSelected, cellsToScan, cellX, cellY, Mx, My);
//
//			cellX = x - 1;
//			cellY = y;
//			scanToSelect(cellsSelected, cellsToScan, cellX, cellY, Mx, My);
//
//			cellX = x + 1;
//			cellY = y;
//			scanToSelect(cellsSelected, cellsToScan, cellX, cellY, Mx, My);
//
//			cellX = x - 1;
//			cellY = y + 1;
//			scanToSelect(cellsSelected, cellsToScan, cellX, cellY, Mx, My);
//
//			cellX = x;
//			cellY = y + 1;
//			scanToSelect(cellsSelected, cellsToScan, cellX, cellY, Mx, My);
//
//			cellX = x + 1;
//			cellY = y + 1;
//			scanToSelect(cellsSelected, cellsToScan, cellX, cellY, Mx, My);
//			
//			x = cellsToScan.get(0).getxMap();
//			y = cellsToScan.get(0).getyMap();
//			 
//		} while (cellsToScan.size() != 0);
//		
//		return cellsSelected;
//	}

//	private boolean scanToSelect(List<Cell> cell, List<Cell> toScan, int x, int y, int scanedX, int scanedY) {
//		if (cells[x][y].getStatus() == Status.VISITED) {
//			if (MathUtil.pointDistance(x, y, scanedX, scanedY) == rozdzelczosc) {
//				cell.add(findMiddle(cells[x][y], new ArrayList<Cell>()));
//				cell.get(cell.size() - 1).setTest(test.SCANED);
//				return true;
//			} else {
//				toScan.add(cells[x][y]);
//			}
//		}
//		return false;
//	}

//	private Cell findMiddle(Cell cell, List<Cell> scaned) {
//		Cell middleCell = null;
//		int x = cell.getxMap();
//		int y = cell.getyMap();
//
//		List<Cell> toScan = new ArrayList<>();
//		List<Cell> edgeCells = new ArrayList<>();
//
//		int cellX;
//		int cellY;
//		boolean found = false;
//		do {
//			cellX = x - 1;
//			cellY = y - 1;
//			scanToLocate(cellX, cellY, edgeCells, toScan, scaned);
//
//			cellX = x;
//			cellY = y - 1;
//			scanToLocate(cellX, cellY, edgeCells, toScan, scaned);
//
//			cellX = x + 1;
//			cellY = y - 1;
//			scanToLocate(cellX, cellY, edgeCells, toScan, scaned);
//
//			cellX = x - 1;
//			cellY = y;
//			scanToLocate(cellX, cellY, edgeCells, toScan, scaned);
//
//			cellX = x;
//			cellY = y;
//			scanToLocate(cellX, cellY, edgeCells, toScan, scaned);
//
//			cellX = x + 1;
//			cellY = y;
//			scanToLocate(cellX, cellY, edgeCells, toScan, scaned);
//
//			cellX = x - 1;
//			cellY = y + 1;
//			scanToLocate(cellX, cellY, edgeCells, toScan, scaned);
//
//			cellX = x;
//			cellY = y + 1;
//			scanToLocate(cellX, cellY, edgeCells, toScan, scaned);
//
//			cellX = x + 1;
//			cellY = y + 1;
//			scanToLocate(cellX, cellY, edgeCells, toScan, scaned);
//
//			double degree = -1;
//			int distance1 = -1;
//			int distance2 = -1;
//			Vector<Integer> a = new Vector<>(2);
//			Vector<Integer> b = new Vector<>(2);
//
//			for (int i = 0; i < edgeCells.size(); i++) {
//				a.set(0, Math.abs(edgeCells.get(i).getxMap() - x));
//				a.set(1, Math.abs(edgeCells.get(i).getyMap() - y));
//				for (int j = 0; j < edgeCells.size(); j++) {
//					if (i != j) {
//						b.set(0, Math.abs(edgeCells.get(j).getxMap() - x));
//						b.set(1, Math.abs(edgeCells.get(j).getyMap() - y));
//						degree = a.rad(b);
//
//						if (Math.round(degree) == Math.round(Math.PI)) {
//							distance1 = (int) MathUtil.pointDistance(a.get(0), a.get(1), x, y);
//							distance2 = (int) MathUtil.pointDistance(b.get(0), b.get(1), x, y);
//							if (Math.round(distance1) == Math.round(distance2)) {
//								middleCell = cells[x][y];
//								found = true;
//								break;
//							}
//						}
//					}
//				}
//				if (degree == Math.PI) {
//					if (Math.round(distance1) == Math.round(distance2)) {
//						middleCell = cells[x][y];
//						found = true;
//						break;
//					}
//				}
//			}
//
//			x = toScan.get(toScan.size() - 1).getxMap();
//			y = toScan.get(toScan.size() - 1).getyMap();
//		} while (found);
//
//		return middleCell;
//	}

//	private void scanToLocate(int cellX, int cellY, List<Cell> edgeCells, List<Cell> toScan, List<Cell> scaned) {
//		if (cells[cellX][cellY].getLocation() == Location.EDGE) {
//			edgeCells.add(cells[cellX][cellY]);
//			edgeCells.get(edgeCells.size() - 1).setScanLocating(scanLocating.SCANED);
//			scaned.add(cells[cellX][cellY]);
//		} else if (cells[cellX][cellY].getLocation() == Location.INSIDE) {
//			toScan.add(cells[cellX][cellY]);
//			toScan.get(toScan.size() - 1).setScanLocating(scanLocating.SCANED);
//		}
//	}

	private SimpleFeatureType constractShp() {
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("Line");
		try {
			builder.setCRS(CRS.decode("EPSG:2180"));
		} catch (Exception e) {
			System.err.println("B³ad CRS: zastosowano domyœlny uk³ad wsp. WGS84");
			builder.setCRS(DefaultGeographicCRS.WGS84);
		}
		builder.add("the_geom", Point.class);

		String[] columnNames = new String[3];
		columnNames[0] = "source";
		columnNames[1] = "value";
		columnNames[2] = "number";

		builder.length(15).add(columnNames[0], String.class);
		builder.add(columnNames[1], Double.class);
		builder.add(columnNames[2], Integer.class);

		return builder.buildFeatureType();
	}

	private File creatShp(File file) {
		String path = file.getAbsolutePath();
		String newPath = path.substring(0, path.length() - 4) + ".shp";

		JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
		chooser.setDialogTitle("Save shapefile");
		chooser.setSelectedFile(new File(newPath));

		int returnVal = chooser.showSaveDialog(null);

		if (returnVal != JFileDataStoreChooser.APPROVE_OPTION) {
			// the user cancelled the dialog
			System.exit(0);
		}

		File newFile = chooser.getSelectedFile();
		if (newFile.equals(file)) {
			System.out.println("Error: cannot replace " + file);
			System.exit(0);
		}

		return newFile;
	}
}

package main;

import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.geotools.coverage.grid.GridCoordinates2D;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.OverviewPolicy;
import org.geotools.data.Parameter;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.DirectPosition2D;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;

public class GeoTiff {
	private File selectedFile;
	private int sizeX, sizeY;
	private double minX, minY, width, height, cellxSize, cellySize;
	private BufferedImage image;

	public GeoTiff(String pathname) {
		selectedFile = new File(pathname);
		try {
			ParameterValue<OverviewPolicy> policy = AbstractGridFormat.OVERVIEW_POLICY.createValue();
			policy.setValue(OverviewPolicy.IGNORE);
			
			ParameterValue<String> gridSize = AbstractGridFormat.SUGGESTED_TILE_SIZE.createValue();
			ParameterValue<Boolean> useJaiRead = AbstractGridFormat.USE_JAI_IMAGEREAD.createValue();
			useJaiRead.setValue(true);
			
			GeoTiffReader reader = new GeoTiffReader(selectedFile);
			
			GridCoverage2D imageCoverage2d = reader.read(new GeneralParameterValue[] {policy, gridSize, useJaiRead});

			GridGeometry2D geometryGrid = imageCoverage2d.getGridGeometry();
			Rectangle2D.Double geometry = (Double) imageCoverage2d.getEnvelope2D().getBounds2D();
			
			minX = geometry.getMinX();
			minY = geometry.getMinY();
			
			width = geometry.getWidth();
			height = geometry.getHeight();
			
			sizeX = geometryGrid.getGridRange().getHigh(0) + 1;
			sizeY = geometryGrid.getGridRange().getHigh(1) + 1;
			
			cellxSize = width / sizeX;
			cellySize = height / sizeY;
			
			image = ImageIO.read(selectedFile);
//			System.out.println(geometryGrid);
//			
//			BigDecimal widthB = new BigDecimal(width);
//			BigDecimal heightB = new BigDecimal(height);
//			
//			BigDecimal sizeXBigDecimal = new BigDecimal(sizeX + 1);
//			BigDecimal sizeYBigDecimal = new BigDecimal(sizeY + 1);
//			
//			BigDecimal cellXSizeBigDecimal = widthB.divide(sizeXBigDecimal, 16, RoundingMode.DOWN);
//			BigDecimal cellYSizeBigDecimal = heightB.divide(sizeYBigDecimal, 16, RoundingMode.DOWN);
			
//			cellxSize = cellXSizeBigDecimal.doubleValue();
//			cellySize = cellYSizeBigDecimal.doubleValue();
			
//			double currentX = minX + (cellxSize / 2);
//			double currentY = minY + height + (cellySize / 2);
//			image = ImageIO.read(selectedFile);
//			for (int i = 0; i < height; i++) {
//				for (int j = 0; j < width; j++) {
//					int pixel = image.getRGB(i, j);
//					int r = (pixel >> 16) & 0xff;
//					int g = (pixel >> 8) & 0xff;
//					int b = (pixel) & 0xff;
//					double x = currentX;
//					double y = currentY;
//					double z = 0;
//					cells.add(new Cell(r, g, b, x, y, z));
//					
//					currentX += cellxSize;
//				}
//				currentX = minX + (cellxSize / 2);
//				currentY -= cellySize;
//			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	public double getValue(double x, double y) throws Exception {
//		GridGeometry2D gg = grid.getGridGeometry();
//
//		DirectPosition2D posworld = new DirectPosition2D(x, y);
//		GridCoordinates2D posGrid = gg.worldToGrid(posworld);
//
//		System.out.println(posGrid.x + " : " + posGrid.y);
//
//		double[] pixel = new double[1];
//		double[] data = gridData.getPixel(posGrid.x, posGrid.y, pixel);
//		
//		return data[0];
//	}

	public File getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(File selectedFile) {
		this.selectedFile = selectedFile;
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public double getMinX() {
		return minX;
	}

	public double getMinY() {
		return minY;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public double getCellxSize() {
		return cellxSize;
	}

	public double getCellySize() {
		return cellySize;
	}

	public BufferedImage getImage() {
		return image;
	}
}

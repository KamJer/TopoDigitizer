package main;


public class TopoDigitizer {
	
	public static void main(String[] args) {
		GeoTiff geoTiff = new GeoTiff("C:\\Users\\Kamil\\Desktop\\test\\mapa mask.tif");
		ScanLine proces = new ScanLine(geoTiff, 1146, 552, 244, 139, 90, 25, 110, 1);
//		ScanLine proces = new ScanLine(geoTiff, 182, 297, 246, 167, 130, 50, 95, 1);
//		FileSelecter fileSelecter = new FileSelecter("C:\\Users\\Kamil\\Desktop\\Gis dane zapasowe\\GIS\\Ÿród³a\\mapy topograficzne\\10000\\N-33-131-B-c-3 POMARZANOWICE (TOPO-92).tif");
	}
}

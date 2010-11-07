package com.froger.routepathexample;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MainActivity extends MapActivity {
	private MapView mapView;

	@Override
	protected boolean isRouteDisplayed() { return false; }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        mapView = (MapView)findViewById(R.id.mapview);
        //Example data
        double latitudeFrom = 50.06469036372934;
        double longitudeFrom = 19.944788217544556;
        double latitudeTo = 50.06400165088368;
        double longitudeTo = 19.920390844345093;
        
        GeoPoint srcGeoPoint = 
        	new GeoPoint((int)(latitudeFrom * 1E6), (int)(longitudeFrom * 1E6));
        GeoPoint destGeoPoint = 
        	new GeoPoint((int)(latitudeTo * 1E6), (int)(longitudeTo * 1E6));

        drawPath(srcGeoPoint, destGeoPoint, mapView);

        mapView.getController().animateTo(srcGeoPoint);
        mapView.getController().setZoom(15);
    }
    
    private void drawPath(GeoPoint src, GeoPoint dest, MapView mapView) {
		String strUrl = "http://maps.google.com/maps?";
		//From
		strUrl += "saddr=" +
			   (src.getLatitudeE6()/1.0E6) + 
			   "," +
			   (src.getLongitudeE6()/1.0E6);
		//To
		strUrl += "&daddr=" +
			   (dest.getLatitudeE6()/1.0E6) + 
			   "," + 
			   (dest.getLongitudeE6()/1.0E6);
		//Walk attribute (for walk path)
		strUrl += "&dirflg=w";
		//File format
		strUrl += "&output=kml";
    	
    	try {
    		//Parse KML
			URL url = new URL(strUrl.toString());
			
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			SAXParser parser = saxFactory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			
			KMLHandler kmlHandler = new KMLHandler();
			reader.setContentHandler(kmlHandler);
			
			InputSource inputSource = new InputSource(url.openStream());
			reader.parse(inputSource);

			String path = kmlHandler.getPathCoordinates();
			//Draw path
			if(path != null) {
				RouteOverlay routeOverlay = new RouteOverlay();
				
				String pairs[] = path.split(" ");
				
				for (String pair : pairs) {
					String coordinates[] = pair.split(",");
					GeoPoint geoPoint = new GeoPoint(
							(int) (Double.parseDouble(coordinates[1]) * 1E6),
							(int) (Double.parseDouble(coordinates[0]) * 1E6));
					routeOverlay.addGeoPoint(geoPoint);
				}
				
				mapView.getOverlays().add(routeOverlay);
			}
		} catch (Exception e) {
			Log.w("RoutePath", e.toString());
		}
    }
}
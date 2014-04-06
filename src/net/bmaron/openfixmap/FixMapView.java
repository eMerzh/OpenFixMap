package net.bmaron.openfixmap;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class FixMapView extends MapView {
	private Handler mHandler;
	private final CharSequence[] layers = {"OSM Mapnik", "Mapquest", "CycleMap"};
	private MyLocationOverlay locationOverlay;
    GestureDetector mGestureDetector;

    
	public FixMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// TODO Auto-generated constructor stub
	}

	public FixMapView(Context context, int tileSizePixels) {
		super(context, tileSizePixels);
		// TODO Auto-generated constructor stub
	}

	public FixMapView(Context context, int tileSizePixels,
			ResourceProxy resourceProxy) {
		super(context, tileSizePixels, resourceProxy);
		// TODO Auto-generated constructor stub
	}

	public FixMapView(Context context, int tileSizePixels,
			ResourceProxy resourceProxy, MapTileProviderBase aTileProvider) {
		super(context, tileSizePixels, resourceProxy, aTileProvider);
		// TODO Auto-generated constructor stub
	}
	
	public FixMapView(Context context, int tileSizePixels,
			ResourceProxy resourceProxy, MapTileProviderBase aTileProvider,
			Handler tileRequestCompleteHandler) {
		super(context, tileSizePixels, resourceProxy, aTileProvider,
				tileRequestCompleteHandler);
		// TODO Auto-generated constructor stub
	}
	
	public CharSequence[] getLayers()
	{
		return layers;
	}
	
	public void setup(Context ctx)
	{
        mHandler = new Handler(); 
        setBuiltInZoomControls(true);
        setMultiTouchControls(true);

        locationOverlay = new MyLocationOverlay(ctx, this);   
        getOverlays().add(locationOverlay);
        locationOverlay.enableMyLocation();
        getOverlays().add(new Overlay(ctx) {
            @Override
            public boolean onTouchEvent(MotionEvent e, MapView mapView) {
                mGestureDetector.onTouchEvent(e);
                return super.onTouchEvent(e, mapView);
            }

			@Override
			protected void draw(Canvas arg0, MapView arg1, boolean arg2) {}
        });
	}
	
	public void loadMapSource(int source)
	{

		switch(source) {
		case 1:
			setTileSource(TileSourceFactory.MAPQUESTOSM);
			break;
		case 2:
			setTileSource(TileSourceFactory.CYCLEMAP); break;
		case 0:
			setTileSource(TileSourceFactory.MAPNIK);
		default:
			setTileSource(TileSourceFactory.MAPNIK);
			break;
		}

	}
	
	public void goTo(final GeoPoint p,final int zoom)
	{
		mHandler.post(new Runnable() {
			public void run() { 
				if(p != null) {
					//Make sure setZoom is before animateTo ==> may be a Go to sahara animateTo bug
					getController().setZoom(zoom);
					getController().animateTo(p);
				}
			}
		});       
	}
	public MyLocationOverlay getLocationOverlay() {
		return locationOverlay;
	}
	
	public void goToCurrentLocation() {
		goTo(locationOverlay.getMyLocation(),17);
	}    
	
	public GestureDetector getGestureDetector() {
		return mGestureDetector;
	}
	
	public void setGestureDetector(GestureDetector d) {
		mGestureDetector = d;
	}
}

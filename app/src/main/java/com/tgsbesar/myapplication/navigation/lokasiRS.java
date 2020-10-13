package com.tgsbesar.myapplication.navigation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.tgsbesar.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class lokasiRS extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final String DESTINATION_ICON_ID = "destination-icon-id";
    private static final String DESTINATION_SYMBOL_LAYER_ID = "destination-symbol-layer-id";
    private static final String DESTINATION_SOURCE_ID = "destination-source-id";
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private Point destinationPoint = Point.fromLngLat(110.3756467600845,-7.776090528426948);
    private Point point;
    private MapView mapView;
    private FloatingActionButton searchFab;
    private Button btn_nav;

    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_location);

        //create map
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        searchFab=findViewById(R.id.fab_location_search);
        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(lokasiRS.this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });

        btn_nav = findViewById(R.id.startButton);
        btn_nav.setEnabled(true);
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(currentRoute!=null){
                   NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                           .directionsRoute(currentRoute)
                           .shouldSimulateRoute(true)
                           .build();
                   // Call this method with Context from within an Activity
                   NavigationLauncher.startNavigation(lokasiRS.this, options);

               }
            }
        });
    }

    private void initLayers(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage(DESTINATION_ICON_ID,
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource(DESTINATION_SOURCE_ID);
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer symbolLayer = new SymbolLayer(DESTINATION_SYMBOL_LAYER_ID, DESTINATION_SOURCE_ID);
        symbolLayer.withProperties(
                iconImage(DESTINATION_ICON_ID),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(symbolLayer);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(new Style.Builder().fromUri(Style.MAPBOX_STREETS),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        List<Feature> features = new ArrayList<>();
                        enableLocationComponent(style);
                        initLayers(style);
                        getRoute(getPoint(),destinationPoint);

                        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                            private Point point = getPoint() ;
                            @Override
                            public boolean onMapClick(@NonNull LatLng point) {
                                Point originPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                                features.clear();
                                features.add(Feature.fromGeometry(destinationPoint));
                                GeoJsonSource source = mapboxMap.getStyle().getSourceAs(DESTINATION_SOURCE_ID);
                                source.setGeoJson(FeatureCollection.fromFeatures(features));
                                getRoute(originPoint, destinationPoint);

                                btn_nav.setBackgroundResource(R.color.colorPrimary);
                                return true;
                            }
                        });
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            CarmenFeature carmenFeature = PlaceAutocomplete.getPlace(data);

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs("geojsonSourceLayerId");
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] { Feature.fromJson(carmenFeature.toJson()) }
                        ));
                    }
                    /*
                    Kalau udah ketemu lokasinya set mapstyle sekali lagi buat nampilin titik
                    Ini agak ga bagus code nya karena hack banget solusinya
                     */

                    mapboxMap.setStyle(new Style.Builder().fromUri(Style.MAPBOX_STREETS),
                            new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {
                                    List<Feature> features = new ArrayList<>();
                                    enableLocationComponent(style);
                                    initLayers(style);
                                    Point originPoint = Point.fromLngLat(((Point) carmenFeature.geometry()).longitude(), ((Point) carmenFeature.geometry()).latitude());
                                    features.clear();
                                    features.add(Feature.fromGeometry(originPoint));
                                    GeoJsonSource source = mapboxMap.getStyle().getSourceAs(DESTINATION_SOURCE_ID);
                                    source.setGeoJson(FeatureCollection.fromFeatures(features));

                                    /*
                                    Pindah animate kedalam onstyle load biar abis ngasi pin di lokasi ke zoom
                                     */

                                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                            new CameraPosition.Builder()
                                                    .target(new LatLng(((Point) carmenFeature.geometry()).latitude(),
                                                            ((Point) carmenFeature.geometry()).longitude()))
                                                    .zoom(14)
                                                    .build()), 4000);
                                    /*
                                    Enable tombol yg buat mulai navigasi
                                     */
                                    btn_nav.setEnabled(true);
                                    btn_nav.setBackgroundResource(R.color.colorAccent);
                                }
                            }
                    );

                    /*
                    Pake fungsi getroute untuk buat route nya
                     */
                    Point originPoint = Point.fromLngLat(((Point) carmenFeature.geometry()).longitude(), ((Point) carmenFeature.geometry()).latitude());

                        getRoute(originPoint,destinationPoint);

                }
            }
        }
    }

    private void getRoute(Point origin, Point destination) {
        /*
        Fungsi buat mendapatkan rute dari point asal ke point tujuan berguna untuk no 2 dan bonus
         */
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);
                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Grant Location Permission", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private Point getPoint() {
        return this.point;
    }


    @SuppressLint("MissingPermission")
    private void enableLocationComponent(@NonNull Style style) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, style).build()
            );
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
            this.point = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                    locationComponent.getLastKnownLocation().getLatitude());
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
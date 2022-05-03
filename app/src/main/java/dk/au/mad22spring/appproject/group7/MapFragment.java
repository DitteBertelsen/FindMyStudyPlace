package dk.au.mad22spring.appproject.group7;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import dk.au.mad22spring.appproject.group7.Fragments.StudyPlaceListViewModel;
import dk.au.mad22spring.appproject.group7.R;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;
import dk.au.mad22spring.appproject.group7.viewModels.MapFragmentViewmodel;

//ref: https://www.geeksforgeeks.org/how-to-implement-google-map-inside-fragment-in-android/
public class MapFragment extends Fragment implements LocationListener {
    private static final long MIN_TIME_BETWEEN_LOCATION_UPDATES = 5 * 1000;    // milisecs
    private static final float MIN_DISTANCE_MOVED_BETWEEN_LOCATION_UPDATES = 1;  // meters
    public static final String EXTRA_USER_LATITUDE = "location_latitude";
    public static final String EXTRA_USER_LONGITUDE = "location_longitude";


    private boolean isTracking = false;
    private StudyPlaceListViewModel MFViewModel;
    private LocationManager locationManager;
    private StudyPlace studyPlace;
    private Location userLocation;
    private double tempLat;
    private double tempLong;
    private List<StudyPlace> studyPlaceList;
    private ArrayList<StudyPlace> studyPlaceList1;
    public static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private GoogleMap map;
    public Criteria criteria;
    public String bestProvider;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (locationManager == null) {
            locationManager =
                    (LocationManager) getActivity().getSystemService(FMSPApplication.LOCATION_SERVICE);
        }

        // Initialize view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        checkPermissions();
        //startTracking();

        MFViewModel = new ViewModelProvider(getActivity()).get(StudyPlaceListViewModel.class);

        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                showStudyPlaces(map);
                //showUser(map,userLocation);
                // When map is loaded
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                    }

                });
                getLocation();
            }
        });


        MFViewModel.getStudyPlaces().observe(this.getViewLifecycleOwner(), new Observer<List<StudyPlace>>() {
            @Override
            public void onChanged(List<StudyPlace> studyPlaces) {
                studyPlaceList = studyPlaces;
                showStudyPlaces(map);
            }
        });



        // Return view
        return view;
    }


    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(FMSPApplication.getAppContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }
    }


    //modified from: https://developer.android.com/training/permissions/requesting.html
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // got permission
                } else {
                    // permission denied
                    //in this case we just close the app
                    //Toast.makeText(OverviewActivity, "You need to enable permission for Location to use the app", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }
    }


    private void showStudyPlaces(GoogleMap googleMap) {

        if (map == null) {
            return;
        }
        map.clear();

        StudyPlace studyPlace;

        for (int i = 0; i < studyPlaceList.size(); i++) {
            studyPlace = studyPlaceList.get(i);

            //add markers
            // Initialize marker options
            MarkerOptions markerOptions = new MarkerOptions();
            // Set position of marker
            markerOptions.position(new LatLng(studyPlace.getStudyPlaceLat(), studyPlace.getStudyPlaceLong()));
            // Set title of marker
            markerOptions.title(studyPlace.getTitle());
            //Add icon
            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.download));
            // Animating to zoom the marker
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(studyPlace.getStudyPlaceLat(), studyPlace.getStudyPlaceLong()), 15));
            // Add marker on map
            googleMap.addMarker(markerOptions);

        }
    }

    //ref: https://stackoverflow.com/questions/32290045/error-invoke-virtual-method-double-android-location-location-getlatitude-on?fbclid=IwAR3kLGm3UsJBEBiXPIGl8zZvIIjk4RheeSBXPnzZ78vtWP8J8zMXnZS2UZI
    protected void getLocation() {
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
    }

    //ref: https://stackoverflow.com/questions/32290045/error-invoke-virtual-method-double-android-location-location-getlatitude-on?fbclid=IwAR3kLGm3UsJBEBiXPIGl8zZvIIjk4RheeSBXPnzZ78vtWP8J8zMXnZS2UZI
    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Hey, a non null location! Sweet!

        //remove location callback:
        locationManager.removeUpdates(this);

        //open the map:
        //tempLat = location.getLatitude();
        //tempLong = location.getLongitude();

        showUser(map,location);
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }


    private void showUser(GoogleMap googleMap, Location userLocation) {

        MarkerOptions markerOptions=new MarkerOptions();
        // Set position of marker
        markerOptions.position(new LatLng(userLocation.getLatitude(),userLocation.getLongitude()));
        // Set title of marker
        markerOptions.title(getString(R.string.userlocation));
        //Add icon
        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.download));
        // Animating to zoom the marker
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLatitude(),userLocation.getLongitude()),15));
        // Add marker on map
        googleMap.addMarker(markerOptions);
    }


}
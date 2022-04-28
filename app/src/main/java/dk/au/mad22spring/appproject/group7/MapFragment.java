package dk.au.mad22spring.appproject.group7;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
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

import dk.au.mad22spring.appproject.group7.Fragments.StudyPlaceListViewModel;
import dk.au.mad22spring.appproject.group7.R;
import dk.au.mad22spring.appproject.group7.models.StudyPlace;
import dk.au.mad22spring.appproject.group7.viewModels.MapFragmentViewmodel;

//ref: https://www.geeksforgeeks.org/how-to-implement-google-map-inside-fragment-in-android/
public class MapFragment extends Fragment {
    private boolean isTracking = false;
    private MapFragmentViewmodel MFViewModel;
    private LocationManager locationManager;
    private StudyPlace studyPlace;
    private double tempLat;
    private double tempLong;
    private LiveData<ArrayList<StudyPlace>> studyPlaceList;
    public static final int PERMISSIONS_REQUEST_LOCATION = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initialize view
        View view=inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize map fragment
        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        checkPermissions();

        MFViewModel = new ViewModelProvider(getActivity()).get(MapFragmentViewmodel.class);

        studyPlaceList=MFViewModel.getStudyPlaces();

        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                showStudyPlaces(googleMap);
                // When map is loaded
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        // When clicked on map
                        // Initialize marker options
                        MarkerOptions markerOptions=new MarkerOptions();
                        // Set position of marker
                        markerOptions.position(latLng);
                        // Set title of marker
                        markerOptions.title(latLng.latitude+" : "+latLng.longitude);
                        // Remove all marker
                        googleMap.clear();
                        // Animating to zoom the marker
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                        // Add marker on map
                        googleMap.addMarker(markerOptions);



                    }


                });
            }
            private void showStudyPlaces(GoogleMap googleMap) {

                StudyPlace studyPlace;

                for(int i=0; i<studyPlaceList.getValue().size();i++){
                    studyPlace=studyPlaceList.getValue().get(i);

                    //add markers
                    // Initialize marker options
                    MarkerOptions markerOptions=new MarkerOptions();
                    // Set position of marker
                    markerOptions.position(new LatLng(studyPlace.getStudyPlaceLat(),studyPlace.getStudyPlaceLong()));
                    // Set title of marker
                    markerOptions.title(studyPlace.getTitle());
                    //Add icon
                    //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.download));
                    // Animating to zoom the marker
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(studyPlace.getStudyPlaceLat(),studyPlace.getStudyPlaceLong()),10));
                    // Add marker on map
                    googleMap.addMarker(markerOptions);

                }

            }

        });


        // Return view
        return view;




    }

    private void checkPermissions(){
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
}
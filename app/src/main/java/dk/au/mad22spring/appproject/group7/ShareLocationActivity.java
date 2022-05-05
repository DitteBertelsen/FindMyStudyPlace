package dk.au.mad22spring.appproject.group7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import dk.au.mad22spring.appproject.group7.models.NotificationModel;
import dk.au.mad22spring.appproject.group7.viewModels.ShareLocationViewModel;

public class ShareLocationActivity extends AppCompatActivity implements LocationListener {

    Button btnBack, btnShareLocation, btnAdd;
    EditText edtFriendEmail, edtBuilding,edtComment;
    TextView txtAddedFriends;
    private ShareLocationViewModel slViewModel;
    public static final int PERMISSIONS_REQUEST_LOCATION = 2;
    public Criteria criteria;
    public String bestProvider;
    public LocationManager locationManager;
    public Location userLoca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);

        slViewModel = new ViewModelProvider(this).get(ShareLocationViewModel.class);
        if (locationManager == null) {
            locationManager =
                    (LocationManager) this.getSystemService(FMSPApplication.LOCATION_SERVICE);
        }

        setUpUI();
        checkPermissions();
        getLocation();
    }

    private void setUpUI() {
        edtBuilding = findViewById(R.id.edtBuilding);
        edtComment = findViewById(R.id.edtComment);
        edtFriendEmail = findViewById(R.id.edtFriendEmail);

        txtAddedFriends = findViewById(R.id.txtAddedFriends);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        btnShareLocation = findViewById(R.id.btnShareLocation);
        btnShareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] tempfriends = txtAddedFriends.getText().toString().split("\n");
                ArrayList<String> friends = new ArrayList<>();

                //Convert from String[] to ArrayList<String>:
                for (String friend : tempfriends) {
                    friends.add(friend);
                }

                //Create a NotificationModel:
                NotificationModel notificationModel = new NotificationModel();
                notificationModel.setBuilding(edtBuilding.getText().toString());
                notificationModel.setFriendLocationLat(userLoca.getLatitude());
                notificationModel.setFriendLocationLong(userLoca.getLongitude());
                notificationModel.setComment(edtComment.getText().toString());

                slViewModel.pushNotification(notificationModel, friends);

                setResult(RESULT_OK);
                finish();
            }
        });

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtFriendEmail.getText().toString();
                edtFriendEmail.setText("");

                //TODO Check if email exist in db:

                //if friend email exist in db:
                if (true) {
                    txtAddedFriends.append(email +"\n");
                }
                else {
                    Toast.makeText(FMSPApplication.getAppContext(), R.string.txtFriendDoesNotExist, Toast.LENGTH_SHORT);
                }
            }
        });
    }
    //Checks location permission
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(FMSPApplication.getAppContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }
    }


    //modified from: https://developer.android.com/training/permissions/requesting.html
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // got permission
                } else {
                    // permission denied
                    Toast.makeText(this, "You need to enable permission to share", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    //Get user location
    //ref: https://stackoverflow.com/questions/32290045/error-invoke-virtual-method-double-android-location-location-getlatitude-on?fbclid=IwAR3kLGm3UsJBEBiXPIGl8zZvIIjk4RheeSBXPnzZ78vtWP8J8zMXnZS2UZI
    protected void getLocation() {
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(bestProvider, 1000, 0,this);
    }

    //ref: https://stackoverflow.com/questions/32290045/error-invoke-virtual-method-double-android-location-location-getlatitude-on?fbclid=IwAR3kLGm3UsJBEBiXPIGl8zZvIIjk4RheeSBXPnzZ78vtWP8J8zMXnZS2UZI
    @Override
    public void onLocationChanged(@NonNull Location location) {
        //remove location callback:
        locationManager.removeUpdates(this);
        userLocations(location);
    }

    private void userLocations(Location location) {
        userLoca=location;
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


}
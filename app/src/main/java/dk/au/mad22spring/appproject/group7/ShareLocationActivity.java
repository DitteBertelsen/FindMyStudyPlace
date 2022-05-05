package dk.au.mad22spring.appproject.group7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import dk.au.mad22spring.appproject.group7.models.NotificationModel;
import dk.au.mad22spring.appproject.group7.viewModels.ShareLocationViewModel;

public class ShareLocationActivity extends AppCompatActivity {

    Button btnBack, btnShareLocation, btnAdd;
    EditText edtFriendEmail, edtBuilding,edtComment;
    TextView txtAddedFriends;
    private ShareLocationViewModel slViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);

        slViewModel = new ViewModelProvider(this).get(ShareLocationViewModel.class);

        setUpUI();
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
                String[] tempfriends = txtAddedFriends.toString().split("\n");
                ArrayList<String> friends = new ArrayList<>();

                //Convert from String[] to ArrayList<String>:
                for (String friend : tempfriends) {
                    friends.add(friend);
                }

                //Create a NotificationModel:
                NotificationModel notificationModel = new NotificationModel();
                notificationModel.setBuilding(edtBuilding.getText().toString());
                //TODO få Trine til at tilføje lat og long


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
}
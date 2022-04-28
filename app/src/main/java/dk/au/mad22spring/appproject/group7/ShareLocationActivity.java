package dk.au.mad22spring.appproject.group7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ShareLocationActivity extends AppCompatActivity {

    Button btnBack, btnShareLocation, btnAdd;
    EditText edtFriendEmail, edtComment;
    TextView txtAddedFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);

        setUpUI();
    }

    private void setUpUI() {
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
                String[] friends = txtAddedFriends.toString().split("\n");

                //Todo Share location...

                setResult(RESULT_OK);
                finish();
            }
        });

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtFriendEmail.getText().toString();

                //TODO Check if email exist in db:

                if (true) {
                    txtAddedFriends.append("\n" + email);
                }
                else{
                    Toast.makeText(FMSPApplication.getAppContext(), R.string.txtFriendDoesNotExist, Toast.LENGTH_SHORT);
                }
            }
        });

        edtComment = findViewById(R.id.edtComment);
        edtFriendEmail = findViewById(R.id.edtFriendEmail);

        txtAddedFriends = findViewById(R.id.txtAddedFriends);
    }
}
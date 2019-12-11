package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btn_logout;
    TextView txt_nama, txt_username;
    String nama, username;
    SharedPreferences sharedpreferences;
    Intent intent;

    public static final String TAG_NAMA = "nama";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_IDJOIN = "id_join";
    public static final String TAG_IDCLASS = "idclass";
    public static final String TAG_USERNAME_HOST = "username_host";
    public static final String TAG_NAMECLASS = "nameclass";
    public static final String TAG_USERNAME_CLIENT = "username_client";
    //public static final String TAG_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_nama = (TextView) findViewById(R.id.txt_nama);
        txt_username = (TextView) findViewById(R.id.txt_username);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        sharedpreferences = getSharedPreferences(LoginMahasiswaActivity.my_shared_preferences, Context.MODE_PRIVATE);

        nama = getIntent().getStringExtra(TAG_NAMA);
        username = getIntent().getStringExtra(TAG_USERNAME);

        txt_nama.setText(nama);
        txt_username.setText(username);

        btn_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // update login session ke FALSE dan mengosongkan nilai id dan username
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(LoginMahasiswaActivity.session_status, false);
                editor.putString(TAG_NAMA, null);
                editor.putString(TAG_USERNAME, null);
                getIntent().removeExtra("username_client");
                getIntent().removeExtra("nama");
                editor.commit();

                Intent intent = new Intent(MainActivity.this, LoginMahasiswaActivity.class);
                finish();
                startActivity(intent);
            }
        });

    }

    public void createclass(View view) {
        Intent intent = new Intent(MainActivity.this, CreateClassActivity.class);
        intent.putExtra("username", txt_username.getText().toString());
        intent.putExtra("nama", txt_nama.getText().toString());
        finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }

    public void joinclass(View view) {
        Intent intent = new Intent(MainActivity.this, JoinClassActivity.class);
        intent.putExtra("username_client", txt_username.getText().toString());
        intent.putExtra("nama", txt_nama.getText().toString());
        finish();
        startActivity(intent);
    }

    public void listclass(View view) {
        Intent intent = new Intent(MainActivity.this, ListClassActivity.class);
        intent.putExtra("username_client", txt_username.getText().toString());
        intent.putExtra("nama", txt_nama.getText().toString());
        finish();
        startActivity(intent);
    }

    public void manageclass(View view) {
        Intent intent = new Intent(MainActivity.this, ManageClassActivity.class);
        intent.putExtra("username_client", txt_username.getText().toString());
        intent.putExtra("nama", txt_nama.getText().toString());
        finish();
        startActivity(intent);
    }
}
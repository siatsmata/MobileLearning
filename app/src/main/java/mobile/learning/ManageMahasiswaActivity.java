package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mobile.learning.app.AppController;

public class ManageMahasiswaActivity extends AppCompatActivity {

    private TextView txt_idjoin;
    private TextView txt_idclass;
    private TextView txt_username_host;
    private TextView txt_nameclass;
    private TextView txt_username_client;
    private TextView txt_absen2;

    public String idjoin,idclass,username_host,nameclass,username_client;
    private static final String TAG = ManageMahasiswaActivity.class.getSimpleName();
    private String url = Server.URL + "hitung_absen.php";
    ProgressDialog pDialog;
    int success;
    ConnectivityManager conMgr;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";


    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_mahasiswa);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        Intent intent = getIntent();
        //idjoin = intent.getStringExtra(Server.TAG_IDJOIN);
        //idclass = intent.getStringExtra(Server.TAG_IDCLASS);
        //username_host = intent.getStringExtra(Server.TAG_USERNAME_HOST);
        nameclass = intent.getStringExtra(Server.TAG_NAMECLASS);
        username_client = intent.getStringExtra(Server.TAG_USERNAME_CLIENT);

        //txt_idjoin = (TextView) findViewById(R.id.txt_idjoin);
        //txt_idclass = (TextView) findViewById(R.id.txt_idclass);
       // txt_username_host = (TextView) findViewById(R.id.txt_username_host);
        txt_nameclass = (TextView) findViewById(R.id.txt_nameclass);
        txt_username_client = (TextView) findViewById(R.id.txt_username_client);
        txt_absen2 = (TextView) findViewById(R.id.txt_absen2);

        //txt_idjoin.setText(idjoin);
        //txt_idclass.setText(idclass);
        //txt_username_host.setText(username_host);
        txt_nameclass.setText(getIntent().getStringExtra("nameclass"));
        txt_username_client.setText(getIntent().getStringExtra("username_client"));

        hitungabsen(username_client,nameclass);
    }

    private void hitungabsen(final String username_client, final String nameclass) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Searching ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        String jumlah = jObj.getString(Server.TAG_JUMLAH);
                        txt_absen2.setText(jumlah);
                    }
                    else {

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username_client", username_client);
                params.put("name_class", nameclass);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    public void task(View view) {
        Intent intent = new Intent(ManageMahasiswaActivity.this, UpdateNimActivity.class);
        intent.putExtra("username_client", txt_username_client.getText().toString());
        startActivity(intent);
    }

    public void scoretask(View view) {
        Intent intent = new Intent(ManageMahasiswaActivity.this, ListScoreActivity.class);
        intent.putExtra("nameclass", txt_nameclass.getText().toString());
        intent.putExtra("username_client", txt_username_client.getText().toString());
        startActivity(intent);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(ManageMahasiswaActivity.this, DisplayTaskActivity.class);
        intent.putExtra("username_client", txt_username_client.getText().toString());
        intent.putExtra("nameclass",txt_nameclass.getText().toString());
        finish();
        startActivity(intent);
    }*/
}

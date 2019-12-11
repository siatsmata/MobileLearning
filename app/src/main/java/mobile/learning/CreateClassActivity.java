package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import java.util.Random;

import mobile.learning.app.AppController;

import static mobile.learning.LoginMahasiswaActivity.session_status;

public class CreateClassActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    private TextView txt_nama,txt_username,txt_nameclass,txt_code;
    int success;
    ConnectivityManager conMgr;
    Intent intent;
    SharedPreferences sharedpreferences;
    Boolean session = false;

    private String url = Server.URL + "create_class.php";

    private static final String TAG = CreateClassActivity.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    //public final static String TAG_USERNAME = "username";
    //public final static String TAG_NAMA = "nama";

    String nama, username;

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

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
        //String message = intent.getStringExtra(GuruActivity.EXTRA_MESSAGE);

        //mengambil nilai yang diteruskan dari class mainactivity
        txt_nama = (TextView)findViewById(R.id.txt_nama);
        txt_username = (TextView)findViewById(R.id.txt_username);
        txt_nameclass = (TextView)findViewById(R.id.txt_nameclass);
        txt_code = (TextView)findViewById(R.id.txt_code);

        txt_username.setText(getIntent().getStringExtra("username"));
        txt_nama.setText(getIntent().getStringExtra("nama"));

        txt_code.setText(generateRandomChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 5));
    }

    public static String generateRandomChars(String candidateChars, int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(candidateChars.charAt(random.nextInt(candidateChars
                    .length())));
        }

        return sb.toString();
    }

    public void createclass(View view) {
        String username = txt_username.getText().toString();
        String nameclass = txt_nameclass.getText().toString();
        String code = txt_code.getText().toString();


        if(TextUtils.isEmpty(username)) {
            txt_username.setError("username tidak boleh kosong !");
            return;
        }

        if(TextUtils.isEmpty(nameclass)) {
            txt_nameclass.setError("Name Class tidak boleh kosong !");
            return;
        }
        if(TextUtils.isEmpty(code)) {
            txt_code.setError("Code tidak boleh kosong !");
            return;
        }
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            checkCreateClass(username,nameclass,code);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkCreateClass(final String username, final String nameclass, final String code) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Create Class ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Class Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Successfully Class!", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        txt_nameclass.setText("");

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

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
                params.put("username", username);
                params.put("nameclass", nameclass);
                params.put("code", code);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    public void refresh(View view) {
        txt_code.setText(generateRandomChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 5));
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(CreateClassActivity.this, MainActivity.class);
        intent.putExtra("username", txt_username.getText().toString());
        intent.putExtra("nama", txt_nama.getText().toString());
        finish();
        startActivity(intent);
    }
}

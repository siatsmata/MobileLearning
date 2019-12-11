package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import static mobile.learning.LoginMahasiswaActivity.my_shared_preferences;
import static mobile.learning.LoginMahasiswaActivity.session_status;


public class RegisterMahasiswaActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    Button btn_register, btn_login;
    EditText txt_nama,txt_username,txt_email, txt_password, txt_confirm_password;
    Intent intent;

    int success;
    ConnectivityManager conMgr;

    private String url = Server.URL + "register_mahasiswa.php";

    private static final String TAG = RegisterMahasiswaActivity.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mahasiswa);

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

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        txt_nama = (EditText) findViewById(R.id.txt_nama);
        txt_username = (EditText) findViewById(R.id.txt_username);
        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_password = (EditText) findViewById(R.id.txt_password);
        txt_confirm_password = (EditText) findViewById(R.id.txt_confirm_password);


    }

    public void pindahlogin(View view) {
        Intent intent = new Intent(RegisterMahasiswaActivity.this, LoginMahasiswaActivity.class);
        finish();
        startActivity(intent);
    }

    public void Register(View view) {
        String nama = txt_nama.getText().toString();
        String username = txt_username.getText().toString();
        String email = txt_email.getText().toString();
        String password = txt_password.getText().toString();
        String confirm_password = txt_confirm_password.getText().toString();

        if(TextUtils.isEmpty(nama)) {
           txt_nama.setError("Nama tidak boleh kosong !");
           return;
        }

        if(TextUtils.isEmpty(username)) {
           txt_username.setError("Username tidak boleh kosong !");
           return;
        }
        if(TextUtils.isEmpty(email)) {
            txt_email.setError("Email tidak boleh kosong !");
            return;
        }
        if(TextUtils.isEmpty(password)) {
            txt_password.setError("Password tidak boleh kosong !");
            return;
        }
        if(TextUtils.isEmpty(confirm_password)) {
            txt_confirm_password.setError("Confirm Password tidak boleh kosong !");
            return;
        }
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            checkRegister(nama,username,email, password, confirm_password);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkRegister(final String nama,final String username,final String email,final String password, final String confirm_password) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Register ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Successfully Register!", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        txt_nama.setText("");
                        txt_username.setText("");
                        txt_email.setText("");
                        txt_password.setText("");
                        txt_confirm_password.setText("");
                        showAlert();
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
                params.put("nama", nama);
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                params.put("confirm_password", confirm_password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
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
        intent = new Intent(RegisterMahasiswaActivity.this, LoginMahasiswaActivity.class);
        finish();
        startActivity(intent);
    }

    private void showAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title dialog
        alertDialogBuilder.setTitle("Succeed");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("OKE")
                .setIcon(R.drawable.ic_check_black_24dp)
                .setCancelable(false)
                .setPositiveButton("OKE",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        intent = new Intent(RegisterMahasiswaActivity.this, LoginMahasiswaActivity.class);
                        finish();
                        startActivity(intent);
                    }
                });

        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
    }
}

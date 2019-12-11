package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

import mobile.learning.adapter.Adapter;
import mobile.learning.adapter.AdapterNew;
import mobile.learning.app.AppController;

public class ProccessJoinClass extends AppCompatActivity{

    Adapter adapter;
    //AdapterNew adapterNew;

    public String idclass,username,nameclass,code;

    private EditText txt_idclass;
    private EditText txt_username_host;
    private EditText txt_nameclass;
    private EditText txt_code;

    private EditText txt_nama;
    private EditText txt_username_client;

    private String url = Server.URL + "join_class.php";

    private static final String TAG = ProccessJoinClass.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";


    int success;
    ConnectivityManager conMgr;
    ProgressDialog pDialog;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proccess_join_class);

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
        idclass = intent.getStringExtra(Server.TAG_IDCLASS);
        username = intent.getStringExtra(Server.TAG_USERNAME);
        nameclass = intent.getStringExtra(Server.TAG_NAMECLASS);
        code = intent.getStringExtra(Server.TAG_CODE);

        txt_idclass = (EditText) findViewById(R.id.txt_idclass);
        txt_username_host = (EditText) findViewById(R.id.txt_username_host);
        txt_nameclass = (EditText) findViewById(R.id.txt_nameclass);
        txt_code = (EditText) findViewById(R.id.txt_code);

        txt_nama = (EditText) findViewById(R.id.txt_nama);
        txt_username_client = (EditText)findViewById(R.id.txt_username_client);

        txt_idclass.setText(idclass);
        txt_username_host.setText(username);
        txt_nameclass.setText(nameclass);
        txt_code.setText(code);

        txt_username_client.setText(getIntent().getStringExtra("username_client"));
        txt_nama.setText(getIntent().getStringExtra("nama"));
    }

    public void btnjoin(View view) {
        String idclass = txt_idclass.getText().toString();
        String username_host = txt_username_host.getText().toString();
        String nameclass = txt_nameclass.getText().toString();
        String username_client = txt_username_client.getText().toString();

        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            checkJoinClass(idclass,username_host,nameclass, username_client);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkJoinClass(final String idclass, final String username_host, final String nameclass, final String username_client) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Joining ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Join Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Successfully Join!", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                Log.e(TAG, "Join Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("idclass", idclass);
                params.put("username_host", username_host);
                params.put("nameclass", nameclass);
                params.put("username_client", username_client);

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
        intent = new Intent(ProccessJoinClass.this, JoinClassActivity.class);
        adapter.notifyDataSetChanged();
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
                        adapter.notifyDataSetChanged();
                        //adapterNew.notifyDataSetChanged();
                        intent = new Intent(ProccessJoinClass.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
    }
}

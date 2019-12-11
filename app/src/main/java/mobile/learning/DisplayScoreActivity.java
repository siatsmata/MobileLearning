package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mobile.learning.app.AppController;
import mobile.learning.model.DataModel;

public class DisplayScoreActivity extends AppCompatActivity {

    private TextView txt_id,txt_score;
    private ImageView imageView;
    private Button openpdf;

    public String id, photo;

    Intent intent;

    int success;
    ConnectivityManager conMgr;
    ProgressDialog pDialog;

    private String url = Server.URL + "update_score.php";

    private static final String TAG = DisplayScoreActivity.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_score);

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
        id = intent.getStringExtra(Server.TAG_ID);
        photo = intent.getStringExtra(Server.TAG_URL_TASK);

        txt_id = (TextView) findViewById(R.id.txt_id);
        txt_score = (TextView) findViewById(R.id.txt_score);
        imageView = (ImageView) findViewById(R.id.imageView);
        openpdf = (Button) findViewById(R.id.btn_openpdf);


        txt_id.setText(id);


        String file1 = photo;
        String newName = file1.substring(file1.lastIndexOf(".") + 1, file1.length());

        openbutton(newName);
    }

    private void openbutton(String file) {

        String pdf = "pdf";
        String png = "png";

        if (file.equals(pdf)) {
            imageView.setEnabled(false);
            openpdf.setVisibility(View.VISIBLE);

        } else if (file.equals(png)) {
            Picasso.with(this).load(Server.URLUPLOAD + photo).into(imageView);
        }
    }

    public void entertask(View view) {
        String idtask = txt_id.getText().toString();
        String score = txt_score.getText().toString();

        if(TextUtils.isEmpty(id)) {
            txt_id.setError("id tidak boleh kosong !");
            return;
        }
        if(TextUtils.isEmpty(score)) {
            txt_id.setError("score tidak boleh kosong !");
            return;
        }
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            checkScore(idtask,score);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkScore(final String idtask,final String score) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Update ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Successfully Update Score!", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        kosong();
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
                params.put("idtask", idtask);
                params.put("score", score);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void kosong() {
        imageView.setImageResource(0);
        txt_score.setText(null);
    }

    public void openpdf(View view) {
        Intent intent = new Intent(this, PdfDisActivity.class);
        intent.putExtra("file", photo);
        startActivity(intent);
    }
}

package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.learning.adapter.AdapterTaskNew;
import mobile.learning.app.AppController;
import mobile.learning.model.DataModel;

public class ListScoreActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    List<DataModel> listData = new ArrayList<DataModel>();
    AdapterTaskNew adapter;
    SwipeRefreshLayout swipe;
    ListView list_view;
    Intent intent;

    public static final String url_data = Server.URL + "data_score_byname_client.php";

    private TextView txt_username_client;
    private TextView txt_nameclass;
    private static final String TAG = ListScoreActivity.class.getSimpleName();

    public static final String TAG_ID = "id";
    public static final String TAG_USERNAME_HOST = "username_host";
    public static final String TAG_NAME_CLASS = "name_class";
    public static final String TAG_NAME_TASK_DOSEN = "name_task_dosen";
    public static final String TAG_NAME_TASK_MAHASISWA = "name_task_mahasiswa";
    public static final String TAG_USERNAME_CLIENT = "username_client";
    public static final String TAG_URL_TASK = "url_task";
    public static final String TAG_SCORE = "score";
    public static final String TAG_ABSEN = "absen";
    public static final String TAG_RESULTS = "results";
    public static final String TAG_MESSAGE = "message";
    public static final String TAG_VALUE = "value";

    String tag_json_obj = "json_obj_req";

    private ArrayList listItem;

    public static String username_host,nameclass,username_client,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_score);

        list_view = (ListView) findViewById(R.id.list_view);

        adapter = new AdapterTaskNew(ListScoreActivity.this, listData);
        list_view.setAdapter(adapter);

        username_client = getIntent().getStringExtra("username_client");
        nameclass = getIntent().getStringExtra("nameclass");

        //txt_username_client.setText(username_client);
        //txt_nameclass.setText(nameclass);

        cariData(username_client,nameclass);
    }

    private void cariData(final String username_client,final String nameclass) {
        listData.clear();
        adapter.notifyDataSetChanged();


        pDialog = new ProgressDialog(ListScoreActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, url_data, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);

                    int value = jObj.getInt(TAG_VALUE);

                    if (value == 1) {
                        listData.clear();
                        adapter.notifyDataSetChanged();

                        String getObject = jObj.getString(TAG_RESULTS);
                        JSONArray jsonArray = new JSONArray(getObject);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            DataModel data = new DataModel();
                            data.setId(obj.getString(TAG_ID));
                            data.setUsername_host(obj.getString(TAG_USERNAME_HOST));
                            data.setName_class(obj.getString(TAG_NAME_CLASS));
                            data.setName_task_dosen(obj.getString(TAG_NAME_TASK_DOSEN));
                            data.setName_task_mahasiswa(obj.getString(TAG_NAME_TASK_MAHASISWA));
                            data.setUsername_client(obj.getString(TAG_USERNAME_CLIENT));
                            data.setUrl_task(obj.getString(TAG_URL_TASK));
                            data.setScore(obj.getString(TAG_SCORE));
                            data.setAbsen(obj.getString(TAG_ABSEN));
                            listData.add(data);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username_client", username_client);
                params.put("nameclass", nameclass);
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(ListScoreActivity.this, ManageMahasiswaActivity.class);
        intent.putExtra("nameclass", txt_nameclass.getText().toString());
        intent.putExtra("username_client", txt_username_client.getText().toString());
        finish();
        startActivity(intent);
    }*/
}

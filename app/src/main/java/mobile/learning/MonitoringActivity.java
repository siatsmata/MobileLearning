package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
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

import mobile.learning.adapter.AdapterMentoring;
import mobile.learning.app.AppController;
import mobile.learning.model.DataModel;

public class MonitoringActivity extends AppCompatActivity {

    ProgressDialog pDialog;

    public static final String url_data = Server.URL + "mentoring_data.php";
    private static final String TAG = MonitoringActivity.class.getSimpleName();
    public static final String TAG_RESULTS = "results";
    public static final String TAG_MESSAGE = "message";
    public static final String TAG_VALUE = "value";

    AdapterMentoring adapter;
    List<DataModel> listData = new ArrayList<DataModel>();
    ListView list_view;

    public static String username_host,nameclass;

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        list_view = (ListView) findViewById(R.id.list_view);

        adapter = new AdapterMentoring(MonitoringActivity.this, listData);
        list_view.setAdapter(adapter);

        username_host = getIntent().getStringExtra("username");
        nameclass = getIntent().getStringExtra("nameclass");

        cariData(username_host,nameclass);
    }

    private void cariData(final String username_host,final String nameclass) {
        listData.clear();
        adapter.notifyDataSetChanged();


        pDialog = new ProgressDialog(MonitoringActivity.this);
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
                            data.setId(obj.getString("id"));
                            data.setUsername_host(obj.getString("username_dosen"));
                            data.setUsername_client(obj.getString("username_mahasiswa"));
                            data.setNameclass(obj.getString("nameclass"));
                            data.setTanggal(obj.getString("tanggal"));
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
                params.put("keyword", username_host);
                params.put("keyword2", nameclass);
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }


}

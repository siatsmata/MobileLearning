package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import mobile.learning.adapter.AdapterTask;
import mobile.learning.app.AppController;
import mobile.learning.model.DataModel;

public class ListTaskActivity extends AppCompatActivity implements ListView.OnItemClickListener{

    ProgressDialog pDialog;
    List<DataModel> listData = new ArrayList<DataModel>();
    AdapterTask adapter;
    SwipeRefreshLayout swipe;
    ListView list_view;
    Intent intent;

    public static final String url_data = Server.URL + "data_task_byclient.php";

    private TextView txt_username_client;
    private static final String TAG = ListTaskActivity.class.getSimpleName();

    public static final String TAG_ID = "id";
    public static final String TAG_IDCLASS = "id_class";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_NAMECLASS = "nameclass";
    public static final String TAG_NAME = "name";
    public static final String TAG_PHOTO = "photo";
    public static final String TAG_PATH = "path";
    public static final String TAG_RESULTS = "results";
    public static final String TAG_MESSAGE = "message";
    public static final String TAG_VALUE = "value";

    String tag_json_obj = "json_obj_req";

    private ArrayList listItem;

    public static String username_host,nameclass,username_client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);

        list_view = (ListView) findViewById(R.id.list_view);

        adapter = new AdapterTask(ListTaskActivity.this, listData);
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(this);

        username_host = getIntent().getStringExtra("username_host");
        nameclass = getIntent().getStringExtra("nameclass");
        username_client = getIntent().getStringExtra("username_client");
        txt_username_client = (TextView) findViewById(R.id.txt_username_client);
        txt_username_client.setText(username_client);

        cariData(username_host,nameclass);
    }

    private void cariData(final String keyword, final String keyword2) {
        listData.clear();
        adapter.notifyDataSetChanged();


        pDialog = new ProgressDialog(ListTaskActivity.this);
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
                            data.setId_class(obj.getString(TAG_IDCLASS));
                            data.setUsername(obj.getString(TAG_USERNAME));
                            data.setNameclass(obj.getString(TAG_NAMECLASS));
                            data.setName(obj.getString(TAG_NAME));
                            data.setPhoto(obj.getString(TAG_PHOTO));
                            data.setPath(obj.getString(TAG_PATH));
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
                params.put("keyword", keyword);
                params.put("keyword2", keyword2);
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, DisplayTaskActivity.class);

        String idtask = ((TextView)view.findViewById(R.id.txt_id)).getText().toString();
        String id_class = ((TextView)view.findViewById(R.id.txt_idclass)).getText().toString();
        String username = ((TextView)view.findViewById(R.id.txt_username)).getText().toString();
        String nameclass = ((TextView)view.findViewById(R.id.txt_nameclass)).getText().toString();
        String name = ((TextView)view.findViewById(R.id.txt_name)).getText().toString();
        String photo = ((TextView)view.findViewById(R.id.txt_photo)).getText().toString();

        intent.putExtra(Server.TAG_ID,idtask);
        intent.putExtra(Server.TAG_IDCLASS,id_class);
        intent.putExtra(Server.TAG_USERNAME,username);
        intent.putExtra(Server.TAG_NAMECLASS,nameclass);
        intent.putExtra(Server.TAG_NAME,name);
        intent.putExtra(Server.TAG_PHOTO,photo);
        intent.putExtra("username_client", txt_username_client.getText().toString());
        startActivity(intent);
    }

}

package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import mobile.learning.adapter.Adapter;
import mobile.learning.app.AppController;
import mobile.learning.model.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinClassActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener,ListView.OnItemClickListener {

    ProgressDialog pDialog;
    List<DataModel> listData = new ArrayList<DataModel>();
    Adapter adapter;
    SwipeRefreshLayout swipe;
    ListView list_view;
    Intent intent;

    private TextView txt_nama,txt_username;

    /* 10.0.2.2 adalah IP Address localhost EMULATOR ANDROID STUDIO,
    Ganti IP Address tersebut dengan IP Laptop Apabila di RUN di HP. HP dan Laptop harus 1 jaringan */
    public static final String url_data = Server.URL + "data_class.php";
    public static final String url_cari = Server.URL + "cari_class.php";

    private static final String TAG = JoinClassActivity.class.getSimpleName();

    public static final String TAG_IDCLASS = "id_class";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_NAMECLASS = "nameclass";
    public static final String TAG_CODE = "code";
    public static final String TAG_RESULTS = "results";
    public static final String TAG_MESSAGE = "message";
    public static final String TAG_VALUE = "value";

    String tag_json_obj = "json_obj_req";

    private ArrayList listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        list_view = (ListView) findViewById(R.id.list_view);

        adapter = new Adapter(JoinClassActivity.this, listData);
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(this);

        swipe.setOnRefreshListener(this);

        /*swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           swipe.setRefreshing(true);
                           //callData();
                       }
                   }
        );*/

        txt_nama = (TextView)findViewById(R.id.txt_nama);
        txt_username = (TextView)findViewById(R.id.txt_username);

        txt_username.setText(getIntent().getStringExtra("username_client"));
        txt_nama.setText(getIntent().getStringExtra("nama"));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(this, ProccessJoinClass.class);
        //HashMap<String,String> map =(HashMap)parent.getItem(position);

        String idclass = ((TextView)view.findViewById(R.id.txt_idclass)).getText().toString();
        String username = ((TextView)view.findViewById(R.id.txt_username)).getText().toString();
        String nameclass = ((TextView)view.findViewById(R.id.txt_nameclass)).getText().toString();
        String code = ((TextView)view.findViewById(R.id.txt_code)).getText().toString();


        intent.putExtra(Server.TAG_IDCLASS,idclass);
        intent.putExtra(Server.TAG_USERNAME,username);
        intent.putExtra(Server.TAG_NAMECLASS,nameclass);
        intent.putExtra(Server.TAG_CODE,code);

        intent.putExtra("username_client", txt_username.getText().toString());
        intent.putExtra("nama", txt_nama.getText().toString());
        finish();
        startActivity(intent);
    }

    private void callData() {
        listData.clear();
        adapter.notifyDataSetChanged();
        swipe.setRefreshing(true);

        // Creating volley request obj
        JsonArrayRequest jArr = new JsonArrayRequest(url_data, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.e(TAG, response.toString());

                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        DataModel item = new DataModel();

                        item.setId_class(obj.getString(TAG_IDCLASS));
                        item.setUsername(obj.getString(TAG_USERNAME));
                        item.setNameclass(obj.getString(TAG_NAMECLASS));
                        item.setCode(obj.getString(TAG_CODE));
                        listData.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // notifying list adapter about data changes
                // so that it renders the list view with updated data
                adapter.notifyDataSetChanged();
                swipe.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(JoinClassActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                swipe.setRefreshing(false);
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jArr);
    }

    @Override
    public void onRefresh() {
        //callData();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        cariData(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint(getString(R.string.type_name));
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    private void cariData(final String keyword) {
        pDialog = new ProgressDialog(JoinClassActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, url_cari, new Response.Listener<String>() {

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

                            data.setId_class(obj.getString(TAG_IDCLASS));
                            data.setUsername(obj.getString(TAG_USERNAME));
                            data.setNameclass(obj.getString(TAG_NAMECLASS));
                            data.setCode(obj.getString(TAG_CODE));
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

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    @Override
    public void onBackPressed() {
        adapter.notifyDataSetChanged();
        intent = new Intent(JoinClassActivity.this, MainActivity.class);
        intent.putExtra("username", txt_username.getText().toString());
        intent.putExtra("nama", txt_nama.getText().toString());
        startActivity(intent);
    }
}
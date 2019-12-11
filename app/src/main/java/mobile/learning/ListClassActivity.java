package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;

import android.content.Intent;
import android.content.SharedPreferences;
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

import mobile.learning.adapter.AdapterNew;
import mobile.learning.app.AppController;
import mobile.learning.model.DataModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListClassActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    ProgressDialog pDialog;
    List<DataModel> listData = new ArrayList<DataModel>();
    AdapterNew adapter;
    SwipeRefreshLayout swipe;
    ListView list_view;
    Intent intent;

    private TextView txt_nama,txt_username;



    public static final String url_data = Server.URL + "data_class_byclient.php";

    private static final String TAG = ListClassActivity.class.getSimpleName();

    public static final String TAG_IDJOIN = "id_join";
    public static final String TAG_IDCLASS = "idclass";
    public static final String TAG_USERNAME_HOST = "username_host";
    public static final String TAG_NAMECLASS = "nameclass";
    public static final String TAG_USERNAME_CLIENT = "username_client";
    public static final String TAG_RESULTS = "results";
    public static final String TAG_MESSAGE = "message";
    public static final String TAG_VALUE = "value";

    String tag_json_obj = "json_obj_req";
    public static String username_client;

    private ArrayList listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_class);

        list_view = (ListView) findViewById(R.id.list_view);

        adapter = new AdapterNew(ListClassActivity.this, listData);

        list_view.setAdapter(null);
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(this);

        txt_nama = (TextView)findViewById(R.id.txt_nama);
        txt_username = (TextView)findViewById(R.id.txt_username);

        txt_username.setText(getIntent().getStringExtra("username_client"));
        txt_nama.setText(getIntent().getStringExtra("nama"));

        username_client = getIntent().getStringExtra("username_client");

        cariData(username_client);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ListMahasiswaPertemuanActivity.class);

        String idjoin = ((TextView)view.findViewById(R.id.txt_idjoin)).getText().toString();
        String idclass = ((TextView)view.findViewById(R.id.txt_idclass)).getText().toString();
        String username_host = ((TextView)view.findViewById(R.id.txt_username_host)).getText().toString();
        String nameclass = ((TextView)view.findViewById(R.id.txt_nameclass)).getText().toString();
        String username_client = ((TextView)view.findViewById(R.id.txt_username_client)).getText().toString();

        intent.putExtra(Server.TAG_IDJOIN,idjoin);
        intent.putExtra(Server.TAG_IDCLASS,idclass);
        intent.putExtra(Server.TAG_USERNAME_HOST,username_host);
        intent.putExtra(Server.TAG_NAMECLASS,nameclass);
        intent.putExtra(Server.TAG_USERNAME_CLIENT,username_client);

        intent.putExtra("username_client", txt_username.getText().toString());
        intent.putExtra("nama", txt_nama.getText().toString());

        startActivity(intent);
    }

    private void cariData(final String keyword) {
        adapter.notifyDataSetChanged();

        pDialog = new ProgressDialog(ListClassActivity.this);
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
                            data.setId_join(obj.getString(TAG_IDJOIN));
                            data.setIdclass(obj.getString(TAG_IDCLASS));
                            data.setUsername_host(obj.getString(TAG_USERNAME_HOST));
                            data.setNameclass(obj.getString(TAG_NAMECLASS));
                            data.setUsername_client(obj.getString(TAG_USERNAME_CLIENT));
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
        intent = new Intent(ListClassActivity.this, MainActivity.class);
        intent.putExtra("username", txt_username.getText().toString());
        intent.putExtra("nama", txt_nama.getText().toString());
        startActivity(intent);
    }
}
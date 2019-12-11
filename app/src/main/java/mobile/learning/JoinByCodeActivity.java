package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

import mobile.learning.adapter.Adapter;
import mobile.learning.app.AppController;
import mobile.learning.model.DataModel;

public class JoinByCodeActivity extends AppCompatActivity implements ListView.OnItemClickListener{

    Intent intent;
    ProgressDialog pDialog;
    List<DataModel> listData = new ArrayList<DataModel>();
    Adapter adapter;
    ListView list_view;

    private static final String TAG = JoinByCodeActivity.class.getSimpleName();

    public static final String url_cari = Server.URL + "cari_code.php";

    public static final String TAG_IDCLASS = "id_class";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_NAMECLASS = "nameclass";
    public static final String TAG_CODE = "code";
    public static final String TAG_RESULTS = "results";
    public static final String TAG_MESSAGE = "message";
    public static final String TAG_VALUE = "value";
    String tag_json_obj = "json_obj_req";

    private EditText txt_code;

    public static String username,nama,code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_by_code);

        txt_code = (EditText)findViewById(R.id.txt_code);
        code = txt_code.getText().toString();
        username = getIntent().getStringExtra("username_client");
        nama = getIntent().getStringExtra("nama");

        list_view = (ListView) findViewById(R.id.list_view);

        adapter = new Adapter(JoinByCodeActivity.this, listData);
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(this);
    }

    public void cari(View view) {
        cariData(code);
    }

    private void cariData(final String keyword) {
        listData.clear();
        //adapter.notifyDataSetChanged();
        pDialog = new ProgressDialog(JoinByCodeActivity.this);
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
          //              adapter.notifyDataSetChanged();

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

            //    adapter.notifyDataSetChanged();
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
        intent = new Intent(JoinByCodeActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}

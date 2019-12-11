package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import mobile.learning.app.AppController;


public class ManageDosen2Activity extends AppCompatActivity {

    private TextView txt_idclass;
    private TextView txt_username;
    private TextView txt_nameclass;
    private TextView txt_code,txt_pertemuan;
    private Button btn_create_task;

    public String idclass,username,nameclass,code,pertemuan;
    int success;

    private String UPLOAD_URL2 = Server.URL + "create_task.php";
    private String UPLOAD_URL = Server.URL + "check_task.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    private static final String TAG = ManageMahasiswaActivity.class.getSimpleName();

    private ImageView pdf,btn_video,btn_foto,btn_docx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_dosen2);

        Intent intent = getIntent();
        idclass = intent.getStringExtra(Server.TAG_IDCLASS);
        username = intent.getStringExtra(Server.TAG_USERNAME);
        nameclass = intent.getStringExtra(Server.TAG_NAMECLASS);
        code = intent.getStringExtra(Server.TAG_CODE);
        pertemuan = getIntent().getStringExtra("pertemuan");

        txt_idclass = (TextView) findViewById(R.id.txt_idclass);
        txt_username = (TextView) findViewById(R.id.txt_username);
        txt_nameclass = (TextView) findViewById(R.id.txt_nameclass);
        txt_code = (TextView) findViewById(R.id.txt_code);
        txt_pertemuan = (TextView) findViewById(R.id.txt_pertemuan);

        btn_create_task = (Button) findViewById(R.id.btn_create_task);

        btn_video = findViewById(R.id.btn_video);
        btn_foto = findViewById(R.id.btn_foto);
        btn_docx = findViewById(R.id.btn_docx);

        txt_idclass.setText(idclass);
        txt_username.setText(username);
        txt_nameclass.setText(nameclass);
        txt_code.setText(code);
        txt_pertemuan.setText(pertemuan);

        cariData(username,nameclass,pertemuan);

        pdf = findViewById(R.id.btn_pdf);



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            pdf.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        } else {
            pdf.setEnabled(true);
        }
    }

    private void cariData(final String username,final String nameclass,final String pertemuan) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            success = jObj.getInt(TAG_SUCCESS);

                            if (success == 1) {
                                Log.e("v Add", jObj.toString());

                                Toast.makeText(ManageDosen2Activity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                                btn_create_task.setVisibility(View.GONE);

                            } else {
                                Toast.makeText(ManageDosen2Activity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                                btn_create_task.setVisibility(View.VISIBLE);
                                btn_docx.setVisibility(View.GONE);
                                btn_foto.setVisibility(View.GONE);
                                btn_video.setVisibility(View.GONE);
                                pdf.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //menghilangkan progress dialog

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menghilangkan progress dialog
                        //menampilkan toast
                        Toast.makeText(ManageDosen2Activity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters

                Map<String, String> params = new HashMap<String, String>();
                //menambah parameter yang di kirim ke web servis
                params.put("name", pertemuan);
                params.put("nameclass",nameclass);
                params.put("username",username);

                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);

    }

    public void checktask(View view) {
        Intent intent = new Intent(ManageDosen2Activity.this, CheckTaskActivity.class);
        intent.putExtra("idclass", txt_idclass.getText().toString());
        intent.putExtra("username", txt_username.getText().toString());
        intent.putExtra("nameclass", txt_nameclass.getText().toString());
        intent.putExtra("code", txt_code.getText().toString());
        //intent.putExtra("pertemuan", txt_pertemuan.getText().toString());
        startActivity(intent);
    }

    public void createtask(View view) {
        Intent intent = new Intent(ManageDosen2Activity.this, UploadDosenActivity.class);
        intent.putExtra("idclass", txt_idclass.getText().toString());
        intent.putExtra("username", txt_username.getText().toString());
        intent.putExtra("nameclass", txt_nameclass.getText().toString());
        intent.putExtra("code", txt_code.getText().toString());
        intent.putExtra("pertemuan", txt_pertemuan.getText().toString());
        startActivity(intent);
    }

    public void uploadvideotask(View view) {
        Intent intent = new Intent(ManageDosen2Activity.this, UploadVideoActivity.class);
        intent.putExtra("idclass", txt_idclass.getText().toString());
        intent.putExtra("username", txt_username.getText().toString());
        intent.putExtra("nameclass", txt_nameclass.getText().toString());
        intent.putExtra("code", txt_code.getText().toString());
        intent.putExtra("pertemuan", txt_pertemuan.getText().toString());
        startActivity(intent);
    }

    public void pdftask(View view) {
        Intent intent = new Intent(ManageDosen2Activity.this, PdfActivity.class);
        intent.putExtra("idclass", txt_idclass.getText().toString());
        intent.putExtra("username", txt_username.getText().toString());
        intent.putExtra("nameclass", txt_nameclass.getText().toString());
        intent.putExtra("code", txt_code.getText().toString());
        intent.putExtra("pertemuan", txt_pertemuan.getText().toString());
        startActivity(intent);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pdf.setEnabled(true);
            }
        }
    }

    public void uploaddocxtask(View view) {
        Intent intent = new Intent(ManageDosen2Activity.this, UploadWordActivity.class);
        intent.putExtra("idclass", txt_idclass.getText().toString());
        intent.putExtra("username", txt_username.getText().toString());
        intent.putExtra("nameclass", txt_nameclass.getText().toString());
        intent.putExtra("code", txt_code.getText().toString());
        intent.putExtra("pertemuan", txt_pertemuan.getText().toString());
        startActivity(intent);
    }

    public void monitor(View view) {
        Intent intent = new Intent(ManageDosen2Activity.this, MonitoringActivity.class);
        intent.putExtra("username", txt_username.getText().toString());
        intent.putExtra("nameclass", txt_nameclass.getText().toString());
        startActivity(intent);
    }

    public void createtask2(View view) {
        uploadname2();
    }

    private void uploadname2(){
        final ProgressDialog loading = ProgressDialog.show(this, "Create Task...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            success = jObj.getInt(TAG_SUCCESS);

                            if (success == 1) {
                                Log.e("v Add", jObj.toString());

                                //uploadVideo2(selectedFilePath);
                                Toast.makeText(ManageDosen2Activity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                                cariData(username,nameclass,pertemuan);

                            } else {
                                Toast.makeText(ManageDosen2Activity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //menghilangkan progress dialog
                        loading.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menghilangkan progress dialog
                        loading.dismiss();

                        //menampilkan toast
                        Toast.makeText(ManageDosen2Activity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters

                Map<String, String> params = new HashMap<String, String>();
                //menambah parameter yang di kirim ke web servis
                params.put("name", txt_pertemuan.getText().toString().trim());
                params.put("idclass", txt_idclass.getText().toString());
                params.put("nameclass",txt_nameclass.getText().toString().trim());
                params.put("username",txt_username.getText().toString().trim());

                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }
}

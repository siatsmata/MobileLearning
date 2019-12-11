package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;


import android.app.ProgressDialog;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;


import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.net.HttpURLConnection;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


import mobile.learning.app.AppController;
import mobile.learning.networking.ApiConfig;
import mobile.learning.networking.AppConfig;
import mobile.learning.networking.ServerResponse;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static java.security.AccessController.getContext;

public class UploadVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonChoose;
    private Button buttonUpload;
    private Button buttonUpload2;
    private Button btn_openpdf;
    private TextView textView;
    private TextView textView2;
    private TextView textViewResponse;
    public ProgressDialog pDialog;

    private static final int SELECT_VIDEO = 1;
    public static final int FILE_PICKER_REQUEST_CODE = 2;
    int success;

    private String selectedFilePath;
    private String pdfPath;

    public String idclass,username,nameclass,code;

    private TextView txt_idclass;
    private TextView txt_username;
    private TextView txt_nameclass;
    private TextView txt_code;
    private EditText txt_name;

    private String UPLOAD_URL = Server.URL + "update_video.php";
    private String UPLOAD_URL2 = Server.URL + "upload_dosenvideo2.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    private static final String TAG = MainActivity.class.getSimpleName();

    /*ProgressBar predictProgress;

    int status = 0;
    Handler handler = new Handler();
    Dialog dialog;
    ProgressBar text;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonUpload2 = (Button) findViewById(R.id.buttonUpload2);
        btn_openpdf = (Button) findViewById(R.id.btn_openpdf);

        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textViewResponse = (TextView) findViewById(R.id.textViewResponse);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);


        Intent intent = getIntent();
        txt_idclass = (TextView) findViewById(R.id.txt_idclass);
        txt_username = (TextView) findViewById(R.id.txt_username);
        txt_nameclass = (TextView) findViewById(R.id.txt_nameclass);
        txt_code = (TextView) findViewById(R.id.txt_code);


        txt_idclass.setText(getIntent().getStringExtra("idclass"));
        txt_username.setText(getIntent().getStringExtra("username"));
        txt_nameclass.setText(getIntent().getStringExtra("nameclass"));
        txt_code.setText(getIntent().getStringExtra("code"));

        txt_name = (EditText) findViewById(R.id.txt_name);
        txt_name.setText(getIntent().getStringExtra("pertemuan"));
        //predictProgress = (ProgressBar) findViewById(R.id.progress_horizontal);

    }


    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                System.out.println("SELECT_VIDEO");
                Uri selectedVideoUri = data.getData();

                selectedFilePath = getPath(selectedVideoUri);
                if (selectedFilePath != null) {
                    textView.setText(selectedFilePath);
                }
            }
        }
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            if (path != null) {
                Log.d("Path: ", path);
                pdfPath = path;
                Toast.makeText(this, "Picked file: " + path, Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(uri, projection, null, null, null);

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void uploadVideo() {
            class UploadVideo extends AsyncTask<Void, Void, String> {

                ProgressDialog uploading;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    uploading = ProgressDialog.show(UploadVideoActivity.this, "Uploading File", "Please wait...", false, false);

                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    uploading.dismiss();

                    textViewResponse.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
                    textViewResponse.setMovementMethod(LinkMovementMethod.getInstance());
                }

                @Override
                protected String doInBackground(Void... params) {

                    Upload u = new Upload();
                    String msg = u.uploadVideo(selectedFilePath);
                    return msg;
                }
            }

            UploadVideo uv = new UploadVideo();
            uv.execute();
            uploadname(selectedFilePath);

    }


    private void uploadVideo2() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(UploadVideoActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                textViewResponse.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
                textViewResponse.setMovementMethod(LinkMovementMethod.getInstance());
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.uploadVideo(selectedFilePath);
                return msg;
            }
        }

        UploadVideo uv = new UploadVideo();
        uv.execute();
        uploadFile();
    }

    private void uploadFile() {
        if (pdfPath == null) {
            Toast.makeText(this, "please select an pdf ", Toast.LENGTH_LONG).show();
            return;
        } else {
            //uploadVideo2();
            final ProgressDialog uploading;
            uploading = ProgressDialog.show(UploadVideoActivity.this, "Uploading File", "Please wait...", false, false);
            // Map is used to multipart the file using okhttp3.RequestBody
            Map<String, RequestBody> map = new HashMap<>();
            File file = new File(pdfPath);
            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/pdf"), file);
            map.put("file\"; filename=\"" + file.getName() + "\"", requestBody);
            ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
            Call<ServerResponse> call = getResponse.upload("token", map);
            call.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null){
                            uploadname2(pdfPath,selectedFilePath);
                            uploading.dismiss();
                            ServerResponse serverResponse = response.body();
                            Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            pdfPath=null;

                        }
                    }else {
                        uploading.dismiss();
                        Toast.makeText(getApplicationContext(), "problem pdf", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    uploading.dismiss();
                    Log.v("Response gotten is", t.getMessage());
                    Toast.makeText(getApplicationContext(), "problem uploading image " + t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }


    private void uploadname(final String video){
        String video1 = video;
        final String newName = video1.substring(video1.lastIndexOf("/")+1, video1.length());
        final ProgressDialog loading = ProgressDialog.show(this, "Saveing Name...", "Please wait...", false, false);
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

                                //uploadVideo2(selectedFilePath);
                                Toast.makeText(UploadVideoActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                                textView.setText(null);
                                txt_name.setText(null);
                            } else {
                                Toast.makeText(UploadVideoActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(UploadVideoActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters

                Map<String, String> params = new HashMap<String, String>();

                //menambah parameter yang di kirim ke web servis
                params.put("video",newName);
                params.put("username",txt_username.getText().toString().trim());
                params.put("name", txt_name.getText().toString().trim());
                params.put("nameclass",txt_nameclass.getText().toString().trim());
                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void uploadname2(final String pdf,final String video){
        String video1 = video;
        String pdf1 = pdf;
        final String newName = video1.substring(video1.lastIndexOf("/")+1, video1.length());
        final String newName2 = pdf1.substring(pdf1.lastIndexOf("/")+1, pdf1.length());
        final ProgressDialog loading = ProgressDialog.show(this, "Saveing Name...", "Please wait...", false, false);
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
                                Toast.makeText(UploadVideoActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                                textView.setText(null);
                                txt_name.setText(null);
                            } else {
                                Toast.makeText(UploadVideoActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(UploadVideoActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters

                Map<String, String> params = new HashMap<String, String>();

                //menambah parameter yang di kirim ke web servis
                params.put("video",newName);
                params.put("pdf",newName2);
                params.put("name", txt_name.getText().toString().trim());
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

    @Override
    public void onClick(View v) {
        if (v == buttonChoose) {
            chooseVideo();
        }
        if (v == buttonUpload) {
            cekvalidasi();
        }
    }

    public void cekvalidasi()
    {
        String name = txt_name.getText().toString();
        if(TextUtils.isEmpty(name)) {
            txt_name.setError("Username tidak boleh kosong !");
            return;
        }
        else {
            uploadVideo();
        }
    }

    public void itemClicked(View view) {
        CheckBox checkBox = (CheckBox)view;
        if(checkBox.isChecked()){
            btn_openpdf.setVisibility(View.VISIBLE);
            buttonUpload2.setVisibility(View.VISIBLE);
            buttonUpload.setVisibility(View.INVISIBLE);
        }
        else
        {
            btn_openpdf.setVisibility(View.INVISIBLE);
            buttonUpload2.setVisibility(View.INVISIBLE);
            buttonUpload.setVisibility(View.VISIBLE);
        }

    }

    public void openpdftask(View view) {
        launchPicker();
    }

    private void launchPicker() {
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(FILE_PICKER_REQUEST_CODE)
                .withHiddenFiles(true)
                .withFilter(Pattern.compile(".*\\.pdf$"))
                .withTitle("Select PDF file")
                .start();
    }



    public void upload(View view) {
        String name =txt_name.getText().toString();

        if(TextUtils.isEmpty(name)) {
            txt_name.setError("Username tidak boleh kosong !");
            return;
        }
        else {
            uploadVideo2();
        }
    }
}

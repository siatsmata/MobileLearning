package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mobile.learning.app.AppController;

public class VideoNewActivity extends AppCompatActivity {

    public static String file;

    ProgressDialog pDialog;
    VideoView videoView;
    MediaController mediaController;
    Uri video;

    ConnectivityManager conMgr;

    public static String username_dosen,username_mahasiswa,nameclass;
    public static String strDate;


    private String UPLOAD_URL = Server.URL + "upload_absen.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";
    int success;

    private static final String TAG = VideoNewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_new);

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

        videoView = (VideoView) findViewById(R.id.video_view);
        videoView.setMediaController(new MediaController(this));
        file = getIntent().getStringExtra("file");
        videoStream(file);

        username_dosen = getIntent().getStringExtra("username_dosen");
        username_mahasiswa = getIntent().getStringExtra("username_mahasiswa");
        nameclass = getIntent().getStringExtra("nameclass");
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        strDate = dateFormat.format(date);

        uploadname(username_dosen,username_mahasiswa,nameclass,strDate);
    }

    private void videoStream(String photo) {
        // membuat progressbar
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Buffering ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        try {
            // Memulai MediaController
            mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            // Video URL
            video = Uri.parse(Server.URLUPLOAD + photo);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Menutup pDialog dan play video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoView.start();
            }
        });
    }

    private void uploadname(final String username_dosen,final String username_mahasiswa,final String nameclass,final String strDate){

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
                                Toast.makeText(VideoNewActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();


                            } else {
                                Toast.makeText(VideoNewActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(VideoNewActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters

                Map<String, String> params = new HashMap<String, String>();

                //menambah parameter yang di kirim ke web servis

                params.put("username_dosen", username_dosen);
                params.put("username_mahasiswa", username_mahasiswa);
                params.put("nameclass",nameclass);
                params.put("tanggal",strDate);
                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }
}

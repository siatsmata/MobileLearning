package mobile.learning;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Set;

public class UploadMahasiswaActivity extends AppCompatActivity {

    private Button select;
    private Button upload;
    private ImageView imageview;
    private ProgressDialog progressDialog;

    private TextView txt_idjoin;
    private TextView txt_idclass;
    private TextView txt_username_host;
    private TextView txt_nameclass;
    private TextView txt_username_client;

    public String idjoin,idclass,username_host,nameclass,username_client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_mahasiwa);

        Intent intent = getIntent();


        txt_idjoin = (TextView) findViewById(R.id.txt_idjoin);
        txt_idclass = (TextView) findViewById(R.id.txt_idclass);
        txt_username_host = (TextView) findViewById(R.id.txt_username_host);
        txt_nameclass = (TextView) findViewById(R.id.txt_nameclass);
        txt_username_client = (TextView) findViewById(R.id.txt_username_client);

        //txt_username_client.setText(getIntent().getStringExtra("username_client"));

        txt_idjoin.setText(getIntent().getStringExtra("idjoin"));
        txt_idclass.setText(getIntent().getStringExtra("idclass"));
        txt_username_host.setText(getIntent().getStringExtra("username_host"));
        txt_nameclass.setText(getIntent().getStringExtra("nameclass"));
        txt_username_client.setText(getIntent().getStringExtra("username_client"));

        select = findViewById(R.id.buttonSelect);
        upload = findViewById(R.id.buttonUpload);
        imageview = findViewById(R.id.imageView);

        progressDialog = new ProgressDialog(UploadMahasiswaActivity.this);
        progressDialog.setMessage("Uploading Image. Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(UploadMahasiswaActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .start(UploadMahasiswaActivity.this);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if(response.isPermanentlyDenied()){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadMahasiswaActivity.this);
                                    builder.setTitle("Permission Required")
                                            .setMessage("Permission to access your device storage is required to pick image. Please go to setting to enable permission to access storage")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                                                    startActivityForResult(intent, 51);
                                                }
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .check();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();
                imageview.setImageURI(resultUri);
                upload.setVisibility(View.VISIBLE);
                upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File imagefile = new File(resultUri.getPath());
                        progressDialog.show();
                        AndroidNetworking.upload("https://classroom-trilogi.000webhostapp.com/upload.php")
                                .addMultipartFile("image", imagefile)
                                .addMultipartParameter("userid", String.valueOf(12))
                                .setPriority(Priority.HIGH)
                                .build()
                                .setUploadProgressListener(new UploadProgressListener() {
                                    @Override
                                    public void onProgress(long bytesUploaded, long totalBytes) {
                                        float progress = (float) bytesUploaded / totalBytes * 100;
                                        progressDialog.setProgress((int) progress);
                                    }
                                })
                                .getAsString(new StringRequestListener() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.i("mytag", response);
                                        try {
                                            progressDialog.dismiss();
                                            JSONObject jsonObject = new JSONObject(response);
                                            int status = jsonObject.getInt("status");
                                            String message = jsonObject.getString("message");
                                            if (status == 0) {
                                                Toast.makeText(UploadMahasiswaActivity.this, "Unable to upload image :" + message, Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(UploadMahasiswaActivity.this, message, Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(UploadMahasiswaActivity.this, "Image Upload", Toast.LENGTH_LONG).show();
                                        }

                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        progressDialog.dismiss();
                                        anError.printStackTrace();
                                        Toast.makeText(UploadMahasiswaActivity.this, "Failed Upload Image", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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

public class DisplayTaskActivity extends AppCompatActivity {

    private TextView txt_id;
    private TextView txt_idclass;
    private TextView txt_username;
    private TextView txt_nameclass;
    private TextView txt_name;
    private TextView txt_nametask;
    private TextView txt_file;
    private TextView txt_username_client;
    private ImageView imageView, imageViewTask;
    private Button btn_openvid;
    private Button btn_openvideo;
    private Button btn_openphoto;
    private Button btn_openpdf;
    private Button btn_task_1;
    private Button btn_task_2;
    Bitmap bitmap, decoded;

    private final int PICK_IMAGE_REQUEST = 1;
    private final int PICK_IMAGE_CAMERA = 1;
    public static final int FILE_PICKER_REQUEST_CODE = 2;

    ProgressDialog pDialog;
    private File destination = null;
    private String imgPath = null;
    int bitmap_size = 60; // range 1 - 100
    int success;

    public static String id, id_class, username, nameclass, name, photo, username_client,path;


    private static final String TAG = MainActivity.class.getSimpleName();

    private String UPLOAD_URL = Server.URL + "upload_task.php";
    private String UPLOAD_URL2 = Server.URL + "upload_task_pdf.php";


    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    public String pdfPath;
    private String pdfFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_task);

        Intent intent = getIntent();
        id = intent.getStringExtra(Server.TAG_ID);
        id_class = intent.getStringExtra(Server.TAG_IDCLASS);
        username = intent.getStringExtra(Server.TAG_USERNAME);
        nameclass = intent.getStringExtra(Server.TAG_NAMECLASS);
        name = intent.getStringExtra(Server.TAG_NAME);
        photo = intent.getStringExtra(Server.TAG_PHOTO);
        username_client = getIntent().getStringExtra("username_client");
        path = intent.getStringExtra(Server.TAG_PATH);

        txt_id = (TextView) findViewById(R.id.txt_id);
        txt_idclass = (TextView) findViewById(R.id.txt_idclass);
        txt_username = (TextView) findViewById(R.id.txt_username);
        txt_nameclass = (TextView) findViewById(R.id.txt_nameclass);
        txt_name = (TextView) findViewById(R.id.txt_name);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageViewTask = (ImageView) findViewById(R.id.imageViewTask);
        txt_nametask = (TextView) findViewById(R.id.txt_nametask);
        txt_username_client = (TextView) findViewById(R.id.txt_username_client);
        txt_file = (TextView) findViewById(R.id.txt_file);
        btn_openvideo = (Button) findViewById(R.id.btn_openvideo);
        btn_openvid = (Button) findViewById(R.id.btn_openvid);
        btn_openpdf = (Button) findViewById(R.id.btn_openpdf);
        btn_openphoto = (Button) findViewById(R.id.btn_openphoto);

        btn_task_1 = (Button) findViewById(R.id.btn_task_1);
        btn_task_2 = (Button) findViewById(R.id.btn_task_2);

        txt_id.setText(id);
        txt_idclass.setText(id_class);
        txt_username.setText(username);
        txt_nameclass.setText(nameclass);
        txt_name.setText(name);
        txt_username_client.setText(username_client);

        String file1 = photo;
        String file2 = path;
        String newName = file1.substring(file1.lastIndexOf(".") + 1, file1.length());
        String newName2 = file2.substring(file2.lastIndexOf(".") + 1, file2.length());
        txt_file.setText(newName+" "+newName2);
        openbutton(newName+" "+newName2);
        initDialog();
    }

    private void openbutton(String file) {
        String mp4 = "mp4 ";
        String pdf = "pdf ";
        String png = "png ";
        String docx = "docx ";
        String multy = "mp4 pdf";


        if (file.equals(mp4)) {
            btn_openvideo.setVisibility(View.VISIBLE);

        } else if (file.equals(pdf)) {
            btn_openpdf.setVisibility(View.VISIBLE);

        } else if (file.equals(png)) {
            //Picasso.with(this).load(Server.URLUPLOAD + photo).into(imageView);
            btn_openphoto.setVisibility(View.VISIBLE);
        } else if (file.equals(docx)) {
            btn_openpdf.setVisibility(View.VISIBLE);
        } else if (file.equals(multy)) {
            btn_openvid.setVisibility(View.VISIBLE);
        }
    }


    public void senttask(View view) {
        showFileChooser();
    }

    private void showFileChooser() {
        try {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DisplayTaskActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();

        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            if (path != null) {
                Log.d("Path: ", path);
                pdfPath = path;
                Toast.makeText(this, "Picked file: " + path, Toast.LENGTH_LONG).show();
                btn_task_1.setVisibility(View.INVISIBLE);
                btn_task_2.setVisibility(View.VISIBLE);
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            try {
                Uri filePath = data.getData();
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
                btn_task_1.setVisibility(View.VISIBLE);
                btn_task_2.setVisibility(View.INVISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                Uri filePath = data.getData();
                //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                //setToImageView(getResizedBitmap(bitmap, 50));
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                Log.e("Activity", "Pick from Camera::>>> ");

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                destination = new File(Environment.getExternalStorageDirectory() + "/" +
                        getString(R.string.app_name), "IMG_" + timeStamp + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    btn_task_1.setVisibility(View.VISIBLE);
                    btn_task_2.setVisibility(View.INVISIBLE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imgPath = destination.getAbsolutePath();
                setToImageView(getResizedBitmap(bitmap, 1024));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        imageViewTask.setImageBitmap(decoded);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void uploadtask(View view) {
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading Task...", "Please wait...", false, false);
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

                                Toast.makeText(DisplayTaskActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                                kosong();

                            } else {
                                Toast.makeText(DisplayTaskActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(DisplayTaskActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters
                Map<String, String> params = new HashMap<String, String>();

                //menambah parameter yang di kirim ke web servis
                params.put("image", getStringImage(decoded));
                params.put("username_host", txt_username.getText().toString().trim());
                params.put("name_class", txt_nameclass.getText().toString().trim());
                params.put("name_task_dosen", txt_name.getText().toString().trim());
                params.put("name_task_mahasiswa", txt_nametask.getText().toString().trim());
                params.put("username_client", txt_username_client.getText().toString().trim());

                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    private void kosong() {
        imageViewTask.setImageResource(0);
        txt_nametask.setText(null);
    }

    public void openvideo(View view) {
        Intent intent = new Intent(DisplayTaskActivity.this, VideoActivity.class);
        intent.putExtra("video", photo);
        intent.putExtra("pdf", path);
        intent.putExtra("username_dosen",username);
        intent.putExtra("username_mahasiswa",username_client);
        intent.putExtra("nameclass",nameclass);
        startActivity(intent);
    }

    public void openpdf(View view) {
        Intent intent = new Intent(DisplayTaskActivity.this, PdfDisActivity.class);
        intent.putExtra("file", photo);
        intent.putExtra("username_dosen",username);
        intent.putExtra("username_mahasiswa",username_client);
        intent.putExtra("nameclass",nameclass);
        startActivity(intent);
    }

    public void openpdftask(View view) {
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(FILE_PICKER_REQUEST_CODE)
                .withHiddenFiles(true)
                .withFilter(Pattern.compile(".*\\.pdf$"))
                .withTitle("Select PDF file")
                .start();
    }

    public void uploadtaskpdf(View view) {
        if (pdfPath == null) {
            Toast.makeText(this, "please select an pdf ", Toast.LENGTH_LONG).show();
            return;
        } else {
            showpDialog();

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
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            uploadname(pdfPath);
                            hidepDialog();
                            ServerResponse serverResponse = response.body();
                            Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            pdfPath = null;
                        }
                    } else {
                        hidepDialog();
                        Toast.makeText(getApplicationContext(), "problem image", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    hidepDialog();
                    Log.v("Response gotten is", t.getMessage());
                    Toast.makeText(getApplicationContext(), "problem uploading image " + t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

    private void uploadname(final String pdf){
        String pdf1 = pdf;
        final String newName = pdf1.substring(pdf1.lastIndexOf("/")+1, pdf1.length());
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading Pdf Task...", "Please wait...", false, false);
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

                                Toast.makeText(DisplayTaskActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                                kosong();

                            } else {
                                Toast.makeText(DisplayTaskActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(DisplayTaskActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters

                Map<String, String> params = new HashMap<String, String>();

                //menambah parameter yang di kirim ke web servis
                params.put("image", newName);
                params.put("username_host", txt_username.getText().toString().trim());
                params.put("name_class", txt_nameclass.getText().toString().trim());
                params.put("name_task_dosen", txt_name.getText().toString().trim());
                params.put("name_task_mahasiswa", txt_nametask.getText().toString().trim());
                params.put("username_client", txt_username_client.getText().toString().trim());

                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    protected void initDialog() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(true);
    }


    public void openphoto(View view) {
        Intent intent = new Intent(DisplayTaskActivity.this, DisplayFotoActivity.class);
        intent.putExtra("file", photo);
        intent.putExtra("username_dosen",username);
        intent.putExtra("username_mahasiswa",username_client);
        intent.putExtra("nameclass",nameclass);
        startActivity(intent);
    }

    public void openvideosolo(View view) {
        Intent intent = new Intent(DisplayTaskActivity.this, VideoNewActivity.class);
        intent.putExtra("file", photo);
        intent.putExtra("username_dosen",username);
        intent.putExtra("username_mahasiswa",username_client);
        intent.putExtra("nameclass",nameclass);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                insertnim();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    private void insertnim() {
        Intent intent = new Intent(DisplayTaskActivity.this, ManageMahasiswaActivity.class);
        intent.putExtra("username_client", txt_username_client.getText().toString());
        intent.putExtra("nameclass",txt_nameclass.getText().toString());
        startActivity(intent);
    }


}

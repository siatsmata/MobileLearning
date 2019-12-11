package mobile.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListMahasiswaPertemuanActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    TextView txt_username,txt_nama, heading;
    public static String idclass,username,nameclass,code,pertemuan;
    ArrayAdapter<String> adapter;
    String e[] = {"Pertemuan 1","Pertemuan 2","Pertemuan 3","Pertemuan 4","Pertemuan 5","Pertemuan 6","Pertemuan 7","Pertemuan 8"
            ,"Pertemuan 9","Pertemuan 10","Pertemuan 11","Pertemuan 12","Pertemuan 13","Pertemuan 14"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_mahasiswa_pertemuan);

        listView = (ListView) findViewById(R.id.list_view);
        heading = findViewById(R.id.heading);

        adapter  = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, e);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        Intent intent = getIntent();
        idclass = intent.getStringExtra(Server.TAG_IDCLASS);
        username = intent.getStringExtra(Server.TAG_USERNAME);
        nameclass = intent.getStringExtra(Server.TAG_NAMECLASS);
        code = intent.getStringExtra(Server.TAG_CODE);
        heading.setText(nameclass);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, adapter.getItem(position), Toast.LENGTH_SHORT).show();

        pertemuan = e[position];
        Intent intent = new Intent(this, ManageDosen2Activity.class);
        String nameclass = ((TextView)view.findViewById(R.id.heading)).getText().toString();


        intent.putExtra(Server.TAG_IDCLASS,idclass);
        intent.putExtra(Server.TAG_USERNAME,username);
        intent.putExtra(Server.TAG_NAMECLASS,nameclass);
        intent.putExtra(Server.TAG_CODE,code);
        intent.putExtra("pertemuan", pertemuan);
        //intent.putExtra("username_client", txt_username.getText().toString());
        //intent.putExtra("nama", txt_nama.getText().toString());
        startActivity(intent);

    }
}

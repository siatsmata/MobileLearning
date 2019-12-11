package mobile.learning.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import mobile.learning.R;
import mobile.learning.model.DataModel;

import java.util.List;

public class AdapterTaskNew extends BaseAdapter{
    private Activity activity;
    private LayoutInflater inflater;
    private List<DataModel> item;


    public interface OnAdd{
        public void OnAdddataClicked(DataModel dataModel);
    }

    public AdapterTaskNew(Activity activity, List<DataModel> item) {
        this.activity = activity;
        this.item = item;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int location) {
        return item.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView txt_id, txt_username_host, txt_name_class,txt_name_task_dosen,txt_name_task_mahasiswa,txt_username_client,txt_url_task,txt_score,txt_absen;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View view = convertView;
        final AdapterTaskNew.Holder holder = new AdapterTaskNew.Holder();

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if(convertView == null)
            convertView = inflater.inflate(R.layout.list_tasknew, null);

        holder.txt_id = (TextView) convertView.findViewById(R.id.txt_id);
        holder.txt_username_host = (TextView) convertView.findViewById(R.id.txt_username_host);
        holder.txt_name_class = (TextView) convertView.findViewById(R.id.txt_name_class);
        holder.txt_name_task_dosen = (TextView) convertView.findViewById(R.id.txt_name_task_dosen);
        holder.txt_name_task_mahasiswa = (TextView) convertView.findViewById(R.id.txt_name_task_mahasiswa);
        holder.txt_username_client = (TextView) convertView.findViewById(R.id.txt_username_client);
        holder.txt_url_task = (TextView) convertView.findViewById(R.id.txt_url_task);
        holder.txt_score = (TextView) convertView.findViewById(R.id.txt_score);
        holder.txt_absen = (TextView) convertView.findViewById(R.id.txt_absen);
        holder.txt_id.setText(item.get(position).getId());
        holder.txt_username_host.setText(item.get(position).getUsername_host());
        holder.txt_name_class.setText(item.get(position).getName_class());
        holder.txt_name_task_dosen.setText(item.get(position).getName_task_dosen());
        holder.txt_name_task_mahasiswa.setText(item.get(position).getName_task_mahasiswa());
        holder.txt_username_client.setText(item.get(position).getUsername_client());
        holder.txt_url_task.setText(item.get(position).getUrl_task());
        holder.txt_score.setText(item.get(position).getScore());
        holder.txt_absen.setText(item.get(position).getAbsen());

        return convertView;
    }
}

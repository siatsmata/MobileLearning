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

public class AdapterMentoring extends BaseAdapter{
    private Activity activity;
    private LayoutInflater inflater;
    private List<DataModel> item;


    public interface OnAdd{
        public void OnAdddataClicked(DataModel dataModel);
    }

    public AdapterMentoring(Activity activity, List<DataModel> item) {
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
        TextView txt_id, txt_username_host,txt_username_client, txt_nameclass, txt_tanggal;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View view = convertView;
        final Holder holder = new Holder();

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if(convertView == null)
            convertView = inflater.inflate(R.layout.list_mentoring, null);

        holder.txt_id = (TextView) convertView.findViewById(R.id.txt_id);
        holder.txt_username_host = (TextView) convertView.findViewById(R.id.txt_username_host);
        holder.txt_username_client = (TextView) convertView.findViewById(R.id.txt_username_client);
        holder.txt_nameclass = (TextView) convertView.findViewById(R.id.txt_nameclass);
        holder.txt_tanggal = (TextView) convertView.findViewById(R.id.txt_tanggal);
        holder.txt_id.setText(item.get(position).getId());
        holder.txt_username_host.setText(item.get(position).getUsername_host());
        holder.txt_username_client.setText(item.get(position).getUsername_client());
        holder.txt_nameclass.setText(item.get(position).getNameclass());
        holder.txt_tanggal.setText(item.get(position).getTanggal());

        return convertView;
    }
}

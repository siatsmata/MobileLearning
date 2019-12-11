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

public class AdapterNew extends BaseAdapter{
    private Activity activity;
    private LayoutInflater inflater;
    private List<DataModel> item;


    public interface OnAdd{
        public void OnAdddataClicked(DataModel dataModel);
    }

    public AdapterNew(Activity activity, List<DataModel> item) {
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
        TextView txt_idjoin,txt_idclass, txt_username_host, txt_nameclass, txt_username_client;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View view = convertView;
        final AdapterNew.Holder holder = new AdapterNew.Holder();

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if(convertView == null)
            convertView = inflater.inflate(R.layout.list_join_class, null);

        holder.txt_idjoin = (TextView) convertView.findViewById(R.id.txt_idjoin);
        holder.txt_idclass = (TextView) convertView.findViewById(R.id.txt_idclass);
        holder.txt_username_host = (TextView) convertView.findViewById(R.id.txt_username_host);
        holder.txt_nameclass = (TextView) convertView.findViewById(R.id.txt_nameclass);
        holder.txt_username_client = (TextView) convertView.findViewById(R.id.txt_username_client);
        holder.txt_idjoin.setText(item.get(position).getId_join());
        holder.txt_idclass.setText(item.get(position).getIdclass());
        holder.txt_username_host.setText(item.get(position).getUsername_host());
        holder.txt_nameclass.setText(item.get(position).getNameclass());
        holder.txt_username_client.setText(item.get(position).getUsername_client());

        return convertView;
    }
}

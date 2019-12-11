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

public class AdapterTask extends BaseAdapter{
    private Activity activity;
    private LayoutInflater inflater;
    private List<DataModel> item;


    public interface OnAdd{
        public void OnAdddataClicked(DataModel dataModel);
    }

    public AdapterTask(Activity activity, List<DataModel> item) {
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
        TextView txt_id,txt_id_class, txt_username, txt_nameclass,txt_name, txt_photo, txt_path;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View view = convertView;
        final AdapterTask.Holder holder = new AdapterTask.Holder();

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if(convertView == null)
            convertView = inflater.inflate(R.layout.list_task, null);

        holder.txt_id = (TextView) convertView.findViewById(R.id.txt_id);
        holder.txt_id_class = (TextView) convertView.findViewById(R.id.txt_idclass);
        holder.txt_username = (TextView) convertView.findViewById(R.id.txt_username);
        holder.txt_nameclass = (TextView) convertView.findViewById(R.id.txt_nameclass);
        holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
        holder.txt_photo = (TextView) convertView.findViewById(R.id.txt_photo);
        holder.txt_path = (TextView) convertView.findViewById(R.id.txt_path);
        holder.txt_id.setText(item.get(position).getId());
        holder.txt_id_class.setText(item.get(position).getId_class());
        holder.txt_username.setText(item.get(position).getUsername());
        holder.txt_nameclass.setText(item.get(position).getNameclass());
        holder.txt_name.setText(item.get(position).getName());
        holder.txt_photo.setText(item.get(position).getPhoto());
        holder.txt_path.setText(item.get(position).getPath());
        return convertView;
    }
}

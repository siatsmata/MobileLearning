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

public class Adapter extends BaseAdapter{
    private Activity activity;
    private LayoutInflater inflater;
    private List<DataModel> item;


    public interface OnAdd{
        public void OnAdddataClicked(DataModel dataModel);
    }

    public Adapter(Activity activity, List<DataModel> item) {
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
        TextView txt_idclass, txt_username, txt_nameclass, txt_code;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View view = convertView;
        final Holder holder = new Holder();

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if(convertView == null)
            convertView = inflater.inflate(R.layout.list_class, null);

            holder.txt_idclass = (TextView) convertView.findViewById(R.id.txt_idclass);
            holder.txt_username = (TextView) convertView.findViewById(R.id.txt_username);
            holder.txt_nameclass = (TextView) convertView.findViewById(R.id.txt_nameclass);
            holder.txt_code = (TextView) convertView.findViewById(R.id.txt_code);
            holder.txt_idclass.setText(item.get(position).getId_class());
            holder.txt_username.setText(item.get(position).getUsername());
            holder.txt_nameclass.setText(item.get(position).getNameclass());
            holder.txt_code.setText(item.get(position).getCode());

        return convertView;
    }
}
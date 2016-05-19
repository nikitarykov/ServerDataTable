package rykov.serverdatatable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nikita Rykov on 16.05.2016.
 */
public class CustomAdapter extends ArrayAdapter {
    private List<HashMap<String,String>> items;

    private final static int TYPE_USER = 0;
    private final static int TYPE_YEAR = 1;
    private final static int TYPE_FEED = 2;


    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        String type = items.get(position).get("type");
        if (type.equals("user")) {
            return TYPE_USER;
        } else if (type.equals("year")) {
            return TYPE_YEAR;
        }
        return TYPE_FEED;
    }

    public CustomAdapter(Context context, int resource) {
        super(context, resource);
        items = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HashMap<String, String> item = items.get(position);
        int listViewItemType = getItemViewType(position);
        ViewHolder holder  = new ViewHolder();
        if (convertView == null) {
            if (listViewItemType == TYPE_USER) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_item, null);
                TextView balance = (TextView)convertView.findViewById(R.id.balance);
                holder.fields.add(balance);
                balance.setText(item.get("balance"));
                TextView miles = (TextView)convertView.findViewById(R.id.miles);
                miles.setText(item.get("miles"));
                holder.fields.add(balance);
                holder.fields.add(miles);
            } else if (listViewItemType == TYPE_YEAR) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.year_item, null);
                TextView date = (TextView)convertView.findViewById(R.id.date);
                date.setText(item.get("year"));
                holder.fields.add(date);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.feed_item, null);
                TextView details = (TextView)convertView.findViewById(R.id.details);
                details.setText(item.get("details"));
                TextView comment = (TextView)convertView.findViewById(R.id.comment);
                comment.setText(item.get("comment"));
                TextView cost = (TextView)convertView.findViewById(R.id.cost);
                cost.setText(item.get("cost"));
                TextView category = (TextView)convertView.findViewById(R.id.category);
                category.setText(item.get("category"));
                TextView icon = (TextView)convertView.findViewById(R.id.icon);
                icon.setText(item.get("icon"));
                holder.fields.add(details);
                holder.fields.add(comment);
                holder.fields.add(cost);
                holder.fields.add(category);
                holder.fields.add(icon);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            if (listViewItemType == TYPE_USER) {
                holder.fields.get(0).setText(item.get("balance"));
                holder.fields.get(1).setText(item.get("miles"));
            } else if (listViewItemType == TYPE_YEAR) {
                holder.fields.get(0).setText(item.get("year"));
            } else {
                holder.fields.get(0).setText(item.get("details"));
                holder.fields.get(1).setText(item.get("comment"));
                holder.fields.get(2).setText(item.get("cost"));
                holder.fields.get(3).setText(item.get("category"));
                holder.fields.get(4).setText(item.get("icon"));
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        public List<TextView> fields = new ArrayList<>();
    }

    void setData(List<HashMap<String, String>> newItems) {
        clear();
        addAll(newItems);
        items = newItems;
        notifyDataSetChanged();
    }
}

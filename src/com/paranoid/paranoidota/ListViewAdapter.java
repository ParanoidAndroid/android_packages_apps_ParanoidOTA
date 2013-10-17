package com.paranoid.paranoidota;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
public class ListViewAdapter extends BaseAdapter {

    Context context;
    
    String[] ChangelogItems;
    
    int[] flag;
    
    LayoutInflater inflater;
 
    public ListViewAdapter(Context context, String[] aboutOptions, int[] flag) {
    	
        this.context = context;
        
        this.ChangelogItems = aboutOptions;
        
        this.flag = flag;
    }
 
    public int getCount() {
    	
        return ChangelogItems.length;
    }
 
    public Object getItem(int position) {
    	
        return null;
    }
 
    public long getItemId(int position) {
    	
        return 0;
    }
    
    public boolean isEnabled (int position) {
    	// NOT ENABLED MWAHAHAHA
    	return false;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView txtOptions;
 
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
        View itemView = inflater.inflate(R.layout.lv_items, parent, false);

        txtOptions = (TextView) itemView.findViewById(R.id.lvOptions);

        txtOptions.setText(ChangelogItems[position]);
 
        return itemView;
    }
}
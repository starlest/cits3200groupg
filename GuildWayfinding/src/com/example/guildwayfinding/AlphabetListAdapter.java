package com.example.guildwayfinding;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlphabetListAdapter extends BaseAdapter {

    public static abstract class Row {
    	private int id;
    	
    	public int getId() {
    		return id;
    	}
    	
    	public void setId(int i) {
    		this.id = i;
    	}
    }
    
    public static final class Section extends Row {
        public final String text;

        public Section(String text) {
            this.text = text;
            this.setId(-1);
        }
        
        public String toString() {
        	return text;
        }
    }
    
    public static final class Item extends Row {
        public final String text;
        
        public Item(String text, int id) {
            this.text = text;
            this.setId(id);
        }
        
        public String toString() {
        	return text;
        }
    }
    
    private List<Row> rows;
    
    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public Row getItem(int position) {
        return rows.get(position);
    }
    
    public long getItemId(int position) {
        return rows.get(position).getId();
    }
    
    @Override
    public int getViewTypeCount() {
        return 2;
    }
    
    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Section) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        
        if (getItemViewType(position) == 0) { // Item
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = (LinearLayout) inflater.inflate(R.layout.row_item, parent, false);  
            }
            
            Item item = (Item) getItem(position);
            TextView textView = (TextView) view.findViewById(R.id.textView1);
            textView.setText(item.text);
        } else { // Section
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = (LinearLayout) inflater.inflate(R.layout.row_section, parent, false);  
            }
            
            Section section = (Section) getItem(position);
            TextView textView = (TextView) view.findViewById(R.id.textView1);
            textView.setText(section.text);
        }
        
        return view;
    }

}
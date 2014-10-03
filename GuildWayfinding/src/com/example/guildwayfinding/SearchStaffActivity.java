package com.example.guildwayfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.app.ActionBar;

import com.example.guildwayfinding.AlphabetListAdapter.Item;
import com.example.guildwayfinding.AlphabetListAdapter.Row;
import com.example.guildwayfinding.AlphabetListAdapter.Section;

public class SearchStaffActivity extends ListActivity {

    private AlphabetListAdapter adapter = new AlphabetListAdapter();
    private GestureDetector mGestureDetector;
    private List<Object[]> alphabet = new ArrayList<Object[]>();
    private HashMap<String, Integer> sections = new HashMap<String, Integer>();
    private int sideIndexHeight;
    private static float sideIndexX;
    private static float sideIndexY;
    private int indexListSize;
    
    public final static String STAFF_MESSAGE = "com.example.myfirstapp.STAFF_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_alphabet);

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Back To Home");
        
        mGestureDetector = new GestureDetector(this, new SideIndexGestureListener());

        DBHelper d = new DBHelper(this);
        //d = populate(d);
        d.getReadableDatabase();
		List<String> staffs = d.getStaffsIdsNames();
		d.close();
        Collections.sort(staffs);

        List<Row> rows = new ArrayList<Row>();
        int start = 0;
        int end = 0;
        String previousLetter = null;
        Object[] tmpIndexItem = null;
        Pattern numberPattern = Pattern.compile("[0-9]");

        for (String staff : staffs) {
            String firstLetter = staff.substring(0, 1);

            // Group numbers together in the scroller
            if (numberPattern.matcher(firstLetter).matches()) {
                firstLetter = "#";
            }

            // If we've changed to a new letter, add the previous letter to the alphabet scroller
            if (previousLetter != null && !firstLetter.equals(previousLetter)) {
                end = rows.size() - 1;
                tmpIndexItem = new Object[3];
                tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
                tmpIndexItem[1] = start;
                tmpIndexItem[2] = end;
                alphabet.add(tmpIndexItem);

                start = end + 1;
            }

            // Check if we need to add a header row
            if (!firstLetter.equals(previousLetter)) {
                rows.add(new Section(firstLetter));
                sections.put(firstLetter, start);
            }

            // Add the staff to the list
            int i = staff.lastIndexOf(',');
            String staffName = staff.substring(0, i);
            int staffId = Integer.parseInt(staff.substring(i+1));
            rows.add(new Item(staffName, staffId));
            previousLetter = firstLetter;
        }

        if (previousLetter != null) {
            // Save the last letter
            tmpIndexItem = new Object[3];
            tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
            tmpIndexItem[1] = start;
            tmpIndexItem[2] = rows.size() - 1;
            alphabet.add(tmpIndexItem);
        }

        adapter.setRows(rows);
        setListAdapter(adapter);

        updateList();
    }

    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {
    	int staffId = adapter.getItem(position).getId();
    	if (staffId != 1) {
    		Intent intent = new Intent(this, StaffActivity.class);
    		intent.putExtra(STAFF_MESSAGE, staffId);
    		startActivity(intent);
    	}
    }
    
    class SideIndexGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            sideIndexX = sideIndexX - distanceX;
            sideIndexY = sideIndexY - distanceY;

            if (sideIndexX >= 0 && sideIndexY >= 0) {
                displayListItem();
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        } else {
            return false;
        }
    }

    public void updateList() {
        LinearLayout sideIndex = (LinearLayout) findViewById(R.id.sideIndex);
        sideIndex.removeAllViews();
        indexListSize = alphabet.size();
        if (indexListSize < 1) {
            return;
        }

        int indexMaxSize = (int) Math.floor(sideIndex.getHeight() / 20);
        int tmpIndexListSize = indexListSize;
        while (tmpIndexListSize > indexMaxSize) {
            tmpIndexListSize = tmpIndexListSize / 2;
        }
        double delta;
        if (tmpIndexListSize > 0) {
            delta = indexListSize / tmpIndexListSize;
        } else {
            delta = 1;
        }

        TextView tmpTV;
        for (double i = 1; i <= indexListSize; i = i + delta) {
            Object[] tmpIndexItem = alphabet.get((int) i - 1);
            String tmpLetter = tmpIndexItem[0].toString();

            tmpTV = new TextView(this);
            tmpTV.setText(tmpLetter);
            tmpTV.setGravity(Gravity.CENTER);
            tmpTV.setTextSize(15);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            tmpTV.setLayoutParams(params);
            sideIndex.addView(tmpTV);
        }

        sideIndexHeight = sideIndex.getHeight();

        sideIndex.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // coordinates of touch
                sideIndexX = event.getX();
                sideIndexY = event.getY();

                // display a proper item in staff list
                displayListItem();

                return false;
            }
        });
    }

    public void displayListItem() {
        LinearLayout sideIndex = (LinearLayout) findViewById(R.id.sideIndex);
        sideIndexHeight = sideIndex.getHeight();
        // compute number of pixels for every side index item
        double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;

        // compute the item index for given event position belongs to
        int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

        // get the item (we can do it since we know item index)
        if (itemPosition < alphabet.size()) {
            Object[] indexItem = alphabet.get(itemPosition);
            int subitemPosition = sections.get(indexItem[0]);

            //ListView listView = (ListView) findViewById(android.R.id.list);
            getListView().setSelection(subitemPosition);
        }
    }
    private DBHelper populate(DBHelper d){
    	d.addStaff("Abraham Smith", 0, "Computer Science", 0, "0424123234", "abraham_smith@staff.uwa.edu.au");
    	d.addStaff("Bryan Doyle", 1, "Biology", 0, "0234134234", "bryan_doyle@staff.uwa.edu.au");
    	d.addStaff("Charlie Beck", 2, "Chemistry", 0, "0234134235", "charlie_beck@staff.uwa.edu.au");
    	d.addStaff("Daniel Russell", 3, "Chemistry", 0, "0234134236",
    	"daniel_russell@staff.uwa.edu.au");
    	d.addStaff("Enrique Martinez", 4, "Engineering", 0, "0234134237", "enrique_martinez@staff.uwa.edu.au");
    	d.addStaff("Frank Overwood", 5, "Politics", 0, "0234134238", "frank_overwood@staff.uwa.edu.au");
    	d.addStaff("Gary Oak", 6, "Economics", 0, "0234134239", "gary_oak@staff.uwa.edu.au");
    	d.addStaff("Harry Archer", 7, "Economics", 0, "0234134240", "harry_archer@staff.uwa.edu.au");
    	d.addStaff("Ingrid Nelson", 8, "Marketing", 0, "0234134241", "ingrid_nelson@staff.uwa.edu.au");
    	d.addStaff("Jenson Button", 9, "Engineering", 0, "0234134242", "jenson_button@staff.uwa.edu.au");
    	d.addStaff("Kimi Raik", 10, "Physics", 0, "0234134243", "kimi_raik@staff.uwa.edu.au");
    	d.addStaff("Lewis Hamilton", 11, "Physics", 0, "0234134244", "lewis_hamilton@staff.uwa.edu.au");
    	d.addStaff("Marco Gasol", 12, "Biology", 0, "0233495323", "marco_gasol@staff.uwa.edu.au");
    	d.addStaff("Nigel Lawson", 13, "Computer Science", 0, "0234745644", "nigel_lawson@staff.uwa.edu.au");
    	d.addStaff("Orlando Johnson", 14, "Biochem", 0, "0231139244", "orlando_johnson@staff.uwa.edu.au");
    	d.addStaff("Pam Stanley", 15, "Chemistry", 0, "0255392044", "pam_stanley@staff.uwa.edu.au");
    	d.addStaff("Quentin Bryce", 16, "Politics", 0, "0231480234", "quentin_bryce@staff.uwa.edu.au");
    	d.addStaff("Rex Tan", 17, "Mathematics", 0, "0248579230", "rex_tan@staff.uwa.edu.au");
    	d.addStaff("Sark Albert", 18, "Economics", 0, "0231974240", "sark_albert@staff.uwa.edu.au");
    	d.addStaff("Tamika Rumio", 19, "Art", 0, "0289112290", "tamika_rumio@staff.uwa.edu.au");
    	d.addStaff("Una Malaka", 20, "Art", 0, "0232329244", "ula_malaka@staff.uwa.edu.au");
    	d.addStaff("Vince Jackson", 21, "Engineering", 0, "0234921244", "vince_jackson@staff.uwa.edu.au");
    	d.addStaff("Wesley Barker", 22, "Physics", 0, "026789134240", "wesley_barkerr@staff.uwa.edu.au");
    	d.addStaff("Xavier Markus", 23, "Psychology", 0, "0239312240", "xavier_markus@staff.uwa.edu.au");
    	d.addStaff("Yolanda Bertram", 24, "Mathematics", 0, "0241991240", "yolanda_bertram@staff.uwa.edu.au");
    	d.addStaff("Zohaib Hazi", 25, "Zoology", 0, "0247583240", "zohaib_hazi@staff.uwa.edu.au");

    	d.addSchedule(0, "1500-1700",  "1300-1400",  "0900-1700",  "1200-1400",  "0900-1100");
    	d.addSchedule(1, "1500-1700",  "1300-1400",  "0900-1700",  "1200-1400",  "0900-1100");
    	d.addSchedule(2, "1700-1800",  "0900-1400",  "0900-1700",  "0700-1400",  "0600-1100");
    	d.addSchedule(3, "1100-1700",  "1300-1400",  "1100-1400",  "1200-1500",  "0900-1400");
    	d.addSchedule(4, "1500-1700",  "1000-1400",  "1600-1700",  "1000-1400",  "1000-1100");
    	d.addSchedule(5, "1000-1700",  "1000-1400",  "1000-1700",  "1000-1400",  "1000-1100");
    	d.addSchedule(6, "1500-1700",  "1300-1700",  "0900-1700",  "1200-1700",  "0900-1117");
    	d.addSchedule(7, "1000-1700",  "1000-1700",  "1000-1700",  "1000-1700",  "1000-1700");
    	d.addSchedule(8, "-",  "1400-1600",  "0900-1700",  "-",  "0900-1100");
    	d.addSchedule(9, "-",  "-",  "-",  "-",  "-");
    	d.addSchedule(10, "0800-1700",  "0800-1400",  "0800-1700",  "0800-1400",  "0800-1100");
    	d.addSchedule(11, "1200-1400",  "1300-1400",  "0900-1400",  "1200-1400",  "0900-1400");
    	d.addSchedule(12, "1400-1700",  "1400-1500",  "1400-1700",  "1200-1400",  "1400-1500");
    	d.addSchedule(13, "1500-1700",  "1300-1400",  "0900-1700",  "1200-1400",  "0900-1100");
    	d.addSchedule(14, "0900-1000",  "1000-1100",  "1100-1700",  "1200-1600",  "1000-1100");
    	d.addSchedule(15, "0800-1700",  "0800-1400",  "0900-1700",  "0800-1400",  "0800-1100");
    	d.addSchedule(16, "1300-1500",  "1300-1500",  "1300-1500",  "1300-1500",  "1300-1500");
    	d.addSchedule(17, "-",  "-",  "-",  "-",  "-");
    	d.addSchedule(18, "1500-1700",  "1300-1400",  "-",  "-",  "1200-1600");
    	d.addSchedule(19, "-",  "1000-1400",  "1000-1700",  "1200-1700",  "1100-1300");
    	d.addSchedule(20, "1200-1700",  "1200-1400",  "1200-1700",  "1200-1400",  "1200-1100");
    	d.addSchedule(21, "-",  "-",  "-",  "-",  "0900-1700");
    	d.addSchedule(22, "1500-1700",  "1300-1400",  "0900-1700",  "1200-1400",  "0900-1100");
    	d.addSchedule(23,  "1200-1700",  "-",  "-",  "-",  "1200-1600");
    	d.addSchedule(24, "1530-1700",  "09-1400",  "0900-1700",  "0900-1400",  "0900-1100");
    	d.addSchedule(25, "1100-1700",  "1100-1400",  "1100-1700",  "1100-1400",  "1100-1200");
    
    	return d;
    }
}

package com.example.guildwayfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
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
    private TimerTask t;

    
    public final static String STAFF_MESSAGE = "com.example.myfirstapp.STAFF_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_alphabet);

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Back");
        
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
        
		t = new TimerTask(){
		    public void run() { 
		    	Intent intent = new Intent(getBaseContext(), HomeActivity.class);
		        startActivity(intent);
		    }
		};
		
		new Timer().schedule(t, 45000);;
    }

    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {
    	int staffId = adapter.getItem(position).getId();
    	if (staffId != -1) {
    		Intent intent = new Intent(SearchStaffActivity.this, StaffActivity.class);
    		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
    	d.addStaff("Aden Date", 127, "Volunteering Hub", "64885891", "aden.date@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Sophie Greer", 127, "Volunteering Hub", "64885891", "sophie.greer@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Chelsea Hayes", 130, "Student Centre", "64882295", "chelsea.hayes@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Gary Morris", 145, "Tavern", "64882318", "gary.morris@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Vishal Shah", 104, "Finance Office", "64882928", "vishal.shah@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Daryl Sanders", 104, "Finance Office", "64883407", "daryl.sanders@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Nicola Norris", 104, "Finance Office", "64882293", "nicola.norris@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Myriam Hernandez", 104, "Finance Office", "64882296", "nicola.norris@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Jia Jing Hong", 104, "Finance Office", "64882811", "jiajing.hong@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Melissa Davison", 104, "Finance Office", "64882811", "melissa.davison@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Danielle Fitzgerald", 105, "Student Assist Office", "64882292", "danielle.fitzgerald@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Patrice Mitchell", 105, "Student Assist Office", "64888017", "patrice.mitchell@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Steff Langer-Kool", 105, "Student Assist Office", "64882230", "steffi.langer-kool@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Kelvin Lee", 106, "Administration Office", "64883068", "kelvin.lee@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Jessica Toon", 106, "Administration Office", "64881681", "jessica.toon@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Jonathon Zahra", 106, "Administration Office", "64882923", "jonathon.zahra@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Beverly Macintyre", 106, "Administration Office", "64882832", "beverly.macintryre@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Ken Saverimutto", 107, "Catering Director Office", "64882295", "ken.saverimutto@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Tony Goodman", 108, "Director of Student and Corporate Services Office", "64882271", "tony.goodman@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Rodney Taylor", 109, "Catering Administration Office", "64883763", "rodney.taylor@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Irene Conway", 109, "Catering Administration Office", "64881822", "irene.conway@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Geraldine Lambert", 109, "Catering Administration Office", "64882315", "geraldine.lambert@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Chloe Jackson", 110, "Events and Creative Office", "64885340", "chloe.jackson@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Stephanie Stewart", 110, "Events and Creative Office", "64882291", "stephanie.stewart@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Lukas Wilmmler", 110, "Events and Creative Office", "64883762", "lukas.wilmmler@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Karrie McClelland", 110, "Events and Creative Office",  "64883408", "karrie.mcclelland@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Alex Pond", 110, "Events and Creative Office", "64882211", "alex.pond@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Kate Hoolahan", 110, "Events and Creative Office", "64883929", "kate.hoolahan@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Wayne HoweIls", 111, "Managing Director Office", "64883760", "wayne.howells@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Jenny Ophe I", 115, "Human Resources Office", "64884312", "jenny.ophel@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Cameron Fitzgeraldd", 116, "Student Representative Office", "64883773", "vp@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Samuel Shipley", 116, "Student Representative Office", "64883773", "secretary@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Daniel Jo", 116, "Student Representative Office", "64883773", "treasurer@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Elizabeth OÕShea", 116, "Student Representative Office", "64883773", "ed@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Madelene Mulhollad", 116, "Student Representative Office", "64883773", "soc@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Honnny Palayukan", 116, "Student Representative Office", "64883773", "pac@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("David Raithel", 116, "Postgraduate Student's Association Office", "64883194", "psa@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	d.addStaff("Thomas Henderson", 116, "President Office", "64882294", "president@guild.uwa.edu.au", "1500,1300",  "1300,1400",  "0900,1300",  "1200,1400",  "0900,1100");
    	/*
    	d.addStaff("", 1, "Eye Care Centre", "64881491", "eyecarecentres@westnet.com.au", "0830,1630", "0830,1630", "0830,1630", "0830,1630", "0830, 1630");
    	d.addStaff("", 2, "Guild Second Hand Bookshop", "64882310", "bookshop@guild.uwa.edu.au", "1000,1500", "1000,1500", "1000,1500", "1000,1500", "1000,1500");
    	d.addStaff("", 3, "Winthrop Australia Retail Outlet", "64882777", "uwa@winaust.com.au", "0845,1700", "0845,1700", "0845,1700", "0845,1700", "0845,1700");
    	d.addStaff("", 8, "Guild Village CafŽ", "64882295", "catering@guild.uwa.ed.au", "0730,1900", "0730,1900", "0730,1900", "0730,1900", "0730,1700");
    	d.addStaff("", 11, "Campus Pharmacy", "64882290", "campuspharmacy@westnet.com.au", "0900,1700", "0900,1700", "0900,1700", "0900,1700", "0900,1700");
    	d.addStaff("", 12, "Co-Op Bookshop", "61445705", "uw@coop.com.au", "0845,1730", "0845,1730", "0845,1730", "0845,1730", "0845,1730");
    	d.addStaff("", 14, "Talking Heads Hairdresser", "93812638", "sjharris@iinet.net.au", "0900,1700", "0900,1700", "0900,1700", "0900,1700", "0900,1700");
    	d.addStaff("", 15, "Campus News & Gifts", "64882283", "campusnews_uwa@live.com.au", "0800,1700", "0800,1700", "0800,1700", "0800,1700", "0800,1700");
    	d.addStaff("", 27, "Guild Volunteering Hub", "64885891", "volunteering@guild.uwa.edu.au", "0900,1700", "0900,1700", "0900,1700", "0900,1700", "0900,1700" );
    	d.addStaff("", 30, "Guild Student Centre", "64882295", "hello@guild.uwa.edu.au", "0830,1700", "0830,1700", "0830,1700", "0830,1700", "0830,1700");
    	d.addStaff("", 41, "Reflectory Office", "64882287", "refectory@guild.uwa.edu.au", "", "", "", "", "");
    	d.addStaff("", 45, "Tavern Office", "64882295", "tavern@guild.uwa.edu.au", "", "", "", "", "");
    	d.addStaff("", 129, "Winthrop Australia", "64882777", "uwa@winaust.com.au", "0845,1700", "0845,1700", "0845,1700", "0845,1700", "0845,1700");
    	d.addStaff("", 128, "UniPrint", "64883624", "uniprintshop@admin.uwa.edu.au", "0830,1730", "0830,1730", "0830,1730", "0830,1730", "0830,1730");
    	d.addStaff("", 131, "STA Travel", "64882302", "uwa@branch.statravel.com.au", "0900,1700", "0900,1700", "0900,1700", "0900,1700", "0900,1700");
    	d.addStaff("", 132, "Westpac Bank", "92848899", "dpadley@westpac.com.au", "0930,1600", "0930,1600", "0930,1600", "0930,1600", "0930,1700");
    	d.addStaff("", 135, "UniCredit", "64881218", "uw@unicredit.com.au", "", "", "", "1100,1500", "1100,1500");
    	d.addStaff("", 137, "Philips & Father", "64888507","info@philipsandfather.com", "0930,1630", "0930,1630", "0930,1630", "0930,1630", "0930,1630");
    	d.addStaff("", 152, "Pelican Office", "64882284", "pelican@guild.uwa.edu.au", "", "", "", "", "");
    	d.addStaff("", 181, "Science Union", "", "", "", "", "", "", "");
    	d.addStaff("", 182, "University Computer Club", "", "ucc-president@guild.uwa.edu.au", "", "", "", "", "");
    	d.addStaff("", 182, "University Science Fiction (and Fantasy) Association", "", "unisfa-president@guild.uwa.edu.au", "", "", "", "", "");
    	d.addStaff("", 183, "Unigames", "", "unigames-president@guild.uwa.edu.au", "", "", "", "", "");
    	d.addStaff("", 184, "University Dance Society", "", "", "", "", "", "", "");
    	d.addStaff("", 184, "UWAnime and JapSSoc", "", "japsoc-president@guild.uwa.edu.au", "", "", "", "", "");
    	d.addStaff("", 184, "Uwa Society for Creative Anachronism", "", "uwaasca-president@guild.uwa.edu.au", "", "", "", "", "");
    	d.addStaff("", 202, "UWA Medical Centre", "64882118", "0830,1700", "0830,1700", "0830,1700", "0830,1700", "0830,1700", "0830,1700");
    	d.addStaff("", 252, "NUS Office", "", "nus@guild.uwa.edu.au", "", "", "", "", "");
    	d.addStaff("", 253, "Uni Camp for kids Office", "64888763", "committee@unicampforkidds.org.au", "", "", "", "", "");
    	d.addStaff("", 254, "Women`s Department Office (Safe Zone)", "", "womens@guild.uwa.edu.au", "", "", "", "", "");
    	d.addStaff("", 255, "PROSH Office", "", "prosh@guild.uwa.edu.au", "", "", "", "", "");
    	d.addStaff("", 256, "Christian Union Club Room", "", "cu-committee@guild.uwa.edu.au", "", "", "", "", "");
    	d.addStaff("", 257, "Multicultural Week Lounge", "", "enquiries@multiculturalweek.org", "", "", "", "", "");
    	d.addStaff("", 259, "Environment Department Office", "", "environment@guild.uwa.edu.au", "", "", "", "", "");
    	d.addStaff("", 260, "AIESES Club Room", "", "aiesec-committee@guild.uwa.edu.au", "", "", "", "", "");
    	d.addStaff("", 262, "International Students Service Office", "", "iss@guild.uwa.edu.au", "", "", "", "", "");
    	d.addStaff("", 264, "Queer Department Office", "", "queer@guild.uwa.edu.au", "", "", "", "", "");
    	*/

    	d.addRoom(101, "Eye Care Centre");
    	d.addRoom(102, "Guild Second Hand Bookshop");
    	d.addRoom(103, "Winthrop Australia Retail Outlet");
    	d.addRoom(108, "Guild Village Cafe");
    	d.addRoom(111, "Campus Pharmacy");
    	d.addRoom(112, "Co-Op Bookshop");
    	d.addRoom(114, "Talking Heads Hairdresser");
    	d.addRoom(115, "Campus News & Gifts");
    	d.addRoom(127, "Volunteering Hub");
    	d.addRoom(130, "Student Centre");
    	d.addRoom(141, "Refectory Office");
    	d.addRoom(145, "Tavern Office");
    	d.addRoom(204, "Finance Office");
    	d.addRoom(205, "Student Assist Office");
    	d.addRoom(206, "Administration Office");
    	d.addRoom(207, "Catering Director Office");
    	d.addRoom(208, "Director of Student and Corporate Services Office");
    	d.addRoom(209, "Catering Administration Office");

    	
    	return d;
    }
    
    @Override
    protected void onPause() {
    	this.onStop();
    }
    
    @Override
    protected void onStop() {
    	t.cancel();
    	super.onDestroy();
    }
}

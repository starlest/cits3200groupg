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
        //this.populate(d);
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
    	d.addStaff("Aden Date", 127, "Volunteering Hub", "64885891", "aden.date@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Sophie Greer", 127, "Volunteering Hub", "64885891", "sophie.greer@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Chelsea Hayes", 130, "Student Centre", "64882295", "chelsea.hayes@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Gary Morris", 145, "Tavern", "64882318", "gary.morris@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Vishal Shah", 204, "Finance Office", "64882928", "vishal.shah@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Daryl Sanders", 204, "Finance Office", "64883407", "daryl.sanders@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Nicola Norris", 204, "Finance Office", "64882293", "nicola.norris@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Myriam Hernandez", 204, "Finance Office", "64882296", "nicola.norris@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Jia Jing Hong", 204, "Finance Office", "64882811", "jiajing.hong@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Melissa Davison", 204, "Finance Office", "64882811", "melissa.davison@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Danielle Fitzgerald", 205, "Student Assist Office", "64882292", "danielle.fitzgerald@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Patrice Mitchell", 205, "Student Assist Office", "64888017", "patrice.mitchell@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Steff Langer-Kool", 205, "Student Assist Office", "64882230", "steffi.langer-kool@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Kelvin Lee", 206, "Administration Office", "64883068", "kelvin.lee@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Jessica Toon", 206, "Administration Office", "64881681", "jessica.toon@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Jonathon Zahra", 206, "Administration Office", "64882923", "jonathon.zahra@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Beverly Macintyre", 206, "Administration Office", "64882832", "beverly.macintryre@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Ken Saverimutto", 207, "Catering Director Office", "64882295", "ken.saverimutto@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Tony Goodman", 208, "Director of Student and Corporate Services Office", "64882271", "tony.goodman@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Rodney Taylor", 209, "Catering Administration Office", "64883763", "rodney.taylor@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Irene Conway", 209, "Catering Administration Office", "64881822", "irene.conway@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Geraldine Lambert", 209, "Catering Administration Office", "64882315", "geraldine.lambert@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Chloe Jackson",210, "Events and Creative Office", "64885340", "chloe.jackson@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Stephanie Stewart", 210, "Events and Creative Office", "64882291", "stephanie.stewart@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Lukas Wilmmler", 210, "Events and Creative Office", "64883762", "lukas.wilmmler@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Karrie McClelland", 210, "Events and Creative Office",  "64883408", "karrie.mcclelland@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Alex Pond", 210, "Events and Creative Office", "64882211", "alex.pond@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Kate Hoolahan", 210, "Events and Creative Office", "64883929", "kate.hoolahan@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Wayne HoweIls", 211, "Managing Director Office", "64883760", "wayne.howells@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Jenny Ophe I", 215, "Human Resources Office", "64884312", "jenny.ophel@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Cameron Fitzgeraldd", 216, "Student Representative Office", "64883773", "vp@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Samuel Shipley", 216, "Student Representative Office", "64883773", "secretary@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Daniel Jo", 216, "Student Representative Office", "64883773", "treasurer@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Elizabeth OShea", 216, "Student Representative Office", "64883773", "ed@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Madelene Mulhollad", 216, "Student Representative Office", "64883773", "soc@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Honnny Palayukan", 216, "Student Representative Office", "64883773", "pac@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("David Raithel", 216, "Postgraduate Students Association Office", "64883194", "psa@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
    	d.addStaff("Thomas Henderson", 216, "President Office", "64882294", "president@guild.uwa.edu.au", "1000,1300",  "0900,1000,1100",  "1300,1500",  "1300,1400",  "0900,1100");
	d.addStaff("", 101, "Eye Care Centre", "64881491", "eyecarecentres@westnet.com.au", "0830,1630", "0830,1630", "0830,1630", "0830,1630", "0830, 1630");
	d.addStaff("",102, "Guild Second Hand Bookshop", "64882310", "bookshop@guild.uwa.edu.au", "1000,1500", "1000,1500", "1000,1500", "1000,1500", "1000,1500");
	d.addStaff("", 103, "Winthrop Australia Retail Outlet", "64882777", "uwa@winaust.com.au", "0845,1700", "0845,1700", "0845,1700", "0845,1700", "0845,1700");
	d.addStaff("", 108, "Guild Village Café", "64882295", "catering@guild.uwa.ed.au", "0730,1900", "0730,1900", "0730,1900", "0730,1900", "0730,1700");
	d.addStaff("", 111, "Campus Pharmacy", "64882290", "campuspharmacy@westnet.com.au", "0900,1700", "0900,1700", "0900,1700", "0900,1700", "0900,1700");
	d.addStaff("", 112, "Co-Op Bookshop", "61445705", "uw@coop.com.au", "0845,1730", "0845,1730", "0845,1730", "0845,1730", "0845,1730");
	d.addStaff("", 114, "Talking Heads Hairdresser", "93812638", "sjharris@iinet.net.au", "0900,1700", "0900,1700", "0900,1700", "0900,1700", "0900,1700");
	d.addStaff("", 115, "Campus News & Gifts", "64882283", "campusnews_uwa@live.com.au", "0800,1700", "0800,1700", "0800,1700", "0800,1700", "0800,1700");
	d.addStaff("", 127, "Guild Volunteering Hub", "64885891", "volunteering@guild.uwa.edu.au", "0900,1700", "0900,1700", "0900,1700", "0900,1700", "0900,1700" );
	d.addStaff("", 130, "Guild Student Centre", "64882295", "hello@guild.uwa.edu.au", "0830,1700", "0830,1700", "0830,1700", "0830,1700", "0830,1700");
	d.addStaff("", 141, "Reflectory Office", "64882287", "refectory@guild.uwa.edu.au", "", "", "", "", "");
	d.addStaff("", 145, "Tavern Office", "64882295", "tavern@guild.uwa.edu.au", "", "", "", "", "");
	d.addStaff("", 229, "Winthrop Australia", "64882777", "uwa@winaust.com.au", "0845,1700", "0845,1700", "0845,1700", "0845,1700", "0845,1700");
	d.addStaff("", 228, "UniPrint", "64883624", "uniprintshop@admin.uwa.edu.au", "0830,1730", "0830,1730", "0830,1730", "0830,1730", "0830,1730");
	d.addStaff("", 231, "STA Travel", "64882302", "uwa@branch.statravel.com.au", "0900,1700", "0900,1700", "0900,1700", "0900,1700", "0900,1700");
	d.addStaff("", 232, "Westpac Bank", "92848899", "dpadley@westpac.com.au", "0930,1600", "0930,1600", "0930,1600", "0930,1600", "0930,1700");
	d.addStaff("", 235, "UniCredit", "64881218", "uw@unicredit.com.au", "", "", "", "1100,1500", "1100,1500");
	d.addStaff("", 237, "Philips & Father", "64888507","info@philipsandfather.com", "0930,1630", "0930,1630", "0930,1630", "0930,1630", "0930,1630");
	d.addStaff("", 252, "Pelican Office", "64882284", "pelican@guild.uwa.edu.au", "", "", "", "", "");
	d.addStaff("", 281, "Science Union", "", "", "", "", "", "", "");
	d.addStaff("", 282, "University Computer Club", "", "ucc-president@guild.uwa.edu.au", "", "", "", "", "");
	d.addStaff("", 282, "University Science Fiction (and Fantasy) Association", "", "unisfa-president@guild.uwa.edu.au", "", "", "", "", "");
	d.addStaff("", 283, "Unigames", "", "unigames-president@guild.uwa.edu.au", "", "", "", "", "");
	d.addStaff("", 284, "University Dance Society", "", "", "", "", "", "", "");
	d.addStaff("", 284, "UWAnime and JapSSoc", "", "japsoc-president@guild.uwa.edu.au", "", "", "", "", "");
	d.addStaff("", 284, "Uwa Society for Creative Anachronism", "", "uwaasca-president@guild.uwa.edu.au", "", "", "", "", "");
	d.addStaff("", 302, "UWA Medical Centre", "64882118", "0830,1700", "0830,1700", "0830,1700", "0830,1700", "0830,1700", "0830,1700");
	d.addStaff("", 352, "NUS Office", "", "nus@guild.uwa.edu.au", "", "", "", "", "");
	d.addStaff("", 353, "Uni Camp for kids Office", "64888763", "committee@unicampforkidds.org.au", "", "", "", "", "");
	d.addStaff("", 354, "Women`s Department Office (Safe Zone)", "", "womens@guild.uwa.edu.au", "", "", "", "", "");
	d.addStaff("", 355, "PROSH Office", "", "prosh@guild.uwa.edu.au", "", "", "", "", "");
	d.addStaff("", 356, "Christian Union Club Room", "", "cu-committee@guild.uwa.edu.au", "", "", "", "", "");
	d.addStaff("", 357, "Multicultural Week Lounge", "", "enquiries@multiculturalweek.org", "", "", "", "", "");
	d.addStaff("", 359, "Environment Department Office", "", "environment@guild.uwa.edu.au", "", "", "", "", "");
	d.addStaff("", 360, "AIESEC Club Room", "", "aiesec-committee@guild.uwa.edu.au", "", "", "", "", "");
	d.addStaff("", 362, "International Students Service Office", "", "iss@guild.uwa.edu.au", "", "", "", "", "");
	d.addStaff("", 364, "Queer Department Office", "", "queer@guild.uwa.edu.au", "", "", "", "", "");
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

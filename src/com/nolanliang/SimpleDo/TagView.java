package com.nolanliang.SimpleDo;


import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class TagView extends Activity {
	
	private static final String NEW_LINE = "\n";
	
	ArrayList<String> tags;
	ArrayList<String> tasks;
	TextView tv;
	Spinner tagspinner;

	// Parse the lines for all tags and tasks and store them in a list
	public void parseTasks(ArrayList<String> lines) {
		tags = new ArrayList<String>();
		tasks = new ArrayList<String>();
		String line = "";
		for (int i = 0; i < lines.size(); i += 1) {
			line = lines.get(i);
			if (line.startsWith("-")) {
				if (addTag(line) == true) {
					tasks.add(line);
				} else {
					tasks.add(line + " @untagged");
				}
			}
		}
		Collections.sort(tags);	// Sort the tags
		tags.remove("@untagged");
		tags.add("@untagged");
		tags.remove("@done"); // Place @done at the end
		tags.add("@done");
		tags.add(0, "Show All Tasks"); // Place the "all" option at the top
	}
	
	// Adds unique tags to the tags array
	public boolean addTag(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		int token_count = tokenizer.countTokens();
		String word = new String();
		boolean line_has_tag = false;
		for (int t = 0; t < token_count; t += 1){
			word = tokenizer.nextToken();
			if (word.startsWith("@")) {
				// cycle through the tags array to weed out duplicates and place unique tags at the end
				boolean duplicate = false;
				for (int i = 0; i < tags.size(); i += 1) {
					if (word.matches(tags.get(i))) { // can't use == with these strings... huh?
						duplicate = true;
					}
				}
				if (duplicate == false) {
					tags.add(word);
				}
				line_has_tag = true;
			}
		}
		return line_has_tag;
	}
	
	
	// Print out the task list
	public void printTasks() {
		tv.setText("");
		for (int i=1; i < tags.size(); i += 1) {
			tv.append(NEW_LINE);
			tv.append(Html.fromHtml("<i>" + tags.get(i) + "</i>"));
			tv.append(NEW_LINE);
			for (int t=0; t < tasks.size(); t += 1)  {
				int spinner_choice =  tagspinner.getSelectedItemPosition();
				if (spinner_choice < 1) {
					spinner_choice = i;
				}
				if ((tasks.get(t).contains(" " + tags.get(i)) && (tasks.get(t).contains(" " + tags.get(spinner_choice))))) {
					tv.append(Html.fromHtml("<b>" + tasks.get(t).substring(0, tasks.get(t).indexOf(" @")) + "</b>"));
					tv.append(NEW_LINE);
				}				
			}
		}
	}
	
	
	// Creates the menu items 
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Project View");
	    return true;
	}

	// Handles menu item selections 
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case 1:
	    	finish();
	    }
	    return false;
	}
	
	
	

	// Called when the activity is first created. 
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);     		

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ScrollView sv = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.setBackgroundColor(Color.rgb(217, 217, 179));
        tagspinner = new Spinner(this);
        
        tv = new TextView(this);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize((float) 16.0);
        sv.addView(ll);
        ll.addView(tagspinner);
        ll.addView(tv);
        this.setContentView(sv);        
        
        parseTasks(getIntent().getStringArrayListExtra("linelist"));
     
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tags.toArray()); 
        tagspinner.setAdapter(spinnerArrayAdapter);

        tagspinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				printTasks();			
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub				
			}
        });
        
	}
    
}
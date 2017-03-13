package com.nolanliang.SimpleDo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class Main extends Activity {
	/** Static Values */
	static long DOUBLE_CLICK_SPEED = 400; //minimum length of time in milliseconds to register a doubleclick
	static int DONE_SWIPE_DIVIDER = 4; //length of line divided by this to determine a done swipe
	static String STORAGE_DIRECTORY = "/sdcard/SimpleDo/";
	static String STORAGE_FILE = "SimpleDo.txt";
	static final int DATE_DIALOG_ID = 1;
	static final int INFO_MESSAGE_ID = 0;
	static final int IMPORT_CONFIRMATION_ID = 2;
	static final int LIST_DIALOG_ID = 3;
	static final int PROJECT_REMOVE_ID = 5;
	
	private static final String NEW_LINE = "\n";
	
	static private String LOCAL_FILES = "/data/data/com.nolanliang.SimpleDo/files/";
	static private String CARD_FILES = "/sdcard/SimpleDo/";
	static private String ACTIVE_LIST = "ACTIVE LIST";
		
	private SharedPreferences prefsPrivate;
	private Editor prefsPrivateEditor;
	static private String PREFS = "PREFS";
	static private String BACKGROUND_COLOR = "BACKGROUND COLOR";
	static private String PROJECT_COLOR = "PROJECT COLOR";
	static private String TASK_COLOR = "TASK COLOR";
	static private String NOTE_COLOR = "NOTE COLOR";
	static private String DONE_COLOR = "DONE COLOR";
	
	/** Views and Widgets */
	ScrollView scrollWindow;
	LinearLayout mainLayout;
	LinearLayout container;
	LinearLayout dummyBox;
	//EditText textBox;
	ImageView handle;
    //FileHandler filer;

		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Initiate the base Views **/
		// add a dummy box to fill out the remaining space on the page if available and set to listen for click
		// this will then send a selection call to the lowest level textbox!
		container = new LinearLayout(this);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		dummyBox = new LinearLayout(this);
		dummyBox.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
		dummyBox.setClickable(true);
		dummyBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mainLayout.getChildAt(mainLayout.getChildCount()-1).requestFocus();
			}
		});
		scrollWindow = new ScrollView(this);
        mainLayout = new LinearLayout(this);                
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        scrollWindow.addView(container);
        scrollWindow.setFillViewport(true);
        container.addView(mainLayout);
        container.addView(dummyBox);
		
        setContentView(scrollWindow);

        //filer = new FileHandler();
    }
    
    /** Dialog Popup for debugging */
	public void showMessage(String text) {
		Toast message = Toast.makeText(this, text, 10);
		message.show();
	}    
        
    /** Coordinates screen Gestures */
    public boolean onTouchEvent(MotionEvent event) { 
        if (event.getAction() == MotionEvent.ACTION_MOVE) {        	
        	return true;
        } else {
        	return false;
        }
    }
	
    /** Add a new line to the page */
    public void addLine(String include_text, int line_to_add) {
    	EditText textBox = new EditText(this);
    	textBox.setText(include_text);    	
    	registerForContextMenu(textBox);
    	textBox = addLineListeners(textBox);
    	textBox = addLineFormatting(textBox, lineType(include_text)); //format the line
    	
    	mainLayout.addView(textBox, line_to_add);
    }
    
    /** Returns the type of line: project, task, note */
    public String lineType(String line_text) {
    	if (line_text.trim().endsWith(":")) {
    		return "project";
    	} else if (line_text.trim().startsWith("-")) {
    		return "task";
    	} else {
    		return "note";
    	}
    }
    
    /** format the given EditText line to it's proper line type */
	public EditText addLineFormatting(EditText textBox, String linetype) {
		String line_of_text = textBox.getText().toString().trim();
		if (linetype.matches("project")) {
			textBox.setTextSize((float) 22.0);
			textBox.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
			textBox.setTextColor(prefsPrivate.getInt(PROJECT_COLOR, Color.BLUE));
			textBox.setBackgroundColor(prefsPrivate.getInt(BACKGROUND_COLOR, Color.WHITE));
			textBox.setPadding(1, 1, 1, 1);
		} else if (linetype.matches("task")) {
			// Line is a Task, format specially if @done is present
			if (line_of_text.toLowerCase().contains("@done") ) {
				textBox.setTextSize((float) 16.0);
				textBox.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
				textBox.setTextColor(prefsPrivate.getInt(DONE_COLOR, Color.LTGRAY));
				textBox.setBackgroundColor(prefsPrivate.getInt(BACKGROUND_COLOR, Color.WHITE));
				textBox.setPadding(15, 1, 1, 1);
			} else { //default task formatting
				textBox.setTextSize((float) 16.0);
				textBox.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				textBox.setTextColor(prefsPrivate.getInt(TASK_COLOR, Color.BLACK));
				textBox.setBackgroundColor(prefsPrivate.getInt(BACKGROUND_COLOR, Color.WHITE));
				textBox.setPadding(15, 1, 1, 1);
			}
		}  else {
			textBox.setTextSize((float) 14.0);
			textBox.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
			textBox.setTextColor(prefsPrivate.getInt(NOTE_COLOR, Color.DKGRAY));
			textBox.setBackgroundColor(prefsPrivate.getInt(BACKGROUND_COLOR, Color.WHITE));
			textBox.setPadding(25, 1, 1, 1);
		}		
		return textBox;
	}

		
    /** Returns an EditText textBox with standard listening */
    public EditText addLineListeners(EditText textBox) {    	
    	// listen for longpress
    	/*
    	textBox.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return true;
			}
    	});*/    	
    	//set gesture controls
		textBox.setOnTouchListener(new OnTouchListener() {
			Float x_start = 0f;
			Long first_press = 0l;
			@Override
			public boolean onTouch(View v, MotionEvent event) {				
    			if (event.getAction() == MotionEvent.ACTION_DOWN) {
    				//show context menu on double click
    				if ((Math.abs(event.getEventTime()) - first_press) < DOUBLE_CLICK_SPEED) {
    					v.showContextMenu();
    				} else {
    					first_press = event.getEventTime();
    				}
    				//begin tracking the swipe
    				x_start = event.getX();
    			} else if (event.getAction() == MotionEvent.ACTION_UP) {
    				if ( ((Math.abs(event.getX() - x_start)) > (v.getWidth() / DONE_SWIPE_DIVIDER) ) && (x_start < event.getX())) {
    					//put focus on this item
    					v.requestFocus();
    					//add the @done tag on swipe right	    					
    					if (lineType(((EditText)v).getText().toString()).matches("task")) {
    						((EditText)v).setText(addTag( ((EditText)v).getText().toString() ,"@done"));
    					}
    				} else if ( ((Math.abs(event.getX() - x_start)) > (v.getWidth() / DONE_SWIPE_DIVIDER) ) && (x_start > event.getX())) {
    					//put focus on this item
    					v.requestFocus();
    					//remove the @done tag on swipe left	    					
    					if (lineType(((EditText)v).getText().toString()).matches("task")) {
    						((EditText)v).setText(removeTag( ((EditText)v).getText().toString() ,"@done"));
    					}
    				}
    			}
    			return false;
			}
    	});
    	
    	// listen for enter and delete key presses
    	textBox.setOnKeyListener(new OnKeyListener() {
        	public boolean onKey(View v, int keyCode, KeyEvent event) {
        		// Triggers behavior if the Enter key is pressed.
        		if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
        			//get current cursor position
        			int cursor_position = ((EditText)v).getSelectionStart();
        			//store string after the cursor
        			String carry_line = ((EditText)v).getText().toString().substring(cursor_position);
        			//remove characters after the cursor of the original line
        			((EditText)v).setText(((EditText)v).getText().toString().substring(0, cursor_position));
        			//add a new line below the current one with necessary text
        			addLine(carry_line, mainLayout.indexOfChild(v) + 1);
        			//put focus on the next line
        			mainLayout.getChildAt(mainLayout.indexOfChild(v) + 1).requestFocus();
        			//put the cursor at the beginning of the new line
        			((EditText)mainLayout.getFocusedChild()).setSelection(0);
        			return true;
        		// Triggers behavior if the Delete key is pressed at the beginning of a line.
                } else if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL) && ( ((EditText)v).getSelectionStart() == 0 ) && ( mainLayout.indexOfChild(v) > 0 ) ) {
        			//Put the focus on the previous line
                	mainLayout.getChildAt(mainLayout.indexOfChild(v) - 1).requestFocus();
                	//Get the index of the end of the previous line
                	int i = ((EditText)mainLayout.getChildAt(mainLayout.indexOfChild(v) - 1)).length();
        			//Throw the leftover text up a line
        			((EditText)mainLayout.getChildAt(mainLayout.indexOfChild(v) - 1)).append( ((EditText)v).getText().toString() );
        			//Put the cursor in the right spot, then remove the old line
        			((EditText)mainLayout.getChildAt(mainLayout.indexOfChild(v) - 1)).setSelection(i);
                	removeLine(mainLayout.indexOfChild(v));
        			return true;
                }
                return false;
            }
        });
		
		// Listen for text changes
    	textBox.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {		
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {			
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				addLineFormatting((EditText)mainLayout.getFocusedChild(), lineType(s.toString())); //reformat the line after every change in text		
			}			
		});    	
    	return textBox;
    }
    

    
    /** Removes a line from the page */
    public String removeLine(int line_to_delete) {
    	//gets the leftover text of the line to be deleted
    	String leftover_text = new String( ((EditText)mainLayout.getChildAt(line_to_delete)).getText().toString() );
    	mainLayout.removeViewAt(line_to_delete);
    	return leftover_text;
    }
    
    /** removes all @done tasks */
	public void purgeDoneTasks() {
		String line = "";
		for (int i = 0; i < mainLayout.getChildCount(); ) {
			line = ((EditText)mainLayout.getChildAt(i)).getText().toString();
			if (line.startsWith("-") && line.contains(" @done")){
				mainLayout.removeViewAt(i);
				if (mainLayout.getChildCount() < 1) { 
					addLine("", 0); // make sure there's always at least one line
				}
			} else {
				i += 1; // important to place this here to account for a changing array size
			}
		}
		//set focus back to the top
		mainLayout.getChildAt(0).requestFocus();
	}
    
    /** Moves a line from one spot to another */
    public void moveLine(int original_spot, int new_spot) {
    	//moving the line up
    	if (original_spot > new_spot) {
    		addLine(removeLine(original_spot), new_spot);
    	//moving the line down
    	} else if (original_spot < new_spot) {
    		addLine(removeLine(original_spot), new_spot - 1); //subtract one line to account for the deleted line above
    	}
    }
    
    /** Adds a tag to a task line if not already present */
    public String addTag(String line_text, String tag) {
    	if (line_text.contains(tag)) {
    		return line_text;
    	} else {
    		return line_text + " " + tag;
    	}
    }
    
    /** Removes a tag from a task line */
    public String removeTag(String line_text, String tag) {
    	return line_text.replaceAll(" " + tag, "");
    }
    
    /** Sets up the menu for the context menu */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		v.requestFocus();
		menu.setHeaderTitle(((EditText)v).getText().toString()); // put the selected line as the title
		menu.removeGroup(0); // get rid of the default context options
		menu.removeGroup(1); // get rid of the default context options
		if ( lineType(((EditText)v).getText().toString()).matches("project") ) {
			//menu.add(0, 0, 0, "copy the project");
			menu.add(0, 1, 0, "remove entire project!!!");
		} else if ( lineType(((EditText)v).getText().toString()).matches("task") ) {
			menu.add(1, 0, 0, "set date");
			menu.add(1, 1, 0, "toggle @done");
			menu.add(1, 2, 0, "remove task");
		} else if ( lineType(((EditText)v).getText().toString()).matches("note") ) {
			menu.add(2, 0, 0, "remove note");
		}
    }
    
    /** runs the contenxt menu selection */
    public boolean onContextItemSelected(MenuItem item) {
    	switch (item.getGroupId()) {
	    	case 0:
	    		switch (item.getItemId()) {
	    		case 0:
	    			return true;
	    		case 1:
	    			int starting_block = mainLayout.indexOfChild(mainLayout.getFocusedChild());
	    			removeLine(starting_block);
	    			while ( (starting_block < mainLayout.getChildCount()) && (((EditText)mainLayout.getChildAt(starting_block)).getText().toString().trim().length() > 0) && (((EditText)mainLayout.getChildAt(starting_block)).getText().toString().trim().endsWith(":") == false) ) {
	    				removeLine(starting_block);
	    				if (mainLayout.getChildCount() < 1) {
	    					addLine("", 0);
	    				}
	    			}
	    			return true;
	    		default:
	    			return false;
	    		}
	    	case 1:
	    		switch (item.getItemId()) {
	    		case 0:
	    			showDialog(DATE_DIALOG_ID);
	    			return true;
	    		case 1:
	    			if (((EditText)mainLayout.getFocusedChild()).getText().toString().toLowerCase().contains("@done") ) {
    					((EditText)mainLayout.getFocusedChild()).setText(removeTag( ((EditText)mainLayout.getFocusedChild()).getText().toString() ,"@done"));
	    			} else {
    					((EditText)mainLayout.getFocusedChild()).setText(addTag( ((EditText)mainLayout.getFocusedChild()).getText().toString() ,"@done"));
	    			}
	    			return true;
	    		case 2:
    				removeLine(mainLayout.indexOfChild(mainLayout.getFocusedChild()));
	    			return true;
	        	default:
	        		return false;
	    		}
	    	case 2:
	    		switch (item.getItemId()) {
    			case 0:
    				removeLine(mainLayout.indexOfChild(mainLayout.getFocusedChild()));
    				return true;
    			default:
    				return false;
	    		}
	    	default:
	    		return false;
	    }		
    }
    
    /** Listens for the open Dialog command */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
        	final Calendar c = Calendar.getInstance();
            return new DatePickerDialog(this, mDateSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        case INFO_MESSAGE_ID:
        	return null;
        case LIST_DIALOG_ID:
        	fileSave();
        	final String[] fileList = listFiles(LOCAL_FILES);
        	//final String[] fileList = filer.listFiles(LOCAL_FILES);
        	AlertDialog.Builder list_switcher = new AlertDialog.Builder(this);
        	list_switcher.setTitle("Select a list...");
        	list_switcher.setItems(fileList, new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int item) {
	   	        	// Save the imported file name for later export
	   	        	prefsPrivateEditor.putString(ACTIVE_LIST, fileList[item]);
	   	        	prefsPrivateEditor.commit();
    	   	    	resetView(LOCAL_FILES, fileList[item]);
    	   	    	removeDialog(LIST_DIALOG_ID);
	    	    }
		    });
	    	AlertDialog alert = list_switcher.create();
	    	return alert;	
        default:
        	return null;
        }
    }
    
    /**the callback received when the user "sets" the date in the dialog */
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
    	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    		String day = "";
    		String month = "";
    		// add "0" to the month and day if under 10
    		if (monthOfYear < 9) {
    			month = "0" + Integer.toString(monthOfYear + 1);
    		} else {
    			month = Integer.toString(monthOfYear + 1);
    		}
    		if (dayOfMonth < 10) {
    			day = "0" + Integer.toString(dayOfMonth);
    		} else {
    			day = Integer.toString(dayOfMonth);
    		}
    		//attach the due date to the end of the line
    		((EditText)mainLayout.getFocusedChild()).append(" @" + Integer.toString(year) + "/" + month + "/" + day);
        }
    };
    
    /** Called to save state when this activity loses focus */
    @Override
    protected void onPause() {
        super.onPause();
        fileSave();        
    }
    
    /** Called to load state when this activity regains focus */
    @Override
    protected void onResume() {
        super.onResume();
        prefsPrivate = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefsPrivateEditor = prefsPrivate.edit();
		resetView(LOCAL_FILES, prefsPrivate.getString(ACTIVE_LIST, "SimpleDo.txt"));	
    }
    
    /** returns a list of txt files within a given directory */
	public String[] listFiles(String given_directory) {
		// may need to account for null directory...
		File directory = new File(given_directory);
		String[] filelist = directory.list();
		ArrayList<String> file_array = new ArrayList<String>();
		for (int i = 0; i < filelist.length; i += 1) {
			if (filelist[i].toLowerCase().endsWith(".txt")) {
				file_array.add(filelist[i]);
			}			
		}
		filelist = new String[file_array.size()];
		file_array.toArray(filelist);
		return filelist;
	}
    
    public void fileSave() {
        ArrayList<String> lines = new ArrayList<String>();
        for (int i = 0; i < mainLayout.getChildCount(); i += 1) {
        	lines.add(((EditText)mainLayout.getChildAt(i)).getText().toString());
        }
        if (lines.size() > 0) {
        	FileOutputStream fos = null;
        	try {
				fos = openFileOutput(prefsPrivate.getString(ACTIVE_LIST, "SimpleDo.txt"), Context.MODE_PRIVATE);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				showMessage("Failed to open file.");
			}
			if (fos != null) {
				String fulltext = "";
				for (int i = 0; i < lines.size(); i +=1) {
					fulltext = fulltext + NEW_LINE + lines.get(i);
				}
				if (fulltext.startsWith(NEW_LINE)) {
					fulltext = fulltext.replaceFirst(NEW_LINE, "");
				}
				try {
					fos.write(fulltext.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				showMessage("Failed to save file.");
			}			
        }
    }
    
    public void resetView(String directory, String filename) {
       	prefsPrivateEditor.putString(ACTIVE_LIST, prefsPrivate.getString(ACTIVE_LIST, "SimpleDo.txt"));
       	prefsPrivateEditor.commit();
        scrollWindow.setBackgroundColor(prefsPrivate.getInt(BACKGROUND_COLOR, Color.WHITE));
        this.setTitle(prefsPrivate.getString(ACTIVE_LIST, "SimpleDo"));
        mainLayout.removeAllViews();
    	fileLoad(directory, filename);
    }
    
    public void fileLoad(String directory, String filename) {
        ArrayList<String> lines = new ArrayList<String>();
        FileInputStream fis = null;
        try {
			fis = openFileInput(filename);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		if (fis != null) {
			String fulltext = "";			
			try {
				InputStreamReader isr = new InputStreamReader(fis);
				char[] character_buffer = new char[fis.available()];
				isr.read(character_buffer);
				fulltext = new String(character_buffer);
				isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			StringTokenizer tokens = new StringTokenizer(fulltext, NEW_LINE, true);
			String token = "";		
			while(tokens.hasMoreTokens()) {
				token = tokens.nextToken();
				if ( token.contentEquals("\n")) {
					lines.add("");
				} else {
					if (lines.size() < 1) { //this is needed to ensure that a null line isn't written to.
						lines.add("");
					}
					lines.set(lines.size() -1 , token.replace("\r", "")); 	    			   
				}				
			}
			mainLayout.removeAllViews(); // remove all lines first
	        for (int i = 0; i < lines.size(); i += 1) {
	        	addLine(lines.get(i).toString(), i);
	        }
	        if (lines.size() < 1) {
	        	addLine(filename.substring(0,filename.length()-4) + ":", 0); //necessary for newly created files
	        }
	        try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			addLine("-Stay productive and have fun! -Nolan", 0);
			addLine("-finally, if you want to start fresh, long press on the beginning project " +
					"line and choose to remove the entire project", 0);
			addLine("-stay tuned for a lot more polish and additional functionality", 0);			
			addLine("From here you can change the color scheme, remove completed tasks, sort via tags, and " +
					"create/delete/export/import list files.", 0);
			addLine("-pressing the menu button will also bring up additional options", 0);
			addLine("-pressing the menu button also creates a set of options", 0);
			addLine("-long pressing or double clicking on a line will bring up a control menu for that line", 0);
			addLine("-try swiping right on a task to complete it", 0);
			addLine("-if a tag is marked as @done, it gets special formatting", 0);
			addLine("-tags can be used to sort tasks by date/priority/location/etc..", 0);
			addLine("-tasks can also hold context tags @like @this", 0);
			addLine("A line without either marker is treated as a basic note and looks like this.", 0);
			addLine("-these lines are automatically formatted", 0);
			addLine("-tasks start with a hyphen", 0);
			addLine("Projects End in a Colon:", 0);
		}
    }
    
    /* Creates the option menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, 1, 3, "Colors");
		menu.add(0, 2, 5, "Filter by Tag");
	    menu.add(0, 3, 1, "Purge @done");
	    menu.add(0, 4, 2, "Select List");
		menu.add(0, 5, 4, "Manage Files");
	    return true;
	}

	/* Handles the option menu item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case 1:
	    	Intent optionsIntent = new Intent(this, Options.class);  
	    	startActivity(optionsIntent);
	        return true;
	    case 2:
	    	// start tag view intent, pass on array of lines
	    	ArrayList<String> lines = new ArrayList<String>();
	        for (int i = 0; i < mainLayout.getChildCount(); i += 1) {
	        	lines.add(((EditText)mainLayout.getChildAt(i)).getText().toString());
	        }
	    	Intent tagIntent = new Intent(this, TagView.class);
	    	tagIntent.putStringArrayListExtra("linelist", lines);
	    	startActivity(tagIntent);
	    	return true;
	    case 3:
	    	// purge @done
	    	purgeDoneTasks();
	        return true;
	    case 4:
	    	// save to the sdcard
	    	showDialog(LIST_DIALOG_ID);
	    	return true;
	    case 5:
	    	Intent cardIntent = new Intent(this, CardOperator.class);
	    	cardIntent.putExtra("ACTIVE LIST", prefsPrivate.getString(ACTIVE_LIST, null));
	    	startActivity(cardIntent);
	    	return true;
	    default:
	    	return false;
	    }
	}
	
    
}
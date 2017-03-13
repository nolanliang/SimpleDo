package com.nolanliang.SimpleDo;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class Options extends Activity {
	
	private Editor prefsEditor;
	private SharedPreferences prefsPrivate;
	static private String PREFS = "PREFS";
	static private String BACKGROUND_COLOR = "BACKGROUND COLOR";
	static private String PROJECT_COLOR = "PROJECT COLOR";
	static private String TASK_COLOR = "TASK COLOR";
	static private String NOTE_COLOR = "NOTE COLOR";
	static private String DONE_COLOR = "DONE COLOR";
	static private int WHITE = 0;
	static private int BLACK = 1;
	static private int RED = 2;
	static private int BLUE = 3;
	static private int CYAN = 4;
	static private int GRAY = 5;
	static private int DARK_GRAY = 6;
	static private int LIGHT_GRAY = 7;
	static private int GREEN = 8;
	static private int YELLOW = 9;
	
	Spinner spinnerBackground;
    Spinner spinnerProject;
    Spinner spinnerTask;
    Spinner spinnerNote;
    Spinner spinnerDone;
    Button buttonSave;
    Button buttonExit;
	
	/** Dialog Popup for debugging */
	public void showMessage(String text) {
		Toast message = Toast.makeText(this, text, 10);
		message.show();
	} 	

	// Called when the activity is first created. 
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		prefsPrivate = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
				
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.options);
        
        spinnerBackground = (Spinner) findViewById(R.id.BackgroundSpinner);        
        spinnerProject = (Spinner) findViewById(R.id.ProjectSpinner);
        spinnerTask = (Spinner) findViewById(R.id.TaskSpinner);
        spinnerNote = (Spinner) findViewById(R.id.NoteSpinner);
        spinnerDone = (Spinner) findViewById(R.id.DoneSpinner);
        buttonSave = (Button) findViewById(R.id.ConfirmButton);
        buttonExit = (Button) findViewById(R.id.ExitButton);
        
        spinnerBackground.setSelection(positionValue(prefsPrivate.getInt(BACKGROUND_COLOR, positionValue(WHITE))));
        spinnerProject.setSelection(positionValue(prefsPrivate.getInt(PROJECT_COLOR, positionValue(BLACK))));
        spinnerTask.setSelection(positionValue(prefsPrivate.getInt(TASK_COLOR, positionValue(BLACK))));
        spinnerNote.setSelection(positionValue(prefsPrivate.getInt(NOTE_COLOR, positionValue(DARK_GRAY))));
        spinnerDone.setSelection(positionValue(prefsPrivate.getInt(DONE_COLOR, positionValue(GRAY))));
        
        buttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveOptions();
			}        	
        });
        
        buttonExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}        	
        });        
	}
    
    // Saves the color options in the shared preferences
    public void saveOptions() {    	
    	prefsEditor = prefsPrivate.edit();
    	
    	prefsEditor.putInt(BACKGROUND_COLOR, colorValue(spinnerBackground.getSelectedItemPosition()));
    	prefsEditor.putInt(PROJECT_COLOR, colorValue(spinnerProject.getSelectedItemPosition()));
    	prefsEditor.putInt(TASK_COLOR, colorValue(spinnerTask.getSelectedItemPosition()));
    	prefsEditor.putInt(NOTE_COLOR, colorValue(spinnerNote.getSelectedItemPosition()));
    	prefsEditor.putInt(DONE_COLOR, colorValue(spinnerDone.getSelectedItemPosition()));
    	
    	prefsEditor.commit();
    	showMessage("Color style has been saved.");
    	//showMessage(Integer.toString(prefsPrivate.getInt(BACKGROUND_COLOR, 6969)));
    }
 
    // Returns the color value of a selected spinner color
    public int colorValue(int position) {
    	int color = Color.WHITE;
    	if (position == WHITE) {
    		color =  Color.WHITE;
    	} else if (position == BLACK){
    		color =  Color.BLACK;
    	} else if (position == RED){
    		color = Color.RED;
		} else if (position == BLUE){
			color = Color.BLUE;
		} else if (position == CYAN){
			color = Color.CYAN;
		} else if (position == GRAY){
			color = Color.GRAY;
		} else if (position == DARK_GRAY){
			color = Color.DKGRAY;
		} else if (position == LIGHT_GRAY){
			color = Color.LTGRAY;
		} else if (position == GREEN){
			color = Color.GREEN;
		} else if (position == YELLOW){
			color = Color.YELLOW;
    	}
    	return color;
    }
    
    // Returns the spinner position of a color value
    public int positionValue(int color) {
    	int position = color;
    	if (color == Color.WHITE) {
    		position = WHITE;
    	} else if (color == Color.BLACK){
    		position = BLACK;
    	} else if (color == Color.RED){
    		position = RED;
		} else if (color == Color.BLUE){
			position = BLUE;
		} else if (color == Color.CYAN){
			position = CYAN;
		} else if (color == Color.GRAY){
			position = GRAY;
		} else if (color == Color.DKGRAY){
			position = DARK_GRAY;
		} else if (color == Color.LTGRAY){
			position = LIGHT_GRAY;
		} else if (color == Color.GREEN){
			position = GREEN;
		} else if (color == Color.YELLOW){
			position = YELLOW;
    	}
    	return position;
    }
}
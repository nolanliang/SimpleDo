package com.nolanliang.SimpleDo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class CardOperator extends Activity{

	ScrollView sv;
	LinearLayout mainLayout;
	LinearLayout buttonLayout;
	LinearLayout fileLayout;
	static private String LOCAL_FILES = "/data/data/com.nolanliang.SimpleDo/files/";
	static private String CARD_FILES = "/sdcard/SimpleDo/";
	static final int CREATE_LIST_DIALOG_ID = 1;
	
	Boolean is_local;
	
	ArrayList<CheckBox> lists;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				        
		is_local = true;
	
	    //requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    buttonLayout = new LinearLayout(this);
	    buttonLayout.setOrientation(LinearLayout.VERTICAL);
	    
	    fileLayout = new LinearLayout(this);
	    fileLayout.setOrientation(LinearLayout.VERTICAL);

	    sv = new ScrollView(this);
	    sv.addView(fileLayout);

	    mainLayout = new LinearLayout(this);
	    mainLayout.setOrientation(LinearLayout.VERTICAL);
	    mainLayout.addView(buttonLayout);
	    mainLayout.addView(sv);
	    this.setContentView(mainLayout);
	    
	    setLocalScreen();
	}
	
    /** Dialog Popup for debugging */
	public void showMessage(String text) {
		Toast message = Toast.makeText(this, text, 10);
		message.show();
	} 
	
	private void setCardScreen() {
		is_local = false;
		this.setTitle("SD Card");
		buttonLayout.removeAllViews();
		buttonLayout.setBackgroundColor(Color.BLACK);
	    Button importButton = new Button(this);
	    importButton.setText("import selected files to your phone memory");
	    //exportButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
	    importButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//run the export method
				copyLists(CARD_FILES, LOCAL_FILES);
			}	    	
	    });
	    
	    Button deleteButton = new Button(this);
	    deleteButton.setText("delete lists");
	    //deleteButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
	    deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//run the deletion method
				deleteLists(CARD_FILES);
				fileLayout.removeAllViews();
				populateFileList(CARD_FILES);
			}	    	
	    });
	    
	    buttonLayout.addView(importButton);
	    //buttonLayout.addView(createButton);
	    buttonLayout.addView(deleteButton);
	    populateFileList(CARD_FILES);
	}
			
	private void setLocalScreen() {
		is_local = true;
        this.setTitle("Phone Memory");
		buttonLayout.removeAllViews();
		buttonLayout.setBackgroundColor(Color.BLACK);
	    Button exportButton = new Button(this);
	    exportButton.setText("export selected files to your sdCard");
	    //exportButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
	    exportButton.setOnClickListener(new OnClickListener() {
			File tempCardFile = new File(CARD_FILES);
			@Override
			public void onClick(View v) {
				tempCardFile.mkdirs();
				//run the export method
				copyLists(LOCAL_FILES, CARD_FILES);
			}	    	
	    });
	    
	    Button createButton = new Button(this);
	    createButton.setText("create a list");
	    //createButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
	    createButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//run the new list method
				showDialog(CREATE_LIST_DIALOG_ID);
			}	    	
	    });
	    
	    Button deleteButton = new Button(this);
	    deleteButton.setText("delete lists");
	    //deleteButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
	    deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//run the deletion method
				deleteLists(LOCAL_FILES);
				fileLayout.removeAllViews();
				populateFileList(LOCAL_FILES);
			}	    	
	    });
	    
	    buttonLayout.addView(exportButton);
	    buttonLayout.addView(createButton);
	    buttonLayout.addView(deleteButton);
	    populateFileList(LOCAL_FILES);
	}
	
	// Fills the fileLayout portion of the screen with the local file list
	private void populateFileList(String location) {
		fileLayout.removeAllViews();
	    ArrayList<String> file_array = getLists(location);
	    for (int i = 0; i < file_array.size(); i++) {
	    	CheckBox listCheck = new CheckBox(this);
	    	listCheck.setText( file_array.get(i).toString() );
	    	fileLayout.addView(listCheck);
	    }
	}
	
	// Returns an ArrayList<String> of the .txt filenames for a given path
	private ArrayList<String> getLists(String path) {	
		ArrayList<String> temp_array = new ArrayList<String>();	
		File directory = new File(path);
		if (directory.exists()) {
			String[] filelist = directory.list();
			for (int i = 0; i < filelist.length; i ++) {
				if (filelist[i].toLowerCase().endsWith(".txt")) {
					temp_array.add(filelist[i]);
				}			
			}
		}
		return temp_array;
	}
	
	private void deleteLists(String path) {
		File fileToDelete;
		// don't count the first 3 children since they are buttons
		for (int i = fileLayout.getChildCount() - 1; i > -1; i--) {
			if ( ((CheckBox)fileLayout.getChildAt(i)).isChecked() ) {
				String boney = getIntent().getExtras().getString("ACTIVE LIST");
				String honey = ((CheckBox)fileLayout.getChildAt(i)).getText().toString();
				if ((((CheckBox)fileLayout.getChildAt(i)).getText().toString().contentEquals( getIntent().getExtras().getString("ACTIVE LIST") ))
						&& (is_local)) {
					showMessage("Cannot delete " + getIntent().getExtras().getString("ACTIVE LIST") + ". It is in use.");
				} else {
					fileToDelete = new File( path + ((CheckBox)fileLayout.getChildAt(i)).getText() );
					fileToDelete.delete();
				}
			}
		}
	}
	
	private void createList(String path) {
		File filetoCreate = new File(path);
		try {
			filetoCreate.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	private void copyLists(String from, String to) {
		// don't count the first 3 children since they are buttons
		CopyFile copyHelper = new CopyFile();
		for (int i = fileLayout.getChildCount() - 1; i > -1; i--) {
			if ( ((CheckBox)fileLayout.getChildAt(i)).isChecked() ) {
				copyHelper.copyfile(from + ((CheckBox)fileLayout.getChildAt(i)).getText(), to + ((CheckBox)fileLayout.getChildAt(i)).getText());
			}
		}
	}

	/** Listens for the open Dialog command */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case CREATE_LIST_DIALOG_ID:
        	final Dialog exportDialog = new Dialog(this);
        	final EditText filenamebox = new EditText(this);
        	filenamebox.setSingleLine(true);
        	LinearLayout createListLayout = new LinearLayout(this);
        	createListLayout.setOrientation(LinearLayout.VERTICAL);
        	Button ok = new Button(this);
        	ok.setText("Create");
        	ok.setOnTouchListener(new OnTouchListener() {
        		@Override
				public boolean onTouch(View v, MotionEvent event) {
        			if ( (event.getAction() == MotionEvent.ACTION_DOWN) && (filenamebox.getText().toString().length() > 0) ) {
				        	String newfilename = "";
				        	if (filenamebox.getText().toString().toLowerCase().endsWith(".txt")) {
				        		newfilename = filenamebox.getText().toString();
				        	} else {
				        		newfilename = filenamebox.getText().toString() + ".txt";
				        	}
				        	createList(LOCAL_FILES + newfilename);
					        removeDialog(CREATE_LIST_DIALOG_ID);
					        fileLayout.removeAllViews();
							populateFileList(LOCAL_FILES);
							return true;
        			} else {
        				return false;
        			}
				}        		
        	});
        	Button cancel = new Button(this);
        	cancel.setText("cancel");
        	cancel.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
			        removeDialog(CREATE_LIST_DIALOG_ID);
        			return true;
        		}
        	});
        	createListLayout.addView(filenamebox);
        	createListLayout.addView(ok);
        	createListLayout.addView(cancel);
        	// retrieve the name of the last file imported
        	// setup the dialog
        	exportDialog.setTitle("Enter a name for your list...");
        	exportDialog.setContentView(createListLayout);
	    	return exportDialog;
        default:
        	return null;
        }
    }

    public class CopyFile {
		  private void copyfile(String srFile, String dtFile){
		    try{
		      File f1 = new File(srFile);
		      File f2 = new File(dtFile);
		      InputStream in = new FileInputStream(f1);
		      
		      //For Append the file.
//		      OutputStream out = new FileOutputStream(f2,true);

		      //For Overwrite the file.
		      OutputStream out = new FileOutputStream(f2);

		      byte[] buf = new byte[1024];
		      int len;
		      while ((len = in.read(buf)) > 0){
		        out.write(buf, 0, len);
		      }
		      in.close();
		      out.close();
		      System.out.println("File copied.");
		    }
		    catch(FileNotFoundException ex){
		      System.out.println(ex.getMessage() + " in the specified directory.");
		      System.exit(0);
		    }
		    catch(IOException e){
		      System.out.println(e.getMessage());      
		    }
		  }
		  public void main(String[] args){
		    switch(args.length){
		      case 0: System.out.println("File has not mentioned.");
		          System.exit(0);
		      case 1: System.out.println("Destination file has not mentioned.");
		          System.exit(0);
		      case 2: copyfile(args[0],args[1]);
		          System.exit(0);
		      default : System.out.println("Multiple files are not allow.");
		            System.exit(0);
		    }
		  }
		}
    
    
    /* Creates the option menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Toggle Phone/SDCard");
	    menu.add(0, 2, 0, "Back to Project View");
	    return true;
	}

	/* Handles the option menu item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case 1:
	    	if (is_local == true) {
	    		setCardScreen();
	    	} else {
	    		setLocalScreen();
	    	}
	        return true;
	    case 2:
	    	this.finish();
	    	return true;
	    default:
	    	return false;
	    }
	}
    
}

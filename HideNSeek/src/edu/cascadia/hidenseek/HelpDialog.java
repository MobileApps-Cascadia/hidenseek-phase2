package edu.cascadia.hidenseek;

import java.lang.String;




import android.R;
import android.R.string;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class HelpDialog extends DialogFragment {
	
	public static String helpText; 
	private static String header;
	HelpDialog(String help,String title)
	{
		helpText = help;
		header=title;
	}
   

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setTitle(header);
    	builder.setMessage(helpText)    	
    	.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dismiss();
                   }
               });
              
        // Create the AlertDialog object and return it
        return builder.create();
}
}
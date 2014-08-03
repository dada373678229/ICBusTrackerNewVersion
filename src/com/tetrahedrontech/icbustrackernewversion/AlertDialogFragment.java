package com.tetrahedrontech.icbustrackernewversion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

//http://developer.android.com/guide/topics/ui/dialogs.html
public class AlertDialogFragment extends DialogFragment{
	private String msg;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg)
               .setPositiveButton(msg, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // FIRE ZE MISSILES!
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
	
	public void setMsg(String msg){
		this.msg=msg;
	}
}

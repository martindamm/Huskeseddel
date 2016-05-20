package org.projects.shoppinglist;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Kristoffer on 05-05-2016.
 */
public class MyDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Kommer dialogbuilder
        AlertDialog.Builder alert = new AlertDialog.Builder(
                getActivity());
        alert.setTitle("Slet listen");
        alert.setMessage("Vil du slette hele listen?");
        alert.setPositiveButton("Yup", pListener);
        alert.setNegativeButton("Hov, nej!", nListener);

        return alert.create();
    }

// Her kommer "ja" button
    DialogInterface.OnClickListener pListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            positiveClick();
        }
    };

// Her kommer "nej" button
    DialogInterface.OnClickListener nListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            negativeClick();
        }
    };

    protected void positiveClick()
    {

    }
    protected void negativeClick()
    {

    }
}

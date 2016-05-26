package org.projects.shoppinglist;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class MyDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Håndtering af kode til sletning af hele listen.
        AlertDialog.Builder alert = new AlertDialog.Builder(
                getActivity());
        alert.setTitle("Slet listen");
        alert.setMessage("Vil du slette hele listen?");
        alert.setPositiveButton("Ja", pListener);
        alert.setNegativeButton("Nej", nListener);

        return alert.create();
    }

    // Ja knappen
    DialogInterface.OnClickListener pListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            positiveClick();
        }
    };

    // Nej knap
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

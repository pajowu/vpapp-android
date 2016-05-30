package de.pajowu.vp.models;

import de.pajowu.vp.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;

import android.content.Intent;
import android.net.Uri;

public class FCMMessage {
	public String msg, title, action, url;

	public AlertDialog.Builder getAlert(final Context cont) {

		AlertDialog.Builder alert = new AlertDialog.Builder(cont);
        alert.setTitle(title);
        alert.setMessage(msg);
        alert.setPositiveButton(cont.getString(R.string.ok), 
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {}});
        if (url != null && !url.equals("")) {
            alert.setNegativeButton(cont.getString(R.string.open_url),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        cont.startActivity(i);
                    }
                }
            );
        }

        return alert;
	}
}
package shinado.indi.lib.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

public class FunctionUtil {

	public static void dial(Context context, String num) {
		if(!num.equals("")){
			Intent myIntentDial = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+num));  
			context.startActivity(myIntentDial);
		}
	}

	public static void txtTo(Context context, String num) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("smsto:" + num));
		context.startActivity(intent);
	}
	
	public static void launchPhone(Context context){
		Intent intent = new Intent(Intent.ACTION_DIAL,null);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	public static void copyToClipboard(Context context, String text){
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(text);
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData.newPlainText("text label", text);
			clipboard.setPrimaryClip(clip);
		}
	}

}

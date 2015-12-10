package shinado.indi.lib.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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
		intent.setData(Uri.parse("smsto:"+num));
		context.startActivity(intent);
	}
	
	public static void launchPhone(Context context){
		Intent intent = new Intent(Intent.ACTION_DIAL,null);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

}

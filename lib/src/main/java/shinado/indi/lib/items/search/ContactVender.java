package shinado.indi.lib.items.search;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.launcher.SearchHelper;
import shinado.indi.lib.launcher.Searchable;
import shinado.indi.lib.util.ContactManager;


public class ContactVender extends SearchVender {

	private ContactManager contactManager;

	@Override
	public void init(Context context, SearchHelper s, int type) {
		super.init(context, s, type);
		contactManager = ContactManager.getContactManager(context);
		contactManager.addOnContactChangeListener(new ContactManager.OnContactChangeListener() {
			@Override
			public void onContactChange() {
				refreshContacts();
			}
		});
		//TODO takes a lot of time
		refreshContacts();
	}

	@Override
	public int function(VenderItem rs) {
		String body = rs.getValue();
		if(!body.equals("")){
			Intent myIntentDial = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+body));
			context.startActivity(myIntentDial);
			addFrequency(rs);
			return 1;
		}else{
			return 0;
		}
	}

	private void refreshContacts() {  
		for(int i=0 ;i<contactManager.getContactCount(); i++){
			VenderItem vo = contactManager.getResult(i);
			fulfillResult(vo);
			vo.getName();
			putItemInMap(vo);
			allItems.add(vo);
		}
		resultStack.push(frequentItems);
	}

}

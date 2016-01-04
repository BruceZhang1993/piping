package shinado.indi.lib.items.search;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.items.search.translator.AbsTranslator;
import shinado.indi.lib.launcher.SearchHelper;
import shinado.indi.lib.launcher.Searchable;
import shinado.indi.lib.util.ContactManager;


public class ContactVender extends SearchVender {

	private ContactManager contactManager;

	public void load(final AbsTranslator translator){
		new Thread() {
			public void run() {
				while (!translator.ready()) {
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				contactManager = ContactManager.getContactManager(context, translator);
				contactManager.addOnContactChangeListener(new ContactManager.OnContactChangeListener() {
					@Override
					public void onContactChange() {
						refreshContacts();
					}
				});
				refreshContacts();
			}
		}.start();

	}


	@Override
	public VenderItem getItem(String value) {
		//TODO
		return null;
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
		}
		contactManager.destroy();
		resultStack.push(frequentItems);
	}

}

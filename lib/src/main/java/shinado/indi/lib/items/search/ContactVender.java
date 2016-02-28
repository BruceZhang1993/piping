package shinado.indi.lib.items.search;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.items.search.translator.AbsTranslator;
import shinado.indi.lib.util.ContactManager;


public class ContactVender extends SearchVender {

	private ContactManager contactManager;

	public ContactVender(int id) {
		super(id);
	}

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
	public int getType() {
		return VenderItem.TYPE_CONTACT;
	}


	@Override
	public VenderItem getItem(String value) {
		//TODO
		return null;
	}

	@Override
	public void acceptInput(VenderItem rs, String input) {
		String strUri = "smsto:" + rs.getValue().body;
		Log.d("testURI", strUri);
		Uri uri = Uri.parse(strUri);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra("sms_body", input);
		context.startActivity(it);
	}

	@Override
	public void getOutput(VenderItem result, OutputCallback callback) {
		callback.onOutput(result.getDisplayName() + ":" + result.getValue().body);
	}

	@Override
	public void execute(VenderItem rs) {
		final VenderItem.Value value = rs.getValue();
		if(!value.isBodyEmpty()){
			Intent myIntentDial = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+value.body));
			context.startActivity(myIntentDial);
			addFrequency(rs);
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

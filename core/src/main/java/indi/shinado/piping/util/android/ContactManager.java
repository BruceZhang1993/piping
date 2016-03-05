package indi.shinado.piping.util.android;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.impl.PipesLoader;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;


public class ContactManager {

	private AbsTranslator mTranslator;
	private static final String TAG = "Contact@Manager";
	private static final String[] PHONES_PROJECTION = new String[] {
	    Phone.CONTACT_ID, Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_ID};
	private HashMap<String, Contact> map;

	public static ContactManager getContactManager(Context context, AbsTranslator translator){
		if(contactManager == null){
			contactManager = new ContactManager(context, translator);
		}
		return contactManager;
	}
	private static ContactManager contactManager;
	private Context context;

	private ContactManager(Context context, AbsTranslator translator){
		if (this.mTranslator == null){
			this.mTranslator = translator;
		}
		this.context = context;
		refreshContacts();
	}

	//TODO what are you nong sha nie
	private Contact getContact(String tel){
		Contact c = map.get(tel);
		if(c == null){
			c = map.get(tel.replace("+86", ""));
			if(c == null){
				c = map.get("+86"+tel);
			}
		}
		return c;
	}
	
	public String getName(String tel){
		Contact c = getContact(tel);
		if(c != null){
			return c.name;
		}
		return tel;
	}

	public Bitmap getPhoto(String tel){
		Contact c = getContact(tel);
		if(c != null){
			if(c.photo != null){
				return BitmapUtil.roundBitmap(c.photo);
			}
		}
		return null;
	}
	
	public void onContactUpdate(){
		refreshContacts();
		if(onContactChangeListener != null){
			for(OnContactChangeListener l:onContactChangeListener){
				l.onContactChange();
			}
		}
	}

	private void refreshContacts() {
		Log.d(TAG, "start loading contacts");
		map = new HashMap<>();
		ContentResolver resolver = context.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);  
		if (phoneCursor != null) {  
		    while (phoneCursor.moveToNext()) {  
			    String phoneNumber = phoneCursor.getString(2).replace(" ", "");  
			    if (TextUtils.isEmpty(phoneNumber))  
			        continue;  
			    Contact contact = new Contact();
			    String contactName = phoneCursor.getString(1);
			    long photoId = phoneCursor.getLong(3);
			    long contactId = phoneCursor.getLong(0);
			    Bitmap bm = null;
			    if(photoId > 0 ) {
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                    bm = BitmapFactory.decodeStream(input);
                }
			    contact.name = contactName;
			    contact.photo = bm;
			    map.put(phoneNumber, contact);
		    }
		    phoneCursor.close();       
		}
		Log.d(TAG, "end loading contacts");
	}  

	public Pipe getResult(int idx){
		Object[] array = map.keySet().toArray();
		String tel = (String) array[idx];
		return getResult(tel);
	}
	    
	public Pipe getResult(String value){
		String label = getName(value);
		Pipe item = new Pipe(PipesLoader.ID_APPLICATION, label, mTranslator.getName(label), value);
		item.setTypeIndex(Pipe.TYPE_SEARCHABLE);
		return item;
	}
	
	public int getContactCount(){
		return map.size();
	}

	public void destroy() {
		mTranslator.destroy();
		contactManager = null;
	}

	public interface OnContactChangeListener{
		public void onContactChange();
	}
	private ArrayList<OnContactChangeListener> onContactChangeListener =
			new ArrayList<OnContactChangeListener>();;
	public void addOnContactChangeListener(OnContactChangeListener onAppChangeListener) {
		this.onContactChangeListener.add(onAppChangeListener);
	}

	private class Contact{
		String name;
		Bitmap photo;
	}
}

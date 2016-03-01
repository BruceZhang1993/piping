package indi.shinado.piping.pipes.search;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;
import indi.shinado.piping.util.android.ContactManager;

public class ContactPipe extends SearchablePipe {

    public ContactPipe(int id) {
        super(id);
    }

    @Override
    public void load(final AbsTranslator translator, final OnItemsLoadedListener listener, final int total) {
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
                listener.onItemsLoaded(ContactPipe.this.getId(), total);
            }
        }.start();

    }

    private ContactManager contactManager;

    @Override
    public void acceptInput(Pipe rs, String input) {
        String strUri = "smsto:" + rs.getExecutable();
        Uri uri = Uri.parse(strUri);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", input);
        context.startActivity(it);
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        callback.onOutput(result.getDisplayName() + ":" + result.getExecutable());
    }

    @Override
    public void execute(Pipe rs) {
        Intent myIntentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + rs.getExecutable()));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        context.startActivity(myIntentDial);
    }

    private void refreshContacts() {
        for (int i = 0; i < contactManager.getContactCount(); i++) {
            Pipe vo = contactManager.getResult(i);
            vo.setBasePipe(this);
            putItemInMap(vo);
        }
        contactManager.destroy();
    }

}

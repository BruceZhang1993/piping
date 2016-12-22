package indi.shinado.piping.storage;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;

import indi.shinado.piping.storage.implement.FirebaseDatabaseReference;

public class StorageFactory {

    public static final int USE_FIREBASE = 1;
    public static final int USE_WILDDOG = 2;

    public static IDataBaseReference getStorage(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String usage = bundle.getString("database");
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NullPointerException e) {
        }

        return new FirebaseDatabaseReference(FirebaseDatabase.getInstance().getReference());
    }
}

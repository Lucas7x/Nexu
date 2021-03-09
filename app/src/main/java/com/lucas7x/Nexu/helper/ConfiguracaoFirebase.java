package com.lucas7x.Nexu.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {

    private static DatabaseReference db;
    private static FirebaseAuth auth;
    private static StorageReference storage;

    //retorna instância do FirebaseDatabase
    public static DatabaseReference getFirebaseDatabase() {
        if (db == null) {
            db = FirebaseDatabase.getInstance().getReference();
        }

        return db;
    }

    //retorna instância do FirebaseAuth
    public static FirebaseAuth getFirebaseAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }

        return auth;
    }

    public static StorageReference getFirebaseStorage() {
        if (storage == null) {
            storage = FirebaseStorage.getInstance().getReference();
        }

        return storage;
    }

}

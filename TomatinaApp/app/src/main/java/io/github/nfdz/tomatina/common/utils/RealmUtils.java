package io.github.nfdz.tomatina.common.utils;

import io.realm.RealmConfiguration;

public class RealmUtils {

    private static final String DB_NAME = "tomatina.realm";
    private static final long SCHEMA_VERSION_NAME = 1;

    public static RealmConfiguration getConfiguration() {
        return new RealmConfiguration.Builder()
                .name(DB_NAME)
                .schemaVersion(SCHEMA_VERSION_NAME)
                .deleteRealmIfMigrationNeeded()
                .build();
    }

}

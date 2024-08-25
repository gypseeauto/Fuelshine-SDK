package com.gypsee.sdk.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseExecutor {

    private static final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    public static ExecutorService getInstance() {
        return databaseExecutor;
    }
}

package com.shevaalex.android.rickmortydatabase.utils.networking;

public class ConnectionModel {
    private final boolean isConnected;

    ConnectionModel(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }
}

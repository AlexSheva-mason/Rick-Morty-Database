package com.shevaalex.android.rickmortydatabase.networking;

public class ConnectionModel {
    private final boolean isConnected;

    ConnectionModel(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }
}

package com.shevaalex.android.rickmortydatabase.networking;

public class ConnectionModel {
    private final int type;
    private final boolean isConnected;

    ConnectionModel(int type, boolean isConnected) {
        this.type = type;
        this.isConnected = isConnected;
    }

    public int getType() {
        return type;
    }

    public boolean isConnected() {
        return isConnected;
    }
}

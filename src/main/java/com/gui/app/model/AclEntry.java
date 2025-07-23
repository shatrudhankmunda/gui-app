package com.gui.app.model;


public class AclEntry {
    private String filename;
    private String owner;
    private String sharedUser;
    private boolean canRead;
    private boolean canWrite;

    // Getters and Setters
  public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSharedUser() {
        return sharedUser;
    }

    public void setSharedUser(String sharedUser) {
        this.sharedUser = sharedUser;
    }

    public boolean isCanRead() {
        return canRead;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }
}

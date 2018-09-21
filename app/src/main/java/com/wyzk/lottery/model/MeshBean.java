package com.wyzk.lottery.model;

import com.heaven7.android.dragflowlayout.IDraggable;

/**
 * Created by liuzhenhua on 18-9-21.
 */

public class MeshBean implements IDraggable{
    private int meshId = -1;
    private int backgtoundId = -1;

    public int getBackgtoundId() {
        return backgtoundId;
    }

    public void setBackgtoundId(int backgtoundId) {
        this.backgtoundId = backgtoundId;
    }

    boolean draggable = true;

    public int getMeshId() {
        return meshId;
    }

    public void setMeshId(int meshId) {
        this.meshId = meshId;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    @Override
    public boolean isDraggable() {
        return draggable;
    }
}

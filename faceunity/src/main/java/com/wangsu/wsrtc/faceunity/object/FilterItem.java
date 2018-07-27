package com.wangsu.wsrtc.faceunity.object;

public class FilterItem {

    public int nameID;
    public int drawableID;
    public int filterType;
    public String modelName;

    public FilterItem(int name, int drawable, int type) {
        nameID = name;
        drawableID = drawable;
        filterType = type;
    }

    public FilterItem(int nameID, int drawableID, String modelName) {
        this.nameID = nameID;
        this.drawableID = drawableID;
        this.modelName = modelName;
    }


}

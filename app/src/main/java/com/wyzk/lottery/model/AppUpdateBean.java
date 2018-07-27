package com.wyzk.lottery.model;

import java.io.Serializable;
import java.util.List;

public class AppUpdateBean implements Serializable {

    /**
     * verCode : 1
     * verName : v0.0.1
     * force : 0
     * downloadUrl : http://120.77.252.48/file/app-debug.apk
     * content : [{"id":1,"text":"增加了摇一摇功能测"},{"id":2,"text":"优化了拖曳缓冲特效"},{"id":3,"text":"改善菜单用户体验测"},{"id":4,"text":"增加更多人名趣事测"}]
     */

    private int verCode;
    private String verName;
    private int force;
    private String downloadUrl;
    private String content;

    public int getVerCode() {
        return verCode;
    }

    public void setVerCode(int verCode) {
        this.verCode = verCode;
    }

    public String getVerName() {
        return verName;
    }

    public void setVerName(String verName) {
        this.verName = verName;
    }

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "AppUpdateBean{" +
                "verCode=" + verCode +
                ", verName='" + verName + '\'' +
                ", force=" + force +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

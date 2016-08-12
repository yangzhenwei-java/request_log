package com.github.log.conf;

/**
 * Created by yzw on 16/8/2.
 */
public class MatchesRes {




    /**
     * 是否记录日志
     */
    private boolean log;

    /**
     * 是否记录返回值
     */
    private boolean logRes;


    public MatchesRes(boolean log, boolean logRes) {
        this.log = log;
        this.logRes = logRes;
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public boolean isLogRes() {

        return log&&logRes;
    }

    public void setLogRes(boolean logRes) {
        this.logRes = logRes;
    }
}

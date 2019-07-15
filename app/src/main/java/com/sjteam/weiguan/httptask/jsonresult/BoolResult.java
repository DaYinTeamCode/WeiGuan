package com.sjteam.weiguan.httptask.jsonresult;

import com.androidex.zbuild.IKeepSource;

/**
 *
 *
 * @author : pzwwei
 * @crate : 2017 - 02 - 21 下午6:58
 */

public class BoolResult implements IKeepSource {

    private boolean result;

    public boolean isResult() {

        return result;
    }

    public boolean isSuccess() {

        return result;
    }

    public void setResult(boolean result) {

        this.result = result;
    }
}

package com.redefine.welike.common.api;

import com.redefine.sunny.ITask;
import com.redefine.sunny.api.ACTION;
import com.redefine.sunny.api.PARAM;
import com.redefine.welike.common.api.bean.TestResult;

public interface IAPI {

    @ACTION("discovery/taskv2/card")
    ITask<TestResult> getMission(
            @PARAM(value = "userId") String userId
            );

}

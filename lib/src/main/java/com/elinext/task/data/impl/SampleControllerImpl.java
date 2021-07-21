package com.elinext.task.data.impl;

import com.elinext.task.annotation.Inject;
import com.elinext.task.data.SampleController;
import com.elinext.task.data.SampleService;

public class SampleControllerImpl implements SampleController {
    private SampleService sampleService;

    public SampleService getSampleService() {
        return sampleService;
    }

    @Inject
    public SampleControllerImpl(SampleService sampleService) {
        this.sampleService = sampleService;
    }
}

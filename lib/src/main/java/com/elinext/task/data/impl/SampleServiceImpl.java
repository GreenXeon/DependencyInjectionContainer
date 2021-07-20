package com.elinext.task.data.impl;

import com.elinext.task.annotation.Inject;
import com.elinext.task.data.SampleDao;
import com.elinext.task.data.SampleService;

public class SampleServiceImpl implements SampleService {
    private SampleDao sampleDao;

    @Inject
    public SampleServiceImpl(SampleDao sampleDao) {
        this.sampleDao = sampleDao;
    }
}

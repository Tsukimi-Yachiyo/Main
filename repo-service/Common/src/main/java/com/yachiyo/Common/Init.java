package com.yachiyo.Common;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Init {

    @Autowired
    DateScheduledConfigTask dateScheduledConfigTask;

    @PostConstruct
    public void init() {
        dateScheduledConfigTask.dateScheduledConfigTask();
    }
}

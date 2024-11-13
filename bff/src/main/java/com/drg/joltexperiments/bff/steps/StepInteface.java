package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.ServiceConfigEntity;
import com.drg.joltexperiments.bff.Step;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.util.Map;
import java.util.Optional;

public interface StepInteface {
    static final Logger logger = LoggerFactory.getLogger(StepInteface.class);
    String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults, final ServiceConfigEntity config);
}

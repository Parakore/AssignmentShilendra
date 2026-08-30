package com.muni.rcp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class JsonWorkflowProvider implements WorkflowProvider {

    private static final Logger log = LoggerFactory.getLogger(JsonWorkflowProvider.class);

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final String configPath;

    private WorkflowConfigModel rawConfig;

    public JsonWorkflowProvider(
            ResourceLoader resourceLoader,
            ObjectMapper objectMapper,
            @Value("${rcp.workflow.config-path:classpath:workflow-config.json}") String configPath) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        this.configPath = configPath;
    }

    @PostConstruct
    public synchronized void loadConfiguration() {
        try {
            log.info("Loading workflow configuration from: {}", configPath);
            Resource resource = resourceLoader.getResource(configPath);
            try (InputStream is = resource.getInputStream()) {
                this.rawConfig = objectMapper.readValue(is, WorkflowConfigModel.class);
            }
            log.info("Successfully loaded workflow configuration with {} transitions",
                    rawConfig.getTransitions() != null ? rawConfig.getTransitions().size() : 0);
        } catch (Exception e) {
            log.error("Failed to load workflow configuration from {}", configPath, e);
            throw new IllegalStateException("Could not load workflow configuration from " + configPath, e);
        }
    }

    @Override
    public List<String> getAllowedStates() {
        return rawConfig != null && rawConfig.getStates() != null
                ? rawConfig.getStates()
                : Collections.emptyList();
    }

    @Override
    public String getInitialState() {
        return rawConfig != null && rawConfig.getInitialState() != null
                ? rawConfig.getInitialState()
                : "APPLIED";
    }

    @Override
    public List<WorkflowConfigModel.TransitionConfig> getTransitions() {
        return rawConfig != null && rawConfig.getTransitions() != null
                ? rawConfig.getTransitions()
                : Collections.emptyList();
    }

    @Override
    public List<WorkflowConfigModel.TransitionConfig> getAvailableTransitions(String currentState, List<String> userRoles) {
        if (rawConfig == null || rawConfig.getTransitions() == null || currentState == null) {
            return Collections.emptyList();
        }
        List<WorkflowConfigModel.TransitionConfig> available = new ArrayList<>();
        for (WorkflowConfigModel.TransitionConfig t : rawConfig.getTransitions()) {
            if (t.getFromState().equalsIgnoreCase(currentState)) {
                if (userRoles == null || userRoles.isEmpty()) {
                    continue;
                }
                boolean roleMatches = t.getAllowedRoles().stream()
                        .anyMatch(reqRole -> userRoles.stream().anyMatch(uRole -> uRole.equalsIgnoreCase(reqRole)));
                if (roleMatches) {
                    available.add(t);
                }
            }
        }
        return available;
    }

    @Override
    public boolean isValidTransition(String currentState, String action, List<String> userRoles) {
        if (rawConfig == null || rawConfig.getTransitions() == null || currentState == null || action == null) {
            return false;
        }
        for (WorkflowConfigModel.TransitionConfig t : rawConfig.getTransitions()) {
            if (t.getFromState().equalsIgnoreCase(currentState) && t.getAction().equalsIgnoreCase(action)) {
                if (userRoles == null || userRoles.isEmpty()) {
                    return false;
                }
                return t.getAllowedRoles().stream()
                        .anyMatch(reqRole -> userRoles.stream().anyMatch(uRole -> uRole.equalsIgnoreCase(reqRole)));
            }
        }
        return false;
    }

    @Override
    public WorkflowConfigModel.TransitionConfig getTransition(String currentState, String action) {
        if (rawConfig == null || rawConfig.getTransitions() == null || currentState == null || action == null) {
            return null;
        }
        for (WorkflowConfigModel.TransitionConfig t : rawConfig.getTransitions()) {
            if (t.getFromState().equalsIgnoreCase(currentState) && t.getAction().equalsIgnoreCase(action)) {
                return t;
            }
        }
        return null;
    }
}

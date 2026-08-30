package com.muni.rcp.config;

import java.util.List;

public interface WorkflowProvider {

    List<String> getAllowedStates();

    String getInitialState();

    List<WorkflowConfigModel.TransitionConfig> getTransitions();

    List<WorkflowConfigModel.TransitionConfig> getAvailableTransitions(String currentState, List<String> userRoles);

    boolean isValidTransition(String currentState, String action, List<String> userRoles);

    WorkflowConfigModel.TransitionConfig getTransition(String currentState, String action);
}

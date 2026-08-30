package com.muni.rcp.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class WorkflowConfigModel {

    @JsonProperty("states")
    private List<String> states;

    @JsonProperty("initialState")
    private String initialState;

    @JsonProperty("transitions")
    private List<TransitionConfig> transitions;

    public List<String> getStates() {
        return states;
    }

    public void setStates(List<String> states) {
        this.states = states;
    }

    public String getInitialState() {
        return initialState;
    }

    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }

    public List<TransitionConfig> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<TransitionConfig> transitions) {
        this.transitions = transitions;
    }

    public static class TransitionConfig {
        @JsonProperty("action")
        private String action;

        @JsonProperty("fromState")
        private String fromState;

        @JsonProperty("toState")
        private String toState;

        @JsonProperty("allowedRoles")
        private List<String> allowedRoles;

        @JsonProperty("description")
        private String description;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getFromState() {
            return fromState;
        }

        public void setFromState(String fromState) {
            this.fromState = fromState;
        }

        public String getToState() {
            return toState;
        }

        public void setToState(String toState) {
            this.toState = toState;
        }

        public List<String> getAllowedRoles() {
            return allowedRoles;
        }

        public void setAllowedRoles(List<String> allowedRoles) {
            this.allowedRoles = allowedRoles;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}

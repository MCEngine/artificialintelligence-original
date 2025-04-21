package io.github.mcengine.api.artificialintelligence.functions.calling;

import java.util.List;

public class FunctionRule {
    public List<String> match;
    public String response;

    public FunctionRule(List<String> match, String response) {
        this.match = match;
        this.response = response;
    }
}

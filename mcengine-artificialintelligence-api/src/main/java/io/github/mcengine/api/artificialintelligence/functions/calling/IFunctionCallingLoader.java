package io.github.mcengine.api.artificialintelligence.functions.calling;

import io.github.mcengine.api.artificialintelligence.functions.calling.FunctionRule;
import java.util.List;

public interface IFunctionCallingLoader {
    List<FunctionRule> loadFunctionRules();
}
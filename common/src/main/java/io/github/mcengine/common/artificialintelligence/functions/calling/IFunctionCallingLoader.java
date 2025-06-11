package io.github.mcengine.common.artificialintelligence.functions.calling;

import io.github.mcengine.common.artificialintelligence.functions.calling.FunctionRule;
import java.util.List;

public interface IFunctionCallingLoader {
    List<FunctionRule> loadFunctionRules();
}
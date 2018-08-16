package jp.vmi.selenium.selenese;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.json.Json;

import jp.vmi.selenium.selenese.mutator.SeleneseRunnerMutator;

/**
 * Evaluator of script including "storedVars" variable.
 */
public class Eval {

    private final SeleneseRunnerMutator mutator = new SeleneseRunnerMutator();

    /**
     * Evaluate script including "storedVars" variable.
     *
     * @param context Selenese Runner context.
     * @param script JavaScript code.
     * @return result of evaluating script.
     */
    public Object eval(Context context, String script) {
        return eval(context, script, null);
    }

    /**
     * Evaluate script including "storedVars" variable.
     *
     * @param context Selenese Runner context.
     * @param script JavaScript code.
     * @param cast cast type.
     * @return result of evaluating script.
     */
    public Object eval(Context context, String script, String cast) {
        VarsMap varsMap = context.getVarsMap();
        boolean hasStoredVars = script.matches(".*\\bstoredVars\\b.*");
        StringBuilder writer = new StringBuilder();
        Json json = new Json();
        if (hasStoredVars) {
            writer.append("return (function(){var storedVars = ")
                .append(json.toJson(varsMap))
                .append(";\n");
        }
        writer.append("return [");
        if (cast != null)
            writer.append(cast);
        writer.append("((function(){");
        mutator.mutate(context, script, writer);
        writer.append("})())");
        if (hasStoredVars)
            writer.append(", storedVars];})();");
        else
            writer.append("];");
        Object result = context.executeScript(writer.toString());
        if (!(result instanceof List))
            throw new SeleneseRunnerRuntimeException(result.toString());
        List<?> list = (List<?>) result;
        switch (list.size()) {
        case 0:
            return null;
        case 1:
            return list.get(0);
        default: // case 2:
            @SuppressWarnings("unchecked")
            Map<String, Object> newVarsMap = (Map<String, Object>) list.get(1);
            varsMap.clear();
            varsMap.putAll(newVarsMap);
            return list.get(0);
        }
    }
}

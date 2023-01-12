package org.homs.houmls;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CmdArgumentsProcessor {

    public final String[] args;
    public final Map<String, String> modifiers = new LinkedHashMap<>();
    public final List<String> files = new ArrayList<>();

    public CmdArgumentsProcessor(String[] args) {
        this.args = args;
    }

    public void processArgs() {
        var p = Pattern.compile("--(.+?)=(.+?)");
        for (String arg : args) {
            if (arg.startsWith("--")) {
                var m = p.matcher(arg);
                if (m.matches()) {
                    String modifierName = m.group(1);
                    String modifierValue = m.group(2);
                    modifiers.put(modifierName, modifierValue);
                } else {
                    throw new RuntimeException("unexpected argument: " + arg);
                }
            } else {
                files.add(arg);
            }
        }
    }
}
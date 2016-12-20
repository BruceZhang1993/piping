package indi.shinado.piping.pipes.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * the value of key
 * e.g.
 * "tran.ins -ls"  -> ["tran", "ins", ["ls"]]
 * ".txt.play" -> [".txt.", "play", null]
 * "what the -> [null, "what the", null]
 * "what the" -> [null, "what the", null]
 */
public class Instruction {

    public String input;

    public String pre;

    public String body = "";

    public String[] params;

    /**
     * return formatted value from user input.
     * e.g.
     * "tran.ins -ls" -> ["tran", "ins", ["ls"]]
     * "maya.txt.play" -> ["maya.txt", "play", null]
     * "maya" -> [null, "maya", null]
     *
     * @param input user input
     * @return formatted value
     */
    public Instruction(String input) {
        this.input = input;

        String right = "";
        if (input.contains("\"")) {
            String split[] = input.split("\"");
            String left = "";
            int i = split.length - 1;
            for (; i >= 0; i--) {
                String s = split[i];
                if (i % 2 == 1) {
                    //ignore
                } else {
                    if (s.contains(Keys.PIPE)) {
                        int indexDot = s.lastIndexOf(Keys.PIPE);
                        right = s.substring(indexDot + 1);
                        left = s.substring(0, indexDot);
                        break;
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < i; j++) {
                sb.append(split[j]);
            }
            sb.append(left);

            this.pre = sb.toString();
        } else {
            int indexOfDot = input.lastIndexOf(Keys.PIPE);
            if (indexOfDot < 0) {
                right = input;
            } else {
                if (indexOfDot == 0) {
                    this.pre = null;
                } else {
                    this.pre = input.substring(0, indexOfDot);
                }
                right = input.substring(indexOfDot + 1, input.length());
            }
        }

        if (right.contains("\"")) {
            //not allowed, for now
        }else {
            String split[] = right.split(Keys.PARAMS);
            if (split.length > 0){
                boolean bodyFound = false;
                List<String> parameters = new ArrayList<>();
                for (int i=0; i<split.length; i++){
                    if (!split[i].isEmpty()){
                        if (!bodyFound){
                            body = split[i];
                            bodyFound = true;
                        }else {
                            parameters.add(split[i]);
                        }
                    }
                }

                this.params = parameters.toArray(new String[parameters.size()]);
            }
        }
    }

    public boolean isEmpty() {
        return isPreEmpty() && isParamsEmpty();
    }

    public boolean isPreEmpty() {
        return pre == null || pre.isEmpty();
    }

    public boolean isParamsEmpty() {
        return params == null || params.length == 0;
    }

    public boolean isBodyEmpty() {
        return body == null || body.isEmpty();
    }

    public boolean endsWith(String s) {
        return input.endsWith(s);
    }
}

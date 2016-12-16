package indi.shinado.piping.pipes.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * the value of key
 * e.g.
 * "tran.ins -ls"  -> ["tran", "ins", ["ls"]]
 * ".txt.play" -> [".txt.", "play", null]
 * "what the -> [null, "what the", null]
 * "what the" -> [null, "what the", null]
 *
 */
public class Instruction {

    public String input;

    public String pre;

    public String body;

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
        int indexOfDot = input.lastIndexOf(Keys.PIPE);
        String right;
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

        boolean start = false;
        boolean paramStart = false;
        List<String> params = new ArrayList<>();
        StringBuilder body = new StringBuilder();
        StringBuilder param = new StringBuilder();
        for (int i = 0; i < right.length(); i++) {
            char c = right.charAt(i);
            if (c == '\"') {
                start = !start;
                continue;
            }

            if (!start && (c + "").equals(Keys.PARAMS)) {
                paramStart = !paramStart;
                if (!paramStart){
                    params.add(param.toString());
                    param = new StringBuilder();
                }
                continue;
            }

            if (paramStart){
                param.append(c);
            }else {
                body.append(c);
            }
        }
        this.body = body.toString();
        this.params = (String[]) params.toArray();

//        String[] splitRight = right.split(Keys.PARAMS);
//        int splitLength = splitRight.length;
//        if (splitLength > 0) {
//            this.body = splitRight[0];
//            if (splitLength > 1) {
//                String[] params = new String[splitLength - 1];
//                for (int i = 1; i < splitRight.length; i++) {
//                    params[i - 1] = splitRight[i];
//                }
//                this.params = params;
//            } else {
//                this.params = null;
//            }
//        } else {
//            this.body = null;
//            this.params = null;
//        }
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

    public boolean endsWith(String s){
        return input.endsWith(s);
    }
}

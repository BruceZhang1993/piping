package indi.shinado.piping.pipes.entity;

/**
 * the value of key
 * e.g.
 * "tran.ins -ls"  -> ["tran", "ins", ["ls"]]
 * ".txt.play" -> [".txt.", "play", null]
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
     * @param input user input
     * @return formatted value
     */
    public Instruction(String input){
        this.input = input;
        int indexOfDot = input.lastIndexOf(Keys.PIPE);
        String right;
        if (indexOfDot < 0) {
            right = input;
        } else {
            if (indexOfDot == 0) {
                this.pre = null;
            } else {
                String left = input.substring(0, indexOfDot);
                this.pre = left;
            }
        }
        right = input.substring(indexOfDot + 1, input.length());
        String[] splitRight = right.split(Keys.PARAMS);
        int splitLength = splitRight.length;
        if (splitLength > 0) {
            this.body = splitRight[0];
            if (splitLength > 1) {
                String[] params = new String[splitLength - 1];
                for (int i = 1; i < splitRight.length; i++) {
                    params[i - 1] = splitRight[i];
                }
                this.params = params;
            } else {
                this.params = null;
            }
        } else {
            this.body = null;
            this.params = null;
        }
    }

    public boolean isEmpty(){
        return isPreEmpty() && isParamsEmpty();
    }

    public boolean isPreEmpty(){
        return pre == null || pre.isEmpty();
    }

    public boolean isParamsEmpty(){
        return params == null || params.length == 0;
    }

    public boolean isBodyEmpty(){
        return body == null || body.isEmpty();
    }

}

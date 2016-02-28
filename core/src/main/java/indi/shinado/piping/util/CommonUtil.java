package indi.shinado.piping.util;

/**
 * Created by Administrator on 2015/12/6.
 */
public class CommonUtil {

    /**
     * get a random number
     * @param size e.g. 3: {0,1,2}
     * @param offset e.g. 1:{1,2,3}, -1:{-1,0,1}
     * @return random + offset
     */
    public static int getRandom(int size, int offset){
        int random = ((int)System.currentTimeMillis() % size);
        random = Math.abs(random);
        random += offset;
        return random;
    }

}

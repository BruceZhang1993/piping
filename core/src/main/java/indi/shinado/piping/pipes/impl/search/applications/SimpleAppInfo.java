package indi.shinado.piping.pipes.impl.search.applications;

/**
 * Created by shinado on 16/5/15.
 */
public class SimpleAppInfo implements Comparable<SimpleAppInfo>{

    public long installTime;
    public String activityName;
    public String packageName;

    public SimpleAppInfo(long installTime, String activityName, String packageName) {
        this.installTime = installTime;
        this.activityName = activityName;
        this.packageName = packageName;
    }

    @Override
    public int compareTo(SimpleAppInfo simpleAppInfo) {
        return (int) (simpleAppInfo.installTime - installTime);
    }
}

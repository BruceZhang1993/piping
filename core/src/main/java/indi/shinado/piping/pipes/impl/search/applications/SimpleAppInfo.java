package indi.shinado.piping.pipes.impl.search.applications;

public class SimpleAppInfo implements Comparable<SimpleAppInfo>{

    public long installTime;
    public String url;
    public String activityName;
    public String packageName;

    public SimpleAppInfo(long installTime, String url, String activityName, String packageName) {
        this.installTime = installTime;
        this.url = url;
        this.activityName = activityName;
        this.packageName = packageName;
    }

    @Override
    public int compareTo(SimpleAppInfo simpleAppInfo) {
        return (int) (simpleAppInfo.installTime/60000 - installTime/60000);
    }
}

// 時間計測
package debug;

public class DebugLoadTime {

    private String text;
    private long time_start = 0;
    private long time_end = 0;

    public DebugLoadTime(String func) {
        text = func;
        time_start = System.currentTimeMillis();
    }

    public void End() {
        time_end = System.currentTimeMillis();
        long time = time_end - time_start;
        Debug.DebugLog(text + ": " + time + " (ms)");
    }
}

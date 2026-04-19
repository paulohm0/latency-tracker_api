package paulodev.latencytracker_api.enums;

public enum BottleneckCriticalityLevel {
    NORMAL,
    WARNING,
    HIGH,
    CRITICAL;

    public static BottleneckCriticalityLevel fromResponseTime(int responseTimeMs) {
        if (responseTimeMs >= 4000) return CRITICAL;
        if (responseTimeMs >= 2000) return HIGH;
        if (responseTimeMs >= 1000) return WARNING;
        return NORMAL;
    }
}

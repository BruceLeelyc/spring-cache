package com.lixl.sc.POJO;

public class CaffeineProperties {

    /**
     * 是否开启Caffeine缓存
     */
    private boolean enabled = true;
    /**
     * 访问后过期时间，单位毫秒
     */
    private long expireAfterAccess;

    /**
     * 写入后过期时间，单位毫秒
     */
    private long expireAfterWrite;

    /**
     * 写入后刷新时间，单位毫秒
     */
    private long refreshAfterWrite;

    /**
     * 初始化大小
     */
    private int initialCapacity;

    /**
     * 最大缓存对象个数，超过此数量时之前放入的缓存将失效
     */
    private long maximumSize;

    /**
     * 由于权重需要缓存对象来提供，对于使用spring cache这种场景不是很适合，所以暂不支持配置
     */
//		private long maximumWeight;

    /**
     * 缓存更新时通知其他节点的topic名称
     */
    private String topic = "cache:caffeine:topic";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public void setExpireAfterAccess(long expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
    }

    public long getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public void setExpireAfterWrite(long expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }

    public long getRefreshAfterWrite() {
        return refreshAfterWrite;
    }

    public void setRefreshAfterWrite(long refreshAfterWrite) {
        this.refreshAfterWrite = refreshAfterWrite;
    }

    public int getInitialCapacity() {
        return initialCapacity;
    }

    public void setInitialCapacity(int initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    public long getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(long maximumSize) {
        this.maximumSize = maximumSize;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}

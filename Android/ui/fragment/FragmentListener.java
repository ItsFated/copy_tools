package com.goowi.scale.assist;

/** Fragment的通用监听 */
public interface FragmentListener {
    /**
     * 事件回调
     * @param eventType 事件类型
     * @param events 事件源
     */
    void onFragmentEvent(byte eventType, Object... events);
}

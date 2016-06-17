package com.goowi.scale.assist;

/** Fragment的通用监听 */
public interface FragmentListener<T> {
    byte EVENT_CALLBACK = 0;
    byte EVENT_CLICK = 1;
    byte EVENT_LONG_CLICK = 2;
    byte EVENT_DOUBLE_CLICK = 3;

    /**
     * 事件回调
     * @param eventType 事件类型
     * @param events 事件源
     */
    void onFragmentEvent(byte eventType, T... events);
}

package com.yupi.project.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口状态枚举类 * @author Han
 * @data 2024/3/7
 * @apiNode
 */
public enum InterfaceState {
    ONLINE("上线", 1),
    OFFLINE("下线", 0);

    private final String text;

    private final int state;

    InterfaceState(String text, int state) {
        this.text = text;
        this.state = state;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.state).collect(Collectors.toList());
    }

    public String getText() {
        return text;
    }

    public int getState() {
        return state;
    }
}

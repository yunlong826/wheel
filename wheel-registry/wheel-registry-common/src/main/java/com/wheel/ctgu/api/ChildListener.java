package com.wheel.ctgu.api;

import java.util.List;

/**
 * @Author: jessin
 * @Date: 19-11-26 下午10:07
 */
public interface ChildListener {

    void childChanged(String path, List<String> children);

}

package org.lyg.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * 流程实例监听器 --开始
 * @author :lyg
 * @time :2018/7/29 0029
 */
public class StartListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) throws Exception {
        System.out.println("启动流程实例");
    }
}

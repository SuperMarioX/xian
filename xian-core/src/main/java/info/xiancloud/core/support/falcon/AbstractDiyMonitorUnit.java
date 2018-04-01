package info.xiancloud.core.support.falcon;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Bean;
import info.xiancloud.core.Group;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.Reflection;

import java.util.Collection;
import java.util.Map;

/**
 * 自定义业务监控
 *
 * @author happyyangyuan
 */
public abstract class AbstractDiyMonitorUnit implements Unit {

    @Override
    public Group getGroup() {
        return DiyMonitorGroup.singleton;
    }

    @Override
    public void execute(UnitRequest msg, NotifyHandler handler) {
        JSONArray responseMonitor = new JSONArray();

        String title = title();
        if (title == null || "".equals(title.trim()))
            handler.callback(UnitResponse.createSuccess(responseMonitor));//todo why success with empty array? not failure or exception?

        Object monitor = execute0();
        if (monitor != null) {
            if (monitor instanceof UnitResponse)
                padding(responseMonitor, ((UnitResponse) monitor).getData());
            else
                padding(responseMonitor, monitor);
        }

        handler.callback(UnitResponse.createSuccess(responseMonitor));
    }

    private void padding(JSONArray responseMonitor, Object monitor) {
        if (monitor == null)
            return;

        if (monitor instanceof Number) {
            JSONObject _monitor = new JSONObject();
            _monitor.put("title", title());
            _monitor.put("dashboard", dashboard());
            _monitor.put("value", monitor);

            responseMonitor.add(_monitor);
        } else if (monitor instanceof JSONObject || monitor instanceof Map || monitor instanceof Bean) {
            JSONObject _monitor = Reflection.toType(monitor, JSONObject.class);
            _monitor.put("title", title());
            _monitor.put("dashboard", dashboard());

            responseMonitor.add(_monitor);
        } else if (monitor instanceof Collection || monitor.getClass().isArray()) {
            JSONArray jsonArray = Reflection.toType(monitor, JSONArray.class);
            for (Object aJsonArray : jsonArray) {
                padding(responseMonitor, aJsonArray);
            }
        } else {
            LOG.warn("暂时不支持识别的监控数据: " + monitor);
        }
    }

    /**
     * dashboard
     */
    public String dashboard() {
        return null;
    }

    /**
     * title
     */
    public abstract String title();

    /**
     * @return value array/ json/ bean/ value anything.
     */
    public abstract Object execute0();

}

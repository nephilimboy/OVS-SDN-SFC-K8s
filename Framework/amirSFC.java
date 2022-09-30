package apps.amirSFC.src.main.java.org.onosproject.amirSFC;

import apps.amirSFC.src.main.java.org.onosproject.amirSFC.ControllerTask;
import org.onosproject.core.CoreService;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.host.HostService;
import org.onosproject.net.topology.TopologyService;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

@Component(immediate = true)
public class AmirFRERSeparatedStreamIdentification {
    private final Logger log = LoggerFactory.getLogger(getClass());


    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected HostService hostService;


    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected TopologyService topologyService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    private apps.amirSFC.src.main.java.org.onosproject.amirSFC.ControllerTask task = new apps.amirSFC.src.main.java.org.onosproject.amirSFC.ControllerTask();




    @Activate
    protected void activate() {

        log.info("Started");

        try {
//            Timer timer = new Timer();
//            task.setDelay(3);
            task.setExit(false);
            task.setLog(log);
//            task.setTimer(timer);
            task.setDeviceService(deviceService);
            task.setHostService(hostService);
            task.setTopologyService(topologyService);
            task.setFlowRuleService(flowRuleService);
            task.setAppId(coreService.registerApplication("org.onosproject.amirSFC"));
            task.run();
            task.schedule();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Deactivate
    protected void deactivate() {
        for (portStatsReaderTask task : this.getMap().values()) {
        task.setExit(true);
        task.getTimer().cancel();
        }
        log.info("Stopped");
    }

    public Map<String, apps.amirSFC.src.main.java.org.onosproject.amirSFC.ControllerTask> getMap() {
        return map;
    }

    public void setMap(Map<String, apps.amirSFC.src.main.java.org.onosproject.amirSFC.ControllerTask> map) {
        this.map = map;
    }

    private Map<String, apps.amirSFC.src.main.java.org.onosproject.amirSFC.ControllerTask> map = new HashMap<String, ControllerTask>();
}

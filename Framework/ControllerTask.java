package apps.amirSFC.src.main.java.org.onosproject.amirSFC;

import org.onlab.packet.Ethernet;
import org.onlab.packet.IPv4;
import org.onlab.packet.IpAddress;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.Host;
import org.onosproject.net.HostLocation;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.PortStatistics;
import org.onosproject.net.flow.*;
import org.onosproject.net.flow.instructions.Instructions;
import org.onosproject.net.host.HostService;
import org.onosproject.net.topology.TopologyService;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

import static org.onlab.packet.Ethernet.TYPE_ARP;


public class ControllerTask {



    public Host REFHOST;

    public Host getREFHOST() {
        return REFHOST;
    }

    public void setREFHOST(Host REFHOST) {
        this.REFHOST = REFHOST;
    }

    // Previous link cost
    private long pathToInitDstPreviousBitSent = 0;

    private long pathToSecondDstPreviousBitSent = 0;


    // Previous Host and its flows
    private HostLocation previousLocation = null;


    public long[] getLinkStatusDeltaValues() {
        return linkStatusDeltaValues;
    }

    public HostLocation getPreviousLocation() {
        return previousLocation;
    }

    public void setPreviousLocation(HostLocation previousLocation) {
        this.previousLocation = previousLocation;
    }

    public void run() {

        Host hostFirewall = null;
        Host hostIDSP1 = null;
        Host hostIDSP2 = null;
        Host hostWeb = null;



        // Can be static or dynamic
        for (Host container : getHostService().getHosts()) {
            // Servers
            log.info(container.toString());
            if(container.ipAddresses().toString().equals("[10.10.10.2]")){
                hostFirewall = container;
            }
            else if (container.ipAddresses().toString().equals("[10.10.10.3]")){
                hostIDSP1 = container;
            }
            else if(container.ipAddresses().toString().equals("[10.10.10.4]")){
                hostIDSP2 = container;
            }
            else if(container.ipAddresses().toString().equals("[10.10.10.10]")){
                hostWeb = container;
            }
        }


        TrafficSelector selectorSending2 = DefaultTrafficSelector.builder()
                .matchEthType(Ethernet.TYPE_IPV4)
//                .matchIPProtocol(IPv4.PROTOCOL_ICMP)
                .matchInPort(hostFirewall.location().port())
                .build();
        TrafficTreatment treatmentSending2 = DefaultTrafficTreatment.builder()
                .setOutput(hostIDSP1.location().port())
                .build();
        FlowRule flowRuleSending2 = DefaultFlowRule.builder()
                .forDevice(hostFirewall.location().deviceId())
                .fromApp(getAppId())
                .withSelector(selectorSending2)
                .withTreatment(treatmentSending2)
//                .withPriority(60010)
                .makePermanent()
                .build();
        FlowRuleOperations flowRuleOperationsSending2 = FlowRuleOperations.builder()
                .add(flowRuleSending2)
                .build();
        flowRuleService.apply(flowRuleOperationsSending2);




        PortNumber Vxlan = null;
        Vxlan.portNumber(4);

        TrafficSelector selectorSending3 = DefaultTrafficSelector.builder()
                .matchEthType(Ethernet.TYPE_IPV4)
                .matchInPort(hostIDSP2.location().port())
                .build();
        TrafficTreatment treatmentSending3 = DefaultTrafficTreatment.builder()
                .setOutput(Vxlan)
                .build();
        FlowRule flowRuleSending3 = DefaultFlowRule.builder()
                .forDevice(hostIDSP2.location().deviceId())
                .fromApp(getAppId())
                .withSelector(selectorSending3)
                .withTreatment(treatmentSending3)
//                .withPriority(60010)
                .makePermanent()
                .build();
        FlowRuleOperations flowRuleOperationsSending3 = FlowRuleOperations.builder()
                .add(flowRuleSending3)
                .build();
        flowRuleService.apply(flowRuleOperationsSending3);


        // From vxlan to the firewall
        TrafficSelector selectorSending4 = DefaultTrafficSelector.builder()
                .matchInPort(Vxlan)
                .build();
        TrafficTreatment treatmentSending4 = DefaultTrafficTreatment.builder()
                .setOutput(hostFirewall.location().port())
                .build();
        FlowRule flowRuleSending4 = DefaultFlowRule.builder()
                .forDevice(hostFirewall.location().deviceId())
                .fromApp(getAppId())
                .withSelector(selectorSending4)
                .withTreatment(treatmentSending4)
//                .withPriority(60010)
                .makePermanent()
                .build();
        FlowRuleOperations flowRuleOperationsSending4 = FlowRuleOperations.builder()
                .add(flowRuleSending4)
                .build();
        flowRuleService.apply(flowRuleOperationsSending4);



        //From vxlan to web
        PortNumber Vxlan2 = null;
        Vxlan2.portNumber(4);
        TrafficSelector selectorSending5 = DefaultTrafficSelector.builder()
                .matchInPort(Vxlan2)
                .build();
        TrafficTreatment treatmentSending5 = DefaultTrafficTreatment.builder()
                .setOutput(hostWeb.location().port())
                .build();
        FlowRule flowRuleSending5 = DefaultFlowRule.builder()
                .forDevice(hostWeb.location().deviceId())
                .fromApp(getAppId())
                .withSelector(selectorSending5)
                .withTreatment(treatmentSending5)
                .withPriority(60010)
                .makePermanent()
                .build();
        FlowRuleOperations flowRuleOperationsSending5 = FlowRuleOperations.builder()
                .add(flowRuleSending5)
                .build();
        flowRuleService.apply(flowRuleOperationsSending5);


        // from web to vxlan
        TrafficSelector selectorSending6 = DefaultTrafficSelector.builder()
                .matchEthType(Ethernet.TYPE_IPV4)
                .matchInPort(hostWeb.location().port())
                .build();
        TrafficTreatment treatmentSending6 = DefaultTrafficTreatment.builder()
                .setOutput(Vxlan2)
                .build();
        FlowRule flowRuleSending6 = DefaultFlowRule.builder()
                .forDevice(hostWeb.location().deviceId())
                .fromApp(getAppId())
                .withSelector(selectorSending6)
                .withTreatment(treatmentSending6)
                .withPriority(60010)
                .makePermanent()
                .build();
        FlowRuleOperations flowRuleOperationsSending6 = FlowRuleOperations.builder()
                .add(flowRuleSending6)
                .build();
        flowRuleService.apply(flowRuleOperationsSending6);

    }


    long[] linkStatusDeltaValues = new long[2];

    public void setLinkStatusDeltaValues(long[] linkStatusDeltaValues) {
        this.linkStatusDeltaValues = linkStatusDeltaValues;
    }

    private Set<FlowRule> previousHostFlowRules = new HashSet<>();

    void installRule(Host vh, HostLocation hostLocation, Host initialHostDestination, Host secondHostDestination) {
        log.info("  ############# ############# ############# ############# ############# ############# #############");
        log.info("  ############# ############# Setting new role ############# ############# #############");

        IpAddress secDestIp = null;
        IpAddress initDestIp = null;
        for (IpAddress ipSec : secondHostDestination.ipAddresses()) {
            secDestIp = ipSec;
            break;
        }
        for (IpAddress ipInit : initialHostDestination.ipAddresses()) {
            initDestIp = ipInit;
            break;
        }

        log.info("Installing rules.....");
        Set<Path> paths =
                getTopologyService().getPaths(getTopologyService().currentTopology(),
                        hostLocation.deviceId(),
                        secondHostDestination.location().deviceId());

        Path mainPath = paths.stream().findFirst().get();

        // First Rule (Transmission)
        TrafficSelector selectorSending = DefaultTrafficSelector.builder()
                .matchEthType(Ethernet.TYPE_IPV4)
                .matchInPort(hostLocation.port())
                .matchEthSrc(initialHostDestination.mac())
                .build();
        TrafficTreatment treatmentSending = DefaultTrafficTreatment.builder()
                .add(Instructions.modL2Dst(secondHostDestination.mac()))
//                .add(Instructions.modL3Dst(IpAddress.valueOf(SecondDestinationIP)))
                .add(Instructions.modL3Dst(secDestIp))
                .setOutput(mainPath.src().port())
                .build();
        FlowRule flowRuleSending = DefaultFlowRule.builder()
                .forDevice(hostLocation.deviceId())
                .fromApp(getAppId())
                .withSelector(selectorSending)
                .withTreatment(treatmentSending)
                .withPriority(60010)
                .makePermanent()
                .build();
        FlowRuleOperations flowRuleOperationsSending = FlowRuleOperations.builder()
                .add(flowRuleSending)
                .build();
        flowRuleService.apply(flowRuleOperationsSending);


        // Second Rule (Receiving)
        TrafficSelector selectorReceiving = DefaultTrafficSelector.builder()
                .matchEthType(Ethernet.TYPE_IPV4)
                .build();
        TrafficTreatment treatmentReceiving = DefaultTrafficTreatment.builder()
                .add(Instructions.modL2Src(initialHostDestination.mac()))
//                .add(Instructions.modL3Src(IpAddress.valueOf(InitialDestinationIP)))
                .add(Instructions.modL3Src(initDestIp))
                .setOutput(hostLocation.port())
                .build();
        FlowRule flowRuleReceiving = DefaultFlowRule.builder()
                .forDevice(hostLocation.deviceId())
                .fromApp(getAppId())
                .withSelector(selectorReceiving)
                .withTreatment(treatmentReceiving)
                .withPriority(60009)
                .makePermanent()
                .build();
        FlowRuleOperations flowRuleOperationsReceiving = FlowRuleOperations.builder()
                .add(flowRuleReceiving)
                .build();
        flowRuleService.apply(flowRuleOperationsReceiving);


        previousHostFlowRules.add(flowRuleSending);
        previousHostFlowRules.add(flowRuleReceiving);
    }


    private long calculateBitrate(long numberOfBit) {
        if (linkStatusDeltaValues.length == 0) {

        }
        return 0L;
    }

    private FlowRuleService flowRuleService;

    public FlowRuleService getFlowRuleService() {
        return flowRuleService;
    }

    public void setFlowRuleService(FlowRuleService flowRuleService) {
        this.flowRuleService = flowRuleService;
    }

    private Iterable<FlowEntry> flowEntries;

    public Iterable<FlowEntry> getFlowEntries() {
        return flowEntries;
    }

    public void setFlowEntries(Iterable<FlowEntry> flowEntries) {
        this.flowEntries = flowEntries;
    }

    private PortNumber portNumber;

    public PortNumber getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(PortNumber portNumber) {
        this.portNumber = portNumber;
    }

    public void schedule() {
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    private Timer timer = new Timer();

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    private Logger log;

    public boolean isExit() {
        return exit;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    private boolean exit;

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    private long delay;

    public PortStatistics getPortStats() {
        return portStats;
    }

    public void setPortStats(PortStatistics portStats) {
        this.portStats = portStats;
    }

    private PortStatistics portStats;

    public Long getPort() {
        return port;
    }

    public void setPort(Long port) {
        this.port = port;
    }

    private Long port;

    public DeviceService getDeviceService() {
        return deviceService;
    }

    public void setDeviceService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    protected DeviceService deviceService;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }


    protected HostService hostService;

    public void setHostService(HostService hostService) {
        this.hostService = hostService;
    }

    public HostService getHostService() {
        return hostService;
    }


    protected TopologyService topologyService;

    public void setTopologyService(TopologyService topologyService) {
        this.topologyService = topologyService;
    }

    public TopologyService getTopologyService() {
        return topologyService;
    }

    private ApplicationId appId;

    public ApplicationId getAppId() {
        return appId;
    }

    public void setAppId(ApplicationId appId) {
        this.appId = appId;
    }

    private Device device;
}



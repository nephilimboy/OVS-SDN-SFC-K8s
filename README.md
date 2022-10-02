# SDN-Based Service Function Chaining Framework for Kubernetes Cluster Using OvS

## Project Files
```
.
├── Environment Setup
│   ├── Adding_OvS_Bridge.sh
│   ├── CNIs_Implementation.sh
│   ├── k8s_Master.sh
│   └── k8s_Workers.sh
├── Framework
│   ├── amirSFC.java
│   ├── BUILD.bazel
│   └── ControllerTask.java

```

## Setup environment
- Deploy a Kubernetes cluster using ``k8s_Master.sh`` and ``k8s_Workers.sh``
- Add Flannel CNI to the cluster using ``CNIs_Implementation.sh``
- Add Multus CNI to the cluster using ``CNIs_Implementation.sh``
- Add OVS CNI to the cluster using ``CNIs_Implementation.sh``
- Add OvS bridge such ``br1`` in all cluster nodes
- Add ``NetworkAttachmentDefinition`` in ``Adding_OvS_Bridge.sh`` to all deployment Yaml files
- Run ONOS SDN controller on the Kubernetes master node
- Add ``Framework`` folder as a new application in the ONOS (change the code/IP addresses if needed)

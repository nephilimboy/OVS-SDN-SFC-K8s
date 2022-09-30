#!/usr/bin/env bash

######### Flannel ########################
#If you use custom podCIDR (not 10.244.0.0/16) you first need to download the above manifest and modify the network to match your one.
kubectl apply -f https://raw.githubusercontent.com/flannel-io/flannel/master/Documentation/kube-flannel.yml

######### multus ########################
git clone https://github.com/k8snetworkplumbingwg/multus-cni.git
cd multus-cni/
cat ./deployments/multus-daemonset.yml | kubectl apply -f -

######### OVS-CNI ########################
 wget https://raw.githubusercontent.com/k8snetworkplumbingwg/ovs-cni/main/examples/ovs-cni.yml
 kubectl apply -f ovs-cni.yml

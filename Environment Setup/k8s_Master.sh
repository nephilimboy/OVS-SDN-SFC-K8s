#!/usr/bin/env bash

sudo apt update
sudo ufw disable
sudo swapoff -a; sed -i '/swap/d' /etc/fstab

sudo cat >>/etc/sysctl.d/kubernetes.conf<<EOF
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
sudo sysctl --system

sudo apt install -y apt-transport-https ca-certificates curl gnupg-agent software-properties-common
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt update
sudo apt install -y docker-ce=5:19.03.10~3-0~ubuntu-focal containerd.io net-tools


sudo curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -
sudo echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" > /etc/apt/sources.list.d/kubernetes.list


#sudo apt update && apt install -y kubeadm=1.19.0-00 kubelet=1.19.0-00 kubectl=1.19.0-00
sudo apt update && apt install -y kubeadm=1.20.0-00 kubelet=1.20.0-00 kubectl=1.20.0-00

apt-get install -y openvswitch-switch



kubeadm init  --pod-network-cidr=192.168.0.0/16  --ignore-preflight-errors=all


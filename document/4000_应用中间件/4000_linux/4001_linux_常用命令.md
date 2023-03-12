---
title: linux常用命令
sort: 4001
date: 2019-01-01 00:00:00
category:
    - '应用中间件'
tags: 
    - 'linux'
comments: true
---
## 常用命令
``` bash
echo
printf
>
>>
|
grep
sed
`命令`
```


## 文件操作命令
``` bash
# 搜索文件
find 路径 -name 文件名
find / -name api.service

# 查看文件详细信息
stat 文件名

# 文件权限：chmod
# u 表示该文件的拥有者，g 表示与该文件的拥有者属于同一个群体(group)者，o 表示其他以外的人，a 表示这三者皆是
# + 表示增加权限、- 表示取消权限、= 表示唯一设定权限。
# r 表示可读取，w 表示可写入，x 表示可执行，X 表示只有当该文件是个子目录或者该文件已经被设定过为可执行
chmod a+x
```

## linux service
``` bash
# 增加服务service
cd /usr/lib/systemd/system/

# 创建服务文件
touch api.service

# 配置内容
[Unit]
Description=api4icql
[Service]
ExecStart=/usr/bin/java -jar /icql.work/api/icql-build-api.jar
[Install]
WantedBy=multi-user.target

# 启动服务
systemctl start api

# 如果报错 Failed to start api.service: Unit is masked，则执行 systemctl unmask api

# 刷新服务配置
systemctl daemon-reload

# 开机自启
systemctl enable api
```


## linux常用设置
``` bash
# 1、虚拟内存

# 创建虚拟内存文件
dd if=/dev/zero of=/data/swap bs=1024 count=1024000
mkswap /data/swap
/sbin/swapon /data/swap
# 设置开机自动挂载
vi /etc/fstab
# 最后一行加上 /data/swap swap swap default 0 0
# 设置虚拟内存使用频率
/etc/sysctl.conf 
# 最后一行加上 vm.swappiness=90
# 删除虚拟内存
swapoff /data/swap
rm -rf /data/swap


# 2、防火墙

# 启动防火墙
systemctl start firewalld
# 关闭防火墙
systemctl stop firewalld
# 关闭开机启动
systemctl disable firewalld
# 开启开机启动
systemctl enable firewalld
# 开启端口
firewall-cmd --zone=public --add-port=80/tcp --permanent
# 关闭端口
firewall-cmd --zone=public --remove-port=80/tcp --permanent
# 重新载入
firewall-cmd --reload
# 查看已开放的端口
firewall-cmd --list-ports
# 查看单独端口
firewall-cmd --zone=public --query-port=80/tcp


# 3、设置vi/vim行号

# vi基本操作，vi 文件，i进入编辑，esc退出编辑模式，shift + q进入命令模式，wq保存并退出
# 在配置文件 /etc/virc（/etc/vimrc）的首行加上 set number


# 4、安装wget，切换yum阿里源

yum install wget
wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
yum makecache


# 5、安装zip

yum install -y unzip zip


# 6、Xshell报"The remote SSH server rejected X11 forwarding request"错误解决方法

yum install xorg-x11-font* xorg-x11-xauth
touch /root/.Xauthority
vi /etc/ssh/sshd_config
# 修改X11Forwarding参数为yes（要把前面的#注释删除）
# 再将UseLogin参数默认为no，可能这一行最开始是被注释的，去掉注释并保存退出
service sshd restart
```


## 虚拟机环境安装（vmware）
``` bash
# centos安装：最小安装 + 基本开发环境
# 虚拟机设置独立IP
# 编辑 - 虚拟网络编辑器 - 选中VMnet8 - VMnet信息（选中NAT模式，只勾选将主机虚拟适配器连接到此网络，子网IP:设置虚拟机的ip段如172.16.10.0，子网掩码：255.255.255.0，打开 NAT设置 ，设置网关IP 172.16.10.2） - 打开宿主机:控制面板\网络和 Internet\网络连接 - VMware Network Adapter VMnet8属性 - ip4属性 - 配置为自动获取
# 进入虚拟机，控制台输入 ifconfig，查看ip信息，第一行类似ens33字样，打开 /etc/sysconfig/network-scripts/ifcfg-ens33 文件，设置以下几项：bootproto ip获取协议，dhcp改为static，ipaddr设置虚拟机的独立ip，netmask为子网掩码，gateway为网关，dns1和dns2设置，onboot设置为yes

vi /etc/sysconfig/network-scripts/ifcfg-ens33

BOOTPROTO=static
IPADDR=172.16.10.3
NETMASK=255.255.255.0
GATEWAY=172.16.10.2
DNS1=8.8.8.8
DNS2=8.8.4.4
ONBOOT=yes

# 虚拟机控制台输入 systemctl restart network.service 重启网络服务
# 在宿主机 ping 虚拟机的ip，虚拟机 ping www.baidu.com 等，如果正常，则成功
```
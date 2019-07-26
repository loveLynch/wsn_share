<center><font face="黑体" size=6>ZYNQ内核移植开发流程</font></center>
# 一、环境配置
## 1.平台与资源
- 操作系统：SUSE Linux Enterprise Server 11 (x86_64)
- Vivado + SDK:2018.2
- gcc:6.1.0
- linux-xlnx github地址：[linux-xlnx](https://github.com/Xilinx/linux-xlnx)
- u-boot-xlnx github地址：[u-boot-xlnx](https://github.com/Xilinx/u-boot-xlnx)
## 2.环境变量配置
> ![环境变量配置](https://upload-images.jianshu.io/upload_images/17429180-23d05b65373179bc.PNG?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

# 二、源码编译
## 1.u-boot编译
u-boot-master源码工程下：
- 执行如下命令：

```
make distclean
make zynq_xxx_config
make
```
## 2.linux内核编译
linux-xlnx-master源码工程下
- 执行如下命令：

```
make ARCH=arm (CROSS_COMPILE=arm-linux-gnueabihf-) distclean 
make ARCH=arm (CROSS_COMPILE=arm-linux-gnueabihf-) zynq_xxx_config
make ARCH=arm CROSS_COMPILE=arm-linux-gnueabihf- uImage LOADADDR=0x00008000 
```

- 注：将u-boot的工具加入环境变量
> 生成的内核文件所在位置为linux-xlnx-master/arch/arm/boot

## 3.文件系统制作
> 在[www.wiki.xilinx.com/Build+and+Modify+a+Rootfs](http://www.wiki.xilinx.com/Build+and+Modify+a+Rootfs)上可以直接下载
zynq板制作好的文件系统，并使用如下步骤对文件系统做相应的修改:
下载的文件为：arm_ramdisk.image.gz
使用mkimage制作用于u-boot引导启动的文件系统镜像

```
mkimage -A arm -T ramdisk -C gzip -d arm_ramdisk.image.gz uramdisk.image.gz
```

## 4.创建devicetree.dtb
linux-xlnx-master源码工程下
- 执行如下命令：

```
./scripts/dtc/dtc -I dts -O dtb -o devicetree.dtb ./arch/arm/boot/dts/zynq-zc706.dts
```
执行完会在linux-xlnx-master文件夹下生存devicetree.dtb
- 注:直接执行会出现如下错误：

> **a. 错误信息**
$ ./scripts/dtc/dtc -I dts -O dtb -o devicetree.dtb ./arch/arm/boot/dts/zynq-zc706.dts
Error: ./arch/arm/boot/dts/zynq-zc706.dts:7.1-9 syntax error
FATAL ERROR: Unable to parse input tree
**b.原因**
根据提示，是zynq-zed.dts这个文件的15行出错。
将该行 #include “zynq-7000.dtsi”修改为 /include/ “zynq-7000.dtsi”


# 三、内核移植

## 1.JTAG启动
>将将u-boot.elf,uImage,devicetree.dtb和uramdisk.image.gz文件拷贝到工程路径的/hw
进入hw目录
- 在Xilinx软件中的XSCT Console终端上执行如下命令

```
connect arm hw
source ps7_init.tcl
ps7_init
dow -data devicetree.dtb 0x2a00000
dow -data uramdisk.image.gz 0x2000000
dow -data uImage 0x3000000
dow u-boot.elf
con
```

> 在超级终端上回车
在超级终端上运行 

```
bootm 0x30000000x2000000 0x2a00000
```




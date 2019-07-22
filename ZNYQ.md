ZYNQ内核移植开发流程
# 一、环境配置
## 1.平台与资源
- 操作系统：SUSE Linux Enterprise Server 11 (x86_64)
- Vivado + SDK:2018.2
- gcc:6.1.0
- linux-xlnx github地址：https://github.com/Xilinx/linux-xlnx
- u-boot-xlnx github地址：https://github.com/Xilinx/u-boot-xlnx
## 2.环境变量配置
> ![环境变量配置](https://upload-images.jianshu.io/upload_images/17429180-23d05b65373179bc.PNG?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

# 二、源码编译
## 1.u-boot编译
u-boot-master源码工程下：
- 执行如下命令：
'''
make distclean
make xilinx_zynqmp_zcu102_rev1_0_deconfig
make
'''

## 2.linux内核编译
linux-xlnx-master源码工程下
- 执行如下命令：
'''
make ARCH=arm CROSS_COMPILE=arm-linux-gnueabihf- distclean 
make ARCH=arm CROSS_COMPILE=arm-linux-gnueabihf- xilinx_zynqmp_zcu102_rev1_0_deconfig
make ARCH=arm CROSS_COMPILE=arm-linux-gnueabihf- uImage LOADADDR=0x00008000 
'''
- 注：将u-boot的工具加入环境变量
> 生成的内核文件所在位置为linux-xlnx-master/arch/arm/boot

## 3.文件系统制作
> 在http://www.wiki.xilinx.com/Build+and+Modify+a+Rootfs上可以直接下载
zynq板制作好的文件系统，并使用如下步骤对文件系统做相应的修改:
下载的文件为：arm_ramdisk.image.gz
使用mkimage制作用于u-boot引导启动的文件系统镜像
'''
mkimage -A arm -T ramdisk -C gzip -d arm_ramdisk.image.gz uramdisk.image.gz
'''

## 4.创建devicetree.dtb
linux-xlnx-master源码工程下
- 执行如下命令：
'''
./scripts/dtc/dtc -I dts -O dtb -o devicetree.dtb ./arch/arm/boot/dts/zynq-zc706.dts
'''
执行完会在linux-xlnx-master文件夹下生存devicetree.dtb
- 注:直接执行会出现如下错误：
a.错误信息
$ ./scripts/dtc/dtc -I dts -O dtb -o devicetree.dtb ./arch/arm/boot/dts/zynq-zc706.dts
Error: ./arch/arm/boot/dts/zynq-zc706.dts:7.1-9 syntax error
FATAL ERROR: Unable to parse input tree
b.原因
根据提示，是zynq-zed.dts这个文件的15行出错。
将该行 #include “zynq-7000.dtsi”修改为 /include/ “zynq-7000.dtsi”


# 三、内核移植
# <center><font face="黑体" size=4>移植过程问题总结与内核启动失败原因</font></center>   
 <center><font face="黑体" size=2>廖银华   2019年8月5日</font></center>
### 1 移植过程的问题
###### 1.1 u-boot编译前会执行make xilinx_zynqmp_zcu111A_rev_defconfig进行配置，生成.config配置文件用于编译，未在配置文件中定义CONFIG_BOOTDELAY的值，为何.config文件中将此变量的值设为2，依据是什么？如果要修改CONFIG_BOOTDELAY该怎么修改？

> .config 文件将CONFIG_BOOTDELAY的值设置为2，是由于生成.config会读取KConfig的默认配置，其中BOOTDELAY的默认值为2，虽然xilinx_zynqmp_zcu111A_rev_defconfig中没有设置此参数，但默认参数中初始化此参数，因此生成.config文件时会添加。修改方法如下：
> 方法一：在配置文件 xilinx_zynqmp_zcu111A_rev_defconfig中添加CONFIG_BOOTDELAY，注意不要添加在文件尾部
> 方法二：u-boot工程文件的common文件夹下的Kconfig文件中186行修改BOOTDELAY的默认参数2
###### 1.2 linux内核编译不成功
> 原因：openssl版本太低，新安装openssl1.1.0c

###### 1.3 安装openssl问题，无法生成Makefile文件
>执行./config --prefix=/usr/local报如下错误：
>target already defined - linux-x86_64 (offending arg: linux-x86_64).
>解决方法：执行./config输入参数加上平台，即./config --prefix=/usr/local linux-x86_64
###### 1.4 编译linux内核生成Image文件，并不是uImage文件
>将linux-xlnx工程目录下/arch/arm64/boot的Image文件通过mkimage工具将u-boot制作可以识别的格式，生成的文件为uImage。
> - 注意：mkimage工具是编译u-boot过程中生成的工具，注意将mkimage的路径添加到环境变量中，其路径为u-boot工程目录下/tools

> 在linux-xlnx项目工程目下录执行
```
mkimage -A arm -O linux -T kernel -c gzip -a 0x8000 -e 0x8000 -d arch/arm64/boot/Image arch/arm64/boot/uImage
```
> mkimage 各个参数意义如下:
```
Usage: mkimage -l image
          -l ==> list image header information
       mkimage [-x] -A arch -O os -T type -C comp -a addr -e ep -n name -d data_file[:data_file...] image
          -A ==> set architecture to 'arch'  // 体系
          -O ==> set operating system to 'os' // 操作系统
          -T ==> set image type to 'type' // 镜像类型
          -C ==> set compression type 'comp' // 压缩类型
          -a ==> set load address to 'addr' (hex) // 加载地址
          -e ==> set entry point to 'ep' (hex) // 入口地址
          -n ==> set image name to 'name' // 镜像名称，注意不能超过32B
          -d ==> use image data from 'datafile' // 输入文件
          -x ==> set XIP (execute in place)
```

### 2 内核启动失败原因
##### 2.1 uboot启动相关的说明
###### 2.1.1 u-boot启动kernel
> 编译linux内核会生成Image或者压缩过的zImage，通过mkimage工具将u-boot制作可以识别的格式，生成的文件为uImage
uboot支持两种类型的uImage。
- Legacy-uImage 
在kernel镜像的基础上，加上64Byte的信息提供给uboot使用。
- FIT-uImage 
以类似FDT的方式，将kernel、fdt、ramdisk等等镜像打包到一个image file中，并且加上一些需要的信息（属性）。uboot只要获得了这个image file，就可以得到kernel、fdt

**本次编译使用的是Legacy-uImage 方式**

###### 2.1.2 bootm
> bootm这个命令用于启动一个操作系统映像。它会从映像文件的头部取得一些信息，这些信息包括：映像文件的基于的cpu架构、其操作系统类型、映像的类型、压缩方式、映像文件在内存中的加载地址、映像文件运行的入口地址、映像文件名等。 
紧接着bootm将映像加载到指定的地址，如果需要的话，还会解压映像并传递必要有参数给内核，最后跳到入口地址进入内核。
需要打开的宏
CONFIG_BOOTM_LINUX=y
CONFIG_CMD_BOOTM=y
注意，这个宏在自动生成的autoconf.mk中会自动配置，不需要额外配置
**加载kernel、ramdisk、fdt**
bootm Legacy-uImage的加载地址，ramdisk的加载地址，fdt的加载地址
- 操作参考如下：

![](https://github.com/loveLynch/wsn_share/blob/master/uboot.png?raw=true)


##### 2.2 内核启动失败原因
> 通过网上查阅资料，uboot启动失败愿意可能有如下几点，仅供参考。
**(1)uboot移植时一般只需要配置相应的宏即可**
**(2)kernel启动不成功，注意传参是否成功。传参不成功首先看uboot中bootargs设置是否正确，其次看uboot是否开启了相应宏以支持传参。**

###### 2.2.1 uboot能够正常启动，不过在kernel启动时却出现起不了的现象，停在这里
> 参考自https://blog.csdn.net/feixiang_song/article/details/17067059
> 分析原因：这个是由于时钟频率还未达到我们的要求，或者是时钟刚启动还未稳定造成的。
> 解决方法：就是在时钟初始化函数的末尾添加一个毫秒级延时即可。具体修改办法：
> 因为我的是s3c2416的板子
> 打开 /arch/arm/mach-s3c2416/clock.c  在文件的末尾
```     
 for (ptr = 0; ptr < ARRAY_SIZE(init_clocks_disable); ptr++, clkp++)

     {

                ret = s3c24xx_register_clock(clkp);
                if (ret < 0) {
                        printk(KERN_ERR "Failed to register clock %s (%d)\n",
                               clkp->name, ret);
                       }


               (clkp->enable)(clkp, 0);

               msleep(2);  //sfx add  

      }
```
######2.2.2 start kernel 之后没有任何输出与uboot无法将bootargs传入内核的调查方法与解决之道
>参考自https://blog.csdn.net/sy373466062/article/details/50363151
>***可能的原因如下几类：***
(1) 串口的引脚是否配置正确，例如pincltrl指定的Debug串口引脚，串口选错没有
(2) 串口的直连与交叉问题
(3) 查看bootargs传过去的console=是否正确，例如ttyXX是否正确，波特率
(4) 内核使用的command Line是uboot的还是内核的，还是dtb中chosen中的
***调查方法***
(1) 串口硬件问题的调查
可以使用示波器与逻辑分析仪来确认
(2) 串口软件配置问题的调查
可以使用在uboot查看log_buf的方法来查看问题出在哪里
当内核在start kernel之后，没有任何输出，等一会于用reset按键软reset，并停在uboot命令行中，使用下面方法来查看kenrel的log_buf,即printk存储log的地方。
在内核编译目录下面的System.map中找到__log_buf的位置，如：
> ![](https://img-blog.csdn.net/20151220094102840)
> 这个表示log_buf位于内核虚拟地址0x80dcdd84的位置，这个地方是直接映射段，因此对于imx6而言就是0x10dcdd84

##### 2.2.3 uboot无法引导uImage错误及其解决方法
> 参考自https://www.cnblogs.com/qiaoqiao2003/p/3821917.html
> uImage 其中有两个重要的地址，一个是加载地址（Load Address),另一个是入口地址（Entry Point)
> 加载地址是uboot在加载内核时的存放地址，入口地址是内核代码的开始执行地址。可以通过mkimage -l uImage查看
> 在使用前面的uboot加载uImage时，把uImage加载到加载地址（0x30008000）处，然后就在入口地址（0x30008000）处开始执行，而实际的代码执行地址是0x30008040（入口地址+文件头长度）。所以无法启动uImage.

> 可以直接将uImage中的加载地址修改为0x30008040就也能正常加载内核，只需要修改内核源码文件arch/arm/boot/Makefile即可：
```
删除一行：
$(obj)/uImage: STARTADDR=$(LOADADDR)
在删除行后面添加一行：
$(obj)/uImage: STARTADDR=$(shell echo $(LOADADDR) | sed -e "s/..$$/40/")
```# <center><font face="黑体" size=4>移植过程问题总结与内核启动失败原因</font></center>   
 <center><font face="黑体" size=2>廖银华   2019年8月5日</font></center>
### 1 移植过程的问题
###### 1.1 u-boot编译前会执行make xilinx_zynqmp_zcu111A_rev_defconfig进行配置，生成.config配置文件用于编译，未在配置文件中定义CONFIG_BOOTDELAY的值，为何.config文件中将此变量的值设为2，依据是什么？如果要修改CONFIG_BOOTDELAY该怎么修改？

> .config 文件将CONFIG_BOOTDELAY的值设置为2，是由于生成.config会读取KConfig的默认配置，其中BOOTDELAY的默认值为2，虽然xilinx_zynqmp_zcu111A_rev_defconfig中没有设置此参数，但默认参数中初始化此参数，因此生成.config文件时会添加。修改方法如下：
> 方法一：在配置文件 xilinx_zynqmp_zcu111A_rev_defconfig中添加CONFIG_BOOTDELAY，注意不要添加在文件尾部
> 方法二：u-boot工程文件的common文件夹下的Kconfig文件中186行修改BOOTDELAY的默认参数2
###### 1.2 linux内核编译不成功
> 原因：openssl版本太低，新安装openssl1.1.0c

###### 1.3 安装openssl问题，无法生成Makefile文件
>执行./config --prefix=/usr/local报如下错误：
>target already defined - linux-x86_64 (offending arg: linux-x86_64).
>解决方法：执行./config输入参数加上平台，即./config --prefix=/usr/local linux-x86_64
###### 1.4 编译linux内核生成Image文件，并不是uImage文件
>将linux-xlnx工程目录下/arch/arm64/boot的Image文件通过mkimage工具将u-boot制作可以识别的格式，生成的文件为uImage。
> - 注意：mkimage工具是编译u-boot过程中生成的工具，注意将mkimage的路径添加到环境变量中，其路径为u-boot工程目录下/tools

> 在linux-xlnx项目工程目下录执行
```
mkimage -A arm -O linux -T kernel -c gzip -a 0x8000 -e 0x8000 -d arch/arm64/boot/Image arch/arm64/boot/uImage
```
> mkimage 各个参数意义如下:
```
Usage: mkimage -l image
          -l ==> list image header information
       mkimage [-x] -A arch -O os -T type -C comp -a addr -e ep -n name -d data_file[:data_file...] image
          -A ==> set architecture to 'arch'  // 体系
          -O ==> set operating system to 'os' // 操作系统
          -T ==> set image type to 'type' // 镜像类型
          -C ==> set compression type 'comp' // 压缩类型
          -a ==> set load address to 'addr' (hex) // 加载地址
          -e ==> set entry point to 'ep' (hex) // 入口地址
          -n ==> set image name to 'name' // 镜像名称，注意不能超过32B
          -d ==> use image data from 'datafile' // 输入文件
          -x ==> set XIP (execute in place)
```

### 2 内核启动失败原因
##### 2.1 uboot启动相关的说明
###### 2.1.1 u-boot启动kernel
> 编译linux内核会生成Image或者压缩过的zImage，通过mkimage工具将u-boot制作可以识别的格式，生成的文件为uImage
uboot支持两种类型的uImage。
- Legacy-uImage 
在kernel镜像的基础上，加上64Byte的信息提供给uboot使用。
- FIT-uImage 
以类似FDT的方式，将kernel、fdt、ramdisk等等镜像打包到一个image file中，并且加上一些需要的信息（属性）。uboot只要获得了这个image file，就可以得到kernel、fdt

**本次编译使用的是Legacy-uImage 方式**

###### 2.1.2 bootm
> bootm这个命令用于启动一个操作系统映像。它会从映像文件的头部取得一些信息，这些信息包括：映像文件的基于的cpu架构、其操作系统类型、映像的类型、压缩方式、映像文件在内存中的加载地址、映像文件运行的入口地址、映像文件名等。 
紧接着bootm将映像加载到指定的地址，如果需要的话，还会解压映像并传递必要有参数给内核，最后跳到入口地址进入内核。
需要打开的宏
CONFIG_BOOTM_LINUX=y
CONFIG_CMD_BOOTM=y
注意，这个宏在自动生成的autoconf.mk中会自动配置，不需要额外配置
**加载kernel、ramdisk、fdt**
bootm Legacy-uImage的加载地址，ramdisk的加载地址，fdt的加载地址
- 操作参考如下：

![](https://github.com/loveLynch/wsn_share/blob/master/uboot.png?raw=true)


##### 2.2 内核启动失败原因
> 通过网上查阅资料，uboot启动失败愿意可能有如下几点，仅供参考。
**(1)uboot移植时一般只需要配置相应的宏即可**
**(2)kernel启动不成功，注意传参是否成功。传参不成功首先看uboot中bootargs设置是否正确，其次看uboot是否开启了相应宏以支持传参。**

###### 2.2.1 uboot能够正常启动，不过在kernel启动时却出现起不了的现象，停在这里
> 参考自https://blog.csdn.net/feixiang_song/article/details/17067059
> 分析原因：这个是由于时钟频率还未达到我们的要求，或者是时钟刚启动还未稳定造成的。
> 解决方法：就是在时钟初始化函数的末尾添加一个毫秒级延时即可。具体修改办法：
> 因为我的是s3c2416的板子
> 打开 /arch/arm/mach-s3c2416/clock.c  在文件的末尾
```     
 for (ptr = 0; ptr < ARRAY_SIZE(init_clocks_disable); ptr++, clkp++)

     {

                ret = s3c24xx_register_clock(clkp);
                if (ret < 0) {
                        printk(KERN_ERR "Failed to register clock %s (%d)\n",
                               clkp->name, ret);
                       }


               (clkp->enable)(clkp, 0);

               msleep(2);  //sfx add  

      }
```
######2.2.2 start kernel 之后没有任何输出与uboot无法将bootargs传入内核的调查方法与解决之道
>参考自https://blog.csdn.net/sy373466062/article/details/50363151
>***可能的原因如下几类：***
(1) 串口的引脚是否配置正确，例如pincltrl指定的Debug串口引脚，串口选错没有
(2) 串口的直连与交叉问题
(3) 查看bootargs传过去的console=是否正确，例如ttyXX是否正确，波特率
(4) 内核使用的command Line是uboot的还是内核的，还是dtb中chosen中的
***调查方法***
(1) 串口硬件问题的调查
可以使用示波器与逻辑分析仪来确认
(2) 串口软件配置问题的调查
可以使用在uboot查看log_buf的方法来查看问题出在哪里
当内核在start kernel之后，没有任何输出，等一会于用reset按键软reset，并停在uboot命令行中，使用下面方法来查看kenrel的log_buf,即printk存储log的地方。
在内核编译目录下面的System.map中找到__log_buf的位置，如：
> ![](https://img-blog.csdn.net/20151220094102840)
> 这个表示log_buf位于内核虚拟地址0x80dcdd84的位置，这个地方是直接映射段，因此对于imx6而言就是0x10dcdd84

##### 2.2.3 uboot无法引导uImage错误及其解决方法
> 参考自https://www.cnblogs.com/qiaoqiao2003/p/3821917.html
> uImage 其中有两个重要的地址，一个是加载地址（Load Address),另一个是入口地址（Entry Point)
> 加载地址是uboot在加载内核时的存放地址，入口地址是内核代码的开始执行地址。可以通过mkimage -l uImage查看
> 在使用前面的uboot加载uImage时，把uImage加载到加载地址（0x30008000）处，然后就在入口地址（0x30008000）处开始执行，而实际的代码执行地址是0x30008040（入口地址+文件头长度）。所以无法启动uImage.

> 可以直接将uImage中的加载地址修改为0x30008040就也能正常加载内核，只需要修改内核源码文件arch/arm/boot/Makefile即可：
```
删除一行：
$(obj)/uImage: STARTADDR=$(LOADADDR)
在删除行后面添加一行：
$(obj)/uImage: STARTADDR=$(shell echo $(LOADADDR) | sed -e "s/..$$/40/")
```
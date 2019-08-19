# <center><font face="黑体" size=5>u-boot配置与编译</font></center>
**编译过程主要有两步**
- make xxx_config 进行配置
- make 得到编译后目标文件

# 一、U-boot编译make配置过程

## 1. 概述
u-boot从 **v2014.10版本** 后引入kBuild系统，使得其Makefile变的更加复杂；整个Makefile中，嵌套其他不同用途的Makefile，本文将进行简单的分析.

## 2. 执行配置命令, 生成.config文件
> 配置，执行make XXX_defconfig进行各项配置，生成.config文件

```make配置
// 执行make，生成.config文件
asb@IoT:uboot$ make mx6qsensorgw_defconfig V=1
```

#### 2.1 编译生成fixdep工具

a.  %@表示所有的目标依赖的值
> 本例子中是mx6qsensorgw_defconfig

```分析配置文件：
%config: scripts_basic outputmakefile FORCE
	$(Q)$(MAKE) $(build)=scripts/kconfig $@
```

b. 经过上步转换得出：

```
mx6qsensorgw_defconfig: scripts_basic outputmakefile FORCE
	$(Q)$(MAKE) $(build)=scripts/kconfig mx6qsensorgw_defconfig

// 通过查找可得到build定义在文件中
scripts/Kbuild.include: ;
build := -f $(srctree)/scripts/Makefile.build obj

# 即：
mx6qsensorgw_defconfig: scripts_basic outputmakefile FORCE
	$(Q) make -f ./scripts/Makefile.build obj=scripts/kconfig mx6qsensorgw_defconfig
```

c. 得出三个依赖：

```
// scripts_basic依赖
# Basic helpers built in scripts/
PHONY += scripts_basic
scripts_basic:
	$(Q) make -f $(srctree)/scripts/Makefile.build obj=scripts/basic  # make没找到哪里来的，srctree为(./)当前目录
	$(Q) rm -f .tmp_quiet_recordmcount


// outputmakefile依赖
PHONY += outputmakefile
# outputmakefile generates a Makefile in the output directory, if using a
# separate output directory. This allows convenient use of make in the
# output directory.
# 如果执行make mx6qsensorgw_defconfig O=out，则所有生成目标都会放到out目录，此时会通过outputmakefile导出一个makefile到out目录进行编译
# 因在当前目录编译，则$(KBUILD_SRC)为空，不需要导出makefile文件，outputmakefile为空
outputmakefile:
ifneq ($(KBUILD_SRC),)
    $(Q)ln -fsn $(srctree) source
    $(Q)$(CONFIG_SHELL) $(srctree)/scripts/mkmakefile \
        $(srctree) $(objtree) $(VERSION) $(PATCHLEVEL)
endif


// FORCE依赖，目前为空
# 若一个目标添加FORCE依赖，每次编译都会去先去执行FORCE（实际上什么都不做），然后运行命令更新目标，从而确保目标每次都会被更新

```

d. mx6qsensorgw_defconfig的三个依赖scripts_basic outputmakefile FORCE，只有scripts_basic需要执行命令
```
scripts_basic:
    $(Q) make -f ./scripts/Makefile.build obj=scripts/basic # 执行Makefile.build文件，且文件中的obj替换为scripts/basic
    $(Q) rm -f .tmp_quiet_recordmcount

```

e. ./scripts/Makefile.build文件
```
# 赋值src
src := $(patsubst $(prefix)/%,%,$(obj)) # 即src=scripts/basic

# 搜寻子目录的下的Makefile，并包含进来
kbuild-dir := $(if $(filter /%,$(src)),$(src),$(srctree)/$(src))
kbuild-file := $(if $(wildcard $(kbuild-dir)/Kbuild),$(kbuild-dir)/Kbuild,$(kbuild-dir)/Makefile)
include $(kbuild-file)

# 展开后即
include ./scripts/basic/Makefile  # 定义编译在主机上执行的工具fixdep，它用于更新每个生成目标的依赖文件*.cmd
```

f. 编译生成fix总结为
```
  make -f ./scripts/Makefile.build obj=scripts/basic                                                                                                     
     cc -Wp,-MD,scripts/basic/.fixdep.d -Wall -Wstrict-prototypes -O2 -fomit-frame-pointer      -o scripts/basic/fixdep scripts/basic/fixdep.c
```

#### 2.2 编译生成conf工具

a. 依赖scripts_basic执行结束以后，继续执行顶层目录的命令
```
make -f ./scripts/Makefile.build obj=scripts/kconfig mx6qsensorgw_defconfig

```

b. ./scripts/Makefile.build文件
```
# 赋值src
src := $(patsubst $(prefix)/%,%,$(obj)) # 即src=scripts/kconfig

# 搜寻子目录的下的Makefile，并包含进来
kbuild-dir := $(if $(filter /%,$(src)),$(src),$(srctree)/$(src))
kbuild-file := $(if $(wildcard $(kbuild-dir)/Kbuild),$(kbuild-dir)/Kbuild,$(kbuild-dir)/Makefile)
include $(kbuild-file)

# 展开后即
include ./scripts/kconfig/Makefile  # 定义编译在主机上执行的工具fixdep，它用于更新每个生成目标的依赖文件*.cmd
```

c. ./scripts/kconfig/Makefile文件
```
# 匹配到如下规则
%_defconfig: $(obj)/conf
     $(Q)$< $(silent) --defconfig=arch/$(SRCARCH)/configs/$@ $(Kconfig)

# 展开为
mx6qsensorgw_defconfig: scripts/kconfig/conf
    $(Q)scripts/kconfig/conf  --defconfig=arch/../configs/mx6qsensorgw_defconfig Kconfig
```

d. 依赖文件scripts/kconfig/conf文件
```
# 生成conf文件
cc  -o scripts/kconfig/conf scripts/kconfig/conf.o scripts/kconfig/zconf.tab.o
```

e. 最后执行最后的目标命令，生成.conf文件
```
scripts/kconfig/conf  --defconfig=arch/../configs/mx6qsensorgw_defconfig Kconfig
 #
 # configuration written to .config
 #
```
**conf工具从根目录开始树状读取默认的Kconfig文件，分析其配置，并保存在内存中；然后读取指定的xxx_defconfig文件，更新得到最终的符号表，并输出到.config文件中**


# 二、uboot编译之编译过程

> Makefile核心是依赖和命令；对于每个目标，会先检查依赖，如果依赖存在则执行命令更新目标；如果依赖不存在，则会以依赖为目标，先生成依赖，依赖生成后，则执行命令

##  1. 
```
%.imx: %.bin
	$(Q)$(MAKE) $(build)=arch/arm/imx-common $@


u-boot.imx: u-boot.bin
    $(Q) make $(build)=arch/arm/imx-common u-boot.imx

u-boot.bin: u-boot-dtb.bin FORCE
	$(call if_changed,copy)

u-boot-dtb.bin: u-boot-nodtb.bin dts/dt.dtb FORCE
	$(call if_changed,cat)

u-boot-nodtb.bin: u-boot FORCE
	$(call if_changed,objcopy)
	$(call DO_STATIC_RELA,$<,$@,$(CONFIG_SYS_TEXT_BASE))
	$(BOARD_SIZE_CHECK)

dts/dt.dtb: checkdtc u-boot
	$(Q)$(MAKE) $(build)=dts dtbs

PHONY += dtbs
dtbs: dts/dt.dtb
	@:

u-boot:	$(u-boot-init) $(u-boot-main) u-boot.lds FORCE
	$(call if_changed,u-boot__)
ifeq ($(CONFIG_KALLSYMS),y)
	$(call cmd,smap)
	$(call cmd,u-boot__) common/system_map.o
endif

```

### $(u-boot-init)
```
u-boot-init := $(head-y)
head-y := arch/arm/cpu/$(CPU)/start.o
CONFIG_SYS_CPU="armv7"  
```

###  $(u-boot-main) 
```
u-boot-main := $(libs-y)
libs-y += lib/
libs-y += fs/
libs-y += net/
libs-y += disk/
libs-y += drivers/
libs-y += drivers/dma/
libs-y += drivers/gpio/
libs-y += drivers/i2c/
libs-y += drivers/mmc/
libs-y += drivers/mtd/
libs-y += drivers/mtd/onenand/
libs-y += drivers/mtd/spi/
libs-y += drivers/net/
libs-y += drivers/net/phy/
libs-y += drivers/pci/
libs-y += drivers/power/ \
	drivers/power/domain/ \
	drivers/power/fuel_gauge/ \
	drivers/power/mfd/ \
	drivers/power/pmic/ \
	drivers/power/battery/ \
	drivers/power/regulator/
libs-y += drivers/spi/
libs-y += drivers/serial/
libs-y += drivers/usb/dwc3/
libs-y += drivers/usb/common/
libs-y += drivers/usb/emul/
libs-y += drivers/usb/eth/
libs-y += drivers/usb/gadget/
libs-y += drivers/usb/gadget/udc/
libs-y += drivers/usb/host/
libs-y += drivers/usb/musb/
libs-y += drivers/usb/musb-new/
libs-y += drivers/usb/phy/
libs-y += drivers/usb/ulpi/
libs-y += cmd/
libs-y += common/
libs-y += test/
libs-y += test/dm/

libs-y += board/freescale/mx6qsensorgw

libs-y := $(sort $(libs-y))
libs-y		:= $(patsubst %/, %/built-in.o, $(libs-y))
xxx/xxx/built-in.o
```

### u-boot.lds 
> ARCH := arm; CPU := armv7; BOARD := mx6qsensorgw; VENDOR := freescale; SOC := mx6

```
u-boot.lds: $(LDSCRIPT) prepare FORCE
	$(call if_changed_dep,cpp_lds)

# 根据.config的配置，以及Makefile文件可以得出
LDSCRIPT := ./arch/arm/cpu/u-boot.lds


prepare: prepare0


prepare0: archprepare FORCE
	$(Q)$(MAKE) $(build)=.

archprepare: prepare1 scripts_basic

prepare1: prepare2 $(version_h) $(timestamp_h) include/config/auto.conf

prepare2: prepare3 outputmakefile

prepare3: include/config/uboot.release

outputmakefile:
ifneq ($(KBUILD_SRC),)
	$(Q)ln -fsn $(srctree) source
	$(Q)$(CONFIG_SHELL) $(srctree)/scripts/mkmakefile \
	    $(srctree) $(objtree) $(VERSION) $(PATCHLEVEL)
endif


$(version_h): include/config/uboot.release FORCE
	$(call filechk,version.h)

$(timestamp_h): $(srctree)/Makefile FORCE
	$(call filechk,timestamp.h)

include/config/uboot.release: include/config/auto.conf FORCE
	$(call filechk,uboot.release)

include/config/%.conf: $(KCONFIG_CONFIG) include/config/auto.conf.cmd


```

![u-boot.imx目标关系依赖图](https://github.com/zhaojh329/U-boot-1/raw/master/images/u-boot_makefile.JPG)



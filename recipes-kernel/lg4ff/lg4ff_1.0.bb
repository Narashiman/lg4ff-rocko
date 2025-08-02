SUMMARY = "Logitech G29 and G923 Racing Wheel Custom Driver (minimal working version)"
DESCRIPTION = "Minimal kernel module for Logitech force feedback racing wheels"
HOMEPAGE = "https://github.com/berarma/new-lg4ff"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

# Use a different source or create a minimal working version
SRC_URI = "https://github.com/berarma/new-lg4ff/archive/refs/tags/0.4.0.tar.gz \
           file://015-simple-fix.patch \
           "

SRC_URI[sha256sum] = "37b1007fd3413168ef9fade15532f7173b5b545b93a4d7423c74909fe641ebca"

S = "${WORKDIR}/new-lg4ff-0.4.0"

inherit module

# Ensure our custom driver loads and overrides default drivers
KERNEL_MODULE_AUTOLOAD += "lg4ff"
KERNEL_MODULE_PROBECONF += "lg4ff"
module_conf_hid-logitech-new = "blacklist hid-logitech"

# Make sure O points to the kernel build dir and KERNEL_SRC is right (Yocto provides both)
#EXTRA_OEMAKE = "KERNEL_SRC=${STAGING_KERNEL_DIR}"

do_configure() {
    # Override to avoid ``make clean`` using host kernel paths
    :
}

do_compile() {
    oe_runmake KERNEL_SRC=${STAGING_KERNEL_DIR} KDIR=${STAGING_KERNEL_DIR}
}

do_install() {
    install -d ${D}${nonarch_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/input/joystick
    install -d ${D}${nonarch_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/hid
    
    # Find and install any .ko files
    for ko_file in ${S}/*.ko; do
        if [ -f "$ko_file" ]; then
            module_name=$(basename "$ko_file")
            if [ "$module_name" = "lg4ff.ko" ]; then
                install -m 0644 "$ko_file" ${D}${nonarch_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/input/joystick/
            else
                install -m 0644 "$ko_file" ${D}${nonarch_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/hid/
            fi
        fi
    done
}

FILES_${PN} = "${nonarch_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/input/joystick/*.ko \
                ${nonarch_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/hid/*.ko \
                ${nonarch_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/input/joystick/ \
                ${nonarch_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/hid/ \
                ${nonarch_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/input/"



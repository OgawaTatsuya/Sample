APP_PROJECT_PATH := $(call my-dir)
APP_STL := stlport_static

APP_BUILD_SCRIPT := $(APP_PROJECT_PATH)/jni/Android.mk
#APP_BUILD_SCRIPT := Android.mk
STLPORT_FORCE_REBUILD := true

APP_OPTIM:=debug
# APP_OPTIM:=release

APP_CPPFLAGS += -DHAVE_CONFIG_H 

# stlport and exception can not use same build.
#APP_CPPFLAGS += -fexceptions -frtti

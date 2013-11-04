LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_PACKAGE_NAME := ControlCenter
LOCAL_CERTIFICATE := platform
include $(BUILD_PACKAGE)

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4

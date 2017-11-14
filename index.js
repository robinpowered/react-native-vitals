'use strict';

import {
  DeviceEventEmitter,
  NativeModules
} from 'react-native';

const {RNVitals} = NativeModules;

export default {
  ...RNVitals,
  addListener (callback) {
    return DeviceEventEmitter.addListener(
      MobileDeviceManager.LOW_MEMORY,
      callback
    );
  }
};

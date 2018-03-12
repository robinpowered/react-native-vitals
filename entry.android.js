'use strict';

import Vitals from './vitals';
import {DeviceEventEmitter} from 'react-native';

export default {
  ...Vitals,
  // Android overrides
  addLowMemoryListener(callback) {
    var wrappedCallback = function (memoryInfo) {
      if (memoryInfo.memoryLevel === Vitals.MEMORY_CRITICAL) {
        callback(memoryInfo);
      }
    };

    return DeviceEventEmitter.addListener(
      Vitals.LOW_MEMORY,
      wrappedCallback
    );
  }
};

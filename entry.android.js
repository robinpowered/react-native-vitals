'use strict';

import Vitals from './vitals';
import {DeviceEventEmitter} from 'react-native';

export default {
  ...Vitals,
  // Android overrides
  addLowMemoryListener(callback) {
    const wrappedCallback = memoryInfo => {
      if (memoryInfo.memoryLevel === Vitals.MEMORY_LEVEL.MEMORY_CRITICAL) {
        callback(memoryInfo);
      }
    };

    return DeviceEventEmitter.addListener(
      Vitals.LOW_MEMORY,
      wrappedCallback
    );
  }
};

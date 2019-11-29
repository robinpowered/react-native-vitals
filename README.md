# react-native-vitals

React Native package that provides memory/storage usage info of a device.

## Usage
```javascript
import Vitals from 'react-native-vitals';

Vitals.getMemory().then(memory => {
  var {
    appUsed,
    systemTotal,
    systemFree,
    systemUsed
  } = memory;
});

Vitals.getStorage().then(storage => {
  var {
    total,
    free,
    used
  } = storage;
});

Vitals.addLowMemoryListener(memory => {
  console.log('Low memory warning triggered');
  var {
    appUsed,
    systemTotal,
    systemFree,
    systemUsed
  } = memory;
})
```

## Getting started

`$ npm install react-native-vitals --save`

### Mostly automatic installation

`$ react-native link react-native-vitals`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-vitals` and add `RNVitals.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNVitals.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)

#### iOS (using [CocoaPods](https://cocoapods.org/))

Alternatively, you can use CocoaPods to manage your native dependencies, just add this inside your `Podfile`:
```
  pod 'react-native-vitals', :path => '../node_modules/react-native-vitals'
```

After adding this, you need to run `pod install` inside the `ios`-folder of your RN application.

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.robinpowered.react.vitals.RNVitalsPackage;` to the imports at the top of the file
  - Add `new RNVitalsPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-vitals'
  	project(':react-native-vitals').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-vitals/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-vitals')
  	```

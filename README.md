
# react-native-vitals

## Getting started

`$ npm install react-native-vitals --save`

### Mostly automatic installation

`$ react-native link react-native-vitals`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-vitals` and add `RNVitals.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNVitals.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNVitalsPackage;` to the imports at the top of the file
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

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNVitals.sln` in `node_modules/react-native-vitals/windows/RNVitals.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Vitals.RNVitals;` to the usings at the top of the file
  - Add `new RNVitalsPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNVitals from 'react-native-vitals';

// TODO: What to do with the module?
RNVitals;
```
  
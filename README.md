# react-native-calendarevents-android

Add events to user's calendar on Android.

## Getting started

### Manual install

1. `npm install react-native-calendarevents-android --save`
2. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `com.exilz.calendarevents.CalendarEventsPackage;` to the imports at the top of the file
  - Add `new CalendarEventsPackage()` to the list returned by the `getPackages()` method
3. Append the following lines to `android/settings.gradle`:
```
    include ':react-native-calendarevents-android'
    project(':react-native-calendarevents-android').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-calendarevents-android')
```
4. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
```
    compile project(':react-native-calendarevents-android')
```
5. Add permissions in your `AndroidManifest.xml`
```
    <uses-permission android:name="android.permission.READ_CALENDAR" />
    <uses-permission android:name="android.permission.WRITE_CALENDAR" />
```

## Usage

### Example

```
import AndroidCalendarEvents from 'react-native-calendarevents-android';

AndroidCalendarEvents.addEvent(
    {
        title: 'Event title',
        startDate: Date.now(),
        endDate: Date.now() + 3600 * 1000,
        description: 'Event description',
        location: 'Paris, France'
    },
    (success) => console.log(success),
    (error) => console.log(error)
);
```

### Methods

#### addEvent(options)

Open calendar event dialog

Supported options:

| Name  | Type     | Description |
| :---- | :------: | :--- |
| title | string   | Event title (required) |
| startDate | number   | Start date in milliseconds from epoch (required) |
| endDate | number   |  End date in milliseconds from epoch (optional) |
| description | string   | Description to be pre-filled (optional) |
| location | string   | Location to be pre-filled and looked for by Google (optional) |


![Android calendar react native events](http://i.imgur.com/whOTYAPl.png)
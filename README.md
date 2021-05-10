# Covid News App

Covid News App is an application that can display global statistical information about Covid and news related to the topic. This app is made with native Android using the Java language. 

The reason for making this project is as a portfolio and fulfillment of final assignments for mobile application development courses.

### App Download
You can download the pre-released version of this app on [this link](https://drive.google.com/file/d/1hD5Vw6RQUovqWBKvESaSTL6CbPYcDE_A/view?usp=sharing)

## Installation
Clone this repository and import into **Android Studio**
```bash
git clone git@github.com:FarhanFDjabari/CovidNewsApp.git
```

## Configuration
### Keystores:
Create `app/keystore.gradle` with the following info:
```gradle
ext.key_alias='...'
ext.key_password='...'
ext.store_password='...'
```
And place both keystores under `app/keystores/` directory:
- `playstore.keystore`
- `stage.keystore`

## Build variants
Use the Android Studio *Build Variants* button to choose between **production** and **staging** flavors combined with debug and release build types

**IMPORTANT** Ensure you have set the ANDROID_HOME environment variable.

    cd /path/to/android-basic-samples
    export ANDROID_HOME = /path/to/android/sdk
    ./gradlew build


## Generating signed APK
From Android Studio:
1. ***Build*** menu
2. ***Generate Signed APK...***
3. Fill in the keystore information *(you only need to do this once manually and then let Android Studio remember it)*

## Maintainers
This project is mantained by:
* [FarhanFDjabari](http://github.com/FarhanFDjabari)

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## Special Thanks
* To [Haerul Muttaqin](https://github.com/haerulmuttaqin)
for teaching layout tutorial on his [youtube channel](https://youtube.com/haerulmuttaqin)
* To [Kawal Corona](https://kawalcorona.com/)
for the corona statistic api
* To [KawalCovid19](https://kawalcovid19.id/) for the image assets
* To other developers on youtube for sharing their knowledge on Android development
* To my lecturer for teaching the basic theory of android development

## License

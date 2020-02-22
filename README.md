# androidAppIndoor

This App is an indoor version, it is used to detect shake and issue alert, we suggest the user put the phone installed indoor version app in a steady status when it starts detecting, ex: put phone horizontally.

If you want to receive a notification on your mobile phone when an indoor phone detects shake, please check other of our project [androidAppMobile](https://github.com/ncu106503522/androidAppMobile), you can click the "scan" button on the indoor App and QR code button on the mobile App to pair two apps.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

IDE which can run Android project :

* Android studio


### Installing
Download project from git :
```sh
git clone https://github.com/Earthquake-Warning-System/androidAppIndoor.git
```
Notice that this project uses Firebase to send a notification, so you need to build an account in Firebase and create a project for this, basically, you need to finish the following things.
* put google-services.json into project
* Fill out author key in local.properties
```
auth.key.dev="your FCM key here"
```

If you need a tutorial please check  [FCM setup](https://firebase.google.com/docs/android/setup)

Open project with IDE and click build  → make project, then you can run this project.
### Download from google play
If you want to install this App with Google play, please send your google play account name to us with E-mail: "ncumwnl337@gmail.com", we can add you to our test list, then you can download this App through those URL.
* [androidAppMobile](https://play.google.com/apps/internaltest/4701297342863693173)
* [androidAppIndoor](https://play.google.com/apps/internaltest/4700171472181250730)



## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details



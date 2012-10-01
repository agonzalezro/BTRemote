Bluetooth Android/Arduino remote control
========================================

Here you will find the source code for arduino at
[``ardu.ino``](https://github.com/agonzalezro/BTRemote/blob/master/ardu.ino)
and for the Android application (all the rest of the files). You can download the
compiled version at
[``bin/BTRemote.apk``](https://github.com/agonzalezro/BTRemote/blob/master/bin/BTRemote.apk?raw=true).

The title of the project says almost all about the project. The ideas is
control a RC car with 2 servos (one for direction and one for velocity) using
the BT communication of one Android device.

This project is part of the OpenSeasson projects at
[Paylogic](http://paylogic.nl). BTW, you should check the [Software
Arquitect](http://corporate.paylogic.nl/media/cms_page_media/59/Vacancy-Softwarearchitect_1.pdf)
or the [Software
Developer](http://corporate.paylogic.nl/media/cms_page_media/59/Vacancy-Softwaredeveloper_1.pdf)
vacances if you want to work on a really nice place! :)

Video
-----

You can see a small video trying the car at the [Paylogic](http://paylogic.nl)
HQ. It was a small corridor but I made my best :D

<iframe width="560" height="315" src="http://www.youtube.com/embed/4Az1ovyOVzI"
        frameborder="0" allowfullscreen></iframe>

TODO
----

- Refactor java code... a long time without develop Java :D
- Improve app performance, sometimes it freezes loading BT.
- Remove extra files added by eclipse but not needed to be tracked.

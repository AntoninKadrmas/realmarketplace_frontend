# realmarketplace_frontend google play link
https://play.google.com/store/apps/details?id=com.realmarketplace

## used Modules
- https://github.com/zetbaitsu/Compressor<br />
- https://github.com/square/picasso<br />
- https://square.github.io/retrofit/<br />
- https://mvnrepository.com/artifact/com.squareup.okhttp3<br />
- https://github.com/ongakuer/CircleIndicator<br />
- https://mvnrepository.com/artifact/org.jetbrains.kotlinx<br />
- https://mvnrepository.com/artifact/junit/junit/4.13.2<br />
## set up on your device
Clone realmarketplace_frontend repository on your device.<br />
Open the projec in andorid studio. <br />
There is a huge chance that you are using different version of gradle and verison of android libraries.<br />
Change all optionts to get your parameters satisfied.<br />
Most likely you have to change verion of android librarys so navigate your self into build.gradle (Project)<br />
plugins{<br />
...<br />
    id 'com.android.application' version 'your_version' apply false<br />
    id 'com.android.library' version 'your_version' apply false<br />
...<br />
}<br />
Try to build the project.

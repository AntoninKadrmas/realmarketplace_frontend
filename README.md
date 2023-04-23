# realmarketplace_frontend google play link
Screenshots from application<br /><br />
<img src="https://github.com/AntoninKadrmas/realmarketplace_resources/blob/master/new_screens/signal-2023-04-23-194215.png" width="200">
<img src="https://github.com/AntoninKadrmas/realmarketplace_resources/blob/master/new_screens/signal-2023-04-23-001603_002.png" width="200">
<img src="https://github.com/AntoninKadrmas/realmarketplace_resources/blob/master/new_screens/signal-2023-04-23-001603_003.png" width="200">
<img src="https://github.com/AntoninKadrmas/realmarketplace_resources/blob/master/new_screens/signal-2023-04-23-001603_004.png" width="200">
<br />
<a href="https://play.google.com/store/apps/details?id=com.realmarketplace"><img src="https://github.com/AntoninKadrmas/realmarketplace_resources/blob/master/logo_plakat/google-play-badge.png" width="300"/></a>
## used Modules
- https://github.com/zetbaitsu/Compressor<br />
- https://github.com/square/picasso<br />
- https://square.github.io/retrofit/<br />
- https://mvnrepository.com/artifact/com.squareup.okhttp3<br />
- https://github.com/ongakuer/CircleIndicator<br />
- https://mvnrepository.com/artifact/org.jetbrains.kotlinx<br />
- https://mvnrepository.com/artifact/junit/junit/4.13.2<br />
- https://github.com/MikeOrtiz/TouchImageView<br />
## set up on your device
Clone realmarketplace_frontend repository on your device.<br />
Open the projec in andorid studio. <br />
There is a huge chance that you are using different version of gradle and verison of android libraries.<br />
Change all optionts to get your parameters satisfied.<br />
Most likely you have to change verion of android librarys so navigate your self into build.gradle (Project)<br />
plugins{<br />
&emsp;...<br />
&emsp;    id 'com.android.application' version 'your_version' apply false<br />
&emsp;    id 'com.android.library' version 'your_version' apply false<br />
&emsp;...<br />
}<br />
Try to build the project.

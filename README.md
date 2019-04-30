# Gillis
<img align="right" src="https://vignette.wikia.nocookie.net/muppet/images/d/da/Gillis_Fraggle.png/revision/latest/scale-to-width-down/280?cb=20131112060803">

Gilis is a service that renews certificates.

The component is named after Gillis from the TV-show Fraggle Rock (http://muppet.wikia.com/wiki/Gillis_Fraggle).

 ## Setup
 
 In order to use this project you must set repositories in your `~/.gradle/init.gradle` file
 
     allprojects {
         ext.repos= {
             mavenCentral()
             jcenter()
         }
         repositories repos
         buildscript {
          repositories repos
         }
     }
 

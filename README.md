## About
- This apps displays images in a way that when the phone moves, other parts of the images can be seen
like as if the user is choosing what he sees with the motion of the phone
- In short, it makes the images look like they are in 3D
- This is done my zooming the image a bit to allow the other areas to be shown when the phone moves
- The phone's sensors are used to get the metricts on how much the view of the picture moves

## What I changed with this fork
- I replaced the use of the `TYPE_GRAVITY` sensor as the accelerometer with the `TYPE_ACCELEROMETER`
sensor since my Samsung doesn't have this gravity sensor.
- I re-coded some parts of the project as I like, did simplifications and added logs
- I'm exploring this code and Android's libraries to see how I can make the effect more smooth. Currently it's not as smooth as in the [LinkedIn video](https://www.linkedin.com/posts/victorlucachagas_jetpackcompose-android-compose-activity-7208845954207145984-jNCT?utm_source=share&utm_medium=member_desktop). Likely from the change from sensor `TYPE_GRAVITY` to `TYPE_ACCELEROMETER`
- I intent to allow loading images, altering the intensity of the movement and overrall add more
features

## Resources
- https://developer.android.com/develop/sensors-and-location/sensors/sensors_overview
- https://developer.android.com/reference/android/hardware/SensorManager
- https://developer.android.com/reference/android/hardware/SensorEvent
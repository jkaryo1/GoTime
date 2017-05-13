Team 9
GoTime
Jon Karyo jkaryo1
Ryan Dens rdesn1
Marshall Demaree mdemare3
Chris Cardoz ccardoz1

Application Purpose:
GoTime's purpose is to augment features of Google Calendar by providing location
based alarms that take into account both transportation and preparation time to
alert the user when they should be getting ready for and leaving for an event.

Features:
	1. Create, edit, and delete events with information such as location, time, 
	date, title, transportation method, preparation time, and notes abou the event.

	2. Appliction notifies the user when it is time to start getting ready for the
	event, which is calculated by summing the time of the event, the time it will 
	take to get there based via the specified transportation method from the user's
	current location, and the minutes they specified they want to prepare for the
	event. 

	3. Application notifies the user when it is time to leave for the event, based
	on the time of the event and the time it would take to travel there from the
	user's (updated) current location. 

	4. Application notifies the user when it is time for the event.
	5. Notifications have alarm sounds that can be chosen in the app. Notifications
	apear as an alert dialog, whether the app is open or running.
	6. Customize default values for preparation time, transportation method, and
	alarm type
	7. View all events in application

Changes:
	1. Changed font colors slighty to better match the primary purlple color
	2. Changed the color of the cancel button so it does not appear disabled.
	3. If the user cancels an event in the EventActivitywithout making any 
	changes to it, the alert dialog confirmin the action does not appear to 
	confirm action
	4. Fixed errors with location services not working with APIs 23+
	5. Pressing the back button in the EventActivity acts the same as pressing cancel
	6. Splash screen removed as it was not being used properly
	7. If no loction services are enabled, "location services disabled" appears
	where the time to next evet would usually. 
	8. Day of week is now included in the dividers between days as suggested 
	by Prof. Selinski
	9. Added functionality for notes in each event. 


Database Storage:
We decided to use SQLiteDatabase as opposed to Firebase. This decision was
reached after realizing that there was no need to store events in the cloud, as
they must merely be accessed by a single device. We store each Event in the
database by dissecting its components and storing strings, ints, and longs. We
utilize a database adapter to easily integrate the database with all views and
the background service, especially the list of all events on the main screen.

Google Places API:
This API allows us to retrieve placeIDs and addresses of locations based off of
user input to the location search field in the add/edit event activity. placeIDs
are unique strings associated with specific locations that allow us to easily
search for directions in the Google Directions API. The placeID and address both
get stored in the database for easy access.

Google Maps API:
The Google Maps API allows the user to see the location that they entered in the
map on the event screen.

Google Directions API:
The Google Directions API allows us to get the estimated travel time associated
with going from the user's current location to the location of the event.

LocationService:
LocationService is a service that runs in the background and continually updates
the device's location. This lets the AlarmManager know when to send alarms for
the user to get ready and leave based on their current location with respect to
the location of the event. It also tells the main activity what to set as the
header, as the main activity's header tells the user either the amount of time
until they should get ready, the amount of time until they should leave, or the
amount of time until the event starts. Also, since the service is constantly
updating on an interval, it will know when the most pressing event has passed
and will send a broadcast to the main activity to delete the event, which then
updates the array of events.

Alarms:
Alarms are set by the LocationService and stored using an alarm manager. The
sound of the alarm can be changed by updating the default alarm sound in the
settings activity.

Permissions:
GoTime requires full location permission (requires the user to turn on high-
accuracy mode) and needs permission to draw over apps in order to send alarms.

The logic behind when and how the user is asked for specific
permissions regarding location servicesis as follows:

	1. If all location services are turned off and gotime does not have 
	permission, then on start we ask the use to enable location for gotime,
	then we prompt them with an alert dialog to take them to settings to
	turn on locaiton services for everthing. 

	2. If location services are turned on, but gotime does not have 
	permission, we simply ask them to enable permissions for gotime.

	3. If location services are turned off, but gotime has permission to
	access location, only the dialog prompting the user to turn on location
	services will showu up.

	4. If either of these are dismissed, we do not ask the user for permissions
	again until the app has been started again, so as not to annoy the user.

	5. If either gotime does not have permission to access
	location or all location services are off, the text describing when the
	next even will be simply says that location services are disabled.


CREDIT:
The WorkaroundMapFragment.java class, as well as its basic implementation, was
modeled after Alok Nair's post on StackOverflow regarding enabling vertical map
movement within a scrollview.
http://stackoverflow.com/questions/30525066/how-to-set-google-map-fragment-inside-scroll-view

The location service is based off of Usman Kurd and Sufian's posts on
StackOverflow.
http://stackoverflow.com/questions/14478179/background-service-with-location-listener-in-android

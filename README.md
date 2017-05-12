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
1) Create and edit events that will alert the user when they need to get ready
for and leave for the event
2) Sync with events in Google Calendar to minimize user hassle
3) Customize default values for preparation time, transportation method, and
alarm type
4) View and search for all events in application

Design Changes:
1) Removed navigation drawer to simplify app
2) Removed in-app alert screen, as users will be notified about events through
external alarms
3) Combined Add Event and View Event activities into a single activity since
they are practically the same
4) Changed placement of buttons to improve user experience
5) Made application colors more consistent to better the UI
6) Added better navigation to settings activity
7) Changed Edittexts to be white with a purple border
8) Edittexts are highlighted when in focus
9) Form validation was added to the event and settings activities

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

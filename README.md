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




CREDIT:
The WorkaroundMapFragment.java class, as well as its basic implementation, was
modeled after Alok Nair's post on StackOverflow regarding enabling vertical map
movement within a scrollview.
http://stackoverflow.com/questions/30525066/how-to-set-google-map-fragment-inside-scroll-view

The location service is based off of Usman Kurd and Sufian's posts on
StackOverflow.
http://stackoverflow.com/questions/14478179/background-service-with-location-listener-in-android

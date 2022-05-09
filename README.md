# DropboxStoreRefreshIssue

This is a sample app with an easy-to-reproduce refresh issue found in the Store library. DbModels, Models,
Room, Retrofit files are directly taken from the official library sample app. The main purpose is to show that
after a few failing refresh attempts events are not delivered to the stream.
The application structure is very basic and UI is not prepared to display the actual list of data
(just informing that data has been successfully fetched).

## Describe the bug

When there is an active stream collecting events and we want to refresh data quickly a few times
with the network disabled we are causing the flow to break and stop delivering events.

I have investigated the problem a little bit in the code and my guess is that flow inside newDownstream
inside Multicaster class is being finished/completed/canceled unexpectedly after a few
StoreResponse.Error emissions, then new Multicaster via RefCountedResource create field is being
recreated, new channels are being created as well and that is why events are not delivered to the stream anymore.

## To Reproduce

1. Turn off plane mode/disable network connection.
2. Launch the app.
3. Click the refresh button couple of times.
4. Events are not delivered to the stream after a few refresh attempts.

### Another wat to reproduce:

1. Launch the app.
2. Make a couple of successful attempts with network-enabled connection by clicking on the refresh button.
3. Turn off plane mode/disable network connection.
4. Click the refresh button couple of times.
5. Events are not delivered to the stream after a few refresh attempts.

## Smartphone

Device: Pixel 6 (real device), also many AS emulators
OS: Android 12 (API 31)
Store Version: 4.0.5

## Additional context

Please take a look at the logcat for a better understanding.
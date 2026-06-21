-- This script prepares the Finder window of 'RevAger'
-- (Set window size, set background image, arrange icons etc.)
tell application "Finder"
	tell disk "RevAger"
		open
		set current view of container window to icon view
		set toolbar visible of container window to false
		set statusbar visible of container window to false
		set the bounds of container window to {200, 100, 800, 360}
		set theViewOptions to the icon view options of container window
		set arrangement of theViewOptions to not arranged
		set icon size of theViewOptions to 48
		set background picture of theViewOptions to file ".background:background.png"
		make new alias file at container window to POSIX file "/Applications" with properties {name:"Applications"}
		set position of item "RevAger" of container window to {310, 90}
		set position of item "Applications" of container window to {520, 150}
		update without registering applications
	end tell
end tell
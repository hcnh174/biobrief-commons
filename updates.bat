TITLE updates
echo Dependency updates > .temp/updates.txt
gradle -q dependencyUpdates >> .temp/updates.txt && ^
REM cd .temp && type updates.txt && cd ..

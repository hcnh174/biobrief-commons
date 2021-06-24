TITLE updates
echo Dependency updates > .temp/updates.txt
gradle -q dependencyUpdates >> .temp/updates.txt && ^
cd .temp && type updates.txt && cd ..

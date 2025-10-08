TITLE updates
echo Dependency updates > .temp/updates.txt
gradle -q dependencyUpdates --no-parallel >> .temp/updates.txt && ^
cd .temp && type updates.txt && cd ..

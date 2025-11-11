echo Dependencies for biobrief > .temp/dependencies.txt
gradle -q :biobrief:dependencies >> .temp/dependencies.txt && ^
cd .temp && type dependencies.txt && cd ..

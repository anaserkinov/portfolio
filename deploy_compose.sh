./gradlew wasmJsBrowserDistribution

(cd composeApp/build/dist/wasmJs && rm -f install.zip && zip -r install.zip productionExecutable)

scp composeApp/build/dist/wasmJs/install.zip root@188.245.102.242:/var/www/portfolio_compose/

ssh root@188.245.102.242 '
cd /var/www/ &&
find portfolio_compose/* ! -name "install.zip" -exec rm -rf {} + &&
cd portfolio_compose &&
unzip -o install.zip -d . &&
mv productionExecutable/* . &&
rm -rf productionExecutable &&
rm -rf install.zip'

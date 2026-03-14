./gradlew installDist

(cd server/build && rm -f install.zip && zip -r install.zip install)

scp server/build/install.zip root@188.245.102.242:apps/portfolio/

ssh root@188.245.102.242 'unzip -o /root/apps/portfolio/install.zip -d /root/apps/portfolio && rm /root/apps/portfolio/install.zip'
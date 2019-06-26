FROM openjdk:8u191-jre-alpine3.8
WORKDIR /app
COPY d-scheduler.war .
CMD echo "172.17.0.2      d-server-db\n172.17.0.4       d-server-scraper\n172.17.0.5       d-server-admin" >> /etc/hosts && java -jar d-scheduler.war
FROM openjdk:8u191-jre-alpine3.8
WORKDIR /app
COPY d-scheduler.war .
CMD echo "172.18.0.2      d-server-db" >> /etc/hosts \
    && echo "172.18.0.3      d-server-admin" >> /etc/hosts \
    && echo "172.18.0.5      d-server-scraper" >> /etc/hosts \
    && echo "172.18.0.7      d-server-mail" >> /etc/hosts \
    && java -jar d-scheduler.war
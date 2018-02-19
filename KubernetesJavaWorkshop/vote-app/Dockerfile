FROM websphere-liberty:beta
RUN installUtility install  --acceptLicense logstashCollector-1.0 
COPY server.xml /config/server.xml
COPY target/microservice-vote-1.0.0-SNAPSHOT.war /config/apps/vote.war

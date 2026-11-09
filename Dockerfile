# --- Compilar y empaquetar el WAR ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY src ./src
COPY web ./web
RUN mkdir -p web/WEB-INF/classes \
 && javac -encoding UTF-8 -cp "web/WEB-INF/lib/*" -d web/WEB-INF/classes $(find src/java -name "*.java") \
 && jar -cf ROOT.war -C web .

# --- Ejecutar en Tomcat 11 (Jakarta EE 11, requerido por Jersey 4) ---
FROM tomcat:11.0-jdk21-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/ROOT.war /usr/local/tomcat/webapps/ROOT.war

# Render indica el puerto en la variable PORT (por defecto 10000)
ENV PORT=10000
EXPOSE 10000
CMD ["sh", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT}\\\"/\" /usr/local/tomcat/conf/server.xml && exec catalina.sh run"]

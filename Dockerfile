# --- FASE 1: Build dell'applicazione con Maven ---
# Usiamo un'immagine Docker che contiene già Maven e Java 21 per compilare il nostro progetto.
# La chiamiamo "build" per poterci riferire ad essa dopo.
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Creiamo una cartella di lavoro all'interno del container.
WORKDIR /home/app

# Copiamo prima il pom.xml per sfruttare la cache di Docker.
# Se il pom.xml non cambia, Docker non scaricherà di nuovo le dipendenze.
COPY pom.xml .

# Scarichiamo tutte le dipendenze del progetto.
RUN mvn dependency:go-offline

# Copiamo il resto del codice sorgente.
COPY src ./src

# Eseguiamo il build con Maven per creare il file .jar.
# L'opzione -DskipTests salta i test, rendendo il build più veloce.
RUN mvn package -DskipTests


# --- FASE 2: Creazione dell'immagine finale di produzione ---
# Partiamo da un'immagine molto più leggera che contiene solo la Java Runtime (JRE),
# non tutto il JDK e Maven, che non servono per eseguire l'app.
FROM eclipse-temurin:21-jre-jammy

# Copiamo SOLO il file .jar che abbiamo creato nella fase di build.
# Questo è il segreto del multi-stage build: l'immagine finale conterrà solo il jar e la JRE.
COPY --from=build /home/app/target/*.jar /usr/local/lib/app.jar

# Esponiamo la porta 8080, quella su cui Spring Boot si avvia di default.
EXPOSE 8080

# Questo è il comando che verrà eseguito quando il container parte.
# Semplicemente, avvia la nostra applicazione.
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]
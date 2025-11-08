# --- FASE 1: Build dell'applicazione con Maven ---
# Usiamo un'immagine specifica di Maven con una JRE Temurin basata su Ubuntu Jammy.
# Questo garantisce coerenza tra l'ambiente di build e quello di esecuzione.
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build

# Impostiamo la directory di lavoro.
WORKDIR /app

# Copiamo il wrapper di Maven per poterlo utilizzare per scaricare le dipendenze.
# Questo è utile se vuoi usare la stessa versione di Maven definita nel progetto.
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Scarichiamo le dipendenze sfruttando la cache di Docker.
# In questo modo, le dipendenze vengono scaricate di nuovo solo se pom.xml o mvnw cambiano.
RUN ./mvnw dependency:go-offline

# Copiamo il resto del codice sorgente dell'applicazione.
COPY src ./src

# Compiliamo l'applicazione e creiamo il pacchetto.
# Usare il wrapper assicura che venga usata la versione di Maven specificata nel progetto.
RUN ./mvnw package -DskipTests


# --- FASE 2: Creazione dell'immagine finale di produzione ---
# Partiamo da un'immagine JRE minimale per ridurre le dimensioni e la superficie di attacco.
# Usare la stessa base (es. "jammy") garantisce la compatibilità delle librerie di sistema.
FROM eclipse-temurin:21-jre-jammy

# Creiamo un utente non-root per eseguire l'applicazione per motivi di sicurezza.
# Eseguire processi come root nei container è una pratica sconsigliata.
RUN useradd -m -s /bin/bash springuser

# Impostiamo la directory di lavoro.
WORKDIR /app

# Copiamo il file JAR dalla fase di build, rinominandolo per semplicità.
# L'utilizzo di `COPY --chown` imposta la proprietà del file al nostro utente non-root.
COPY --from=build --chown=springuser:springuser /app/target/*.jar app.jar

# Cambiamo l'utente che eseguirà l'applicazione.
USER springuser

# Esponiamo la porta su cui l'applicazione sarà in ascolto.
EXPOSE 8080

# Comando per avviare l'applicazione.
# Usare la forma exec (array di stringhe) è la pratica raccomandata.
ENTRYPOINT ["java", "-jar", "app.jar"]
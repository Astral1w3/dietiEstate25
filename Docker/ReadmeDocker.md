# Prerequisiti

Avere Docker Desktop installato e in esecuzione sul tuo computer.

# Istruzioni di Avvio

1. Carica l'Immagine Docker

Prima di tutto, assicurati di avere il file dieti-estates-1.0.tar sul tuo computer.

Apri un terminale (o Prompt dei comandi/PowerShell su Windows) nella stessa cartella:

docker load -i dieti-estates-1.0.tar

2. Avvia il Container dell'Applicazione

Una volta che l'immagine è stata caricata, esegui il comando:


docker run -d -p 8080:8080 \
  --name dieti-backend \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://dietiestates2025v3.postgres.database.azure.com:5432/postgres?sslmode=require" \
  -e SPRING_DATASOURCE_USERNAME="<your_db_user>" \
  -e SPRING_DATASOURCE_PASSWORD="<your_db_password>" \
  -e APPLICATION_SECURITY_JWT_SECRET_KEY="yourGeneratedBase64Key-oJb8V8Yzuw8bFpQ8g7F2aK3eP1cV6sD0lB7jN9hT5yE=" \
  -e SPRING_SERVLET_MULTIPART_LOCATION="/app/uploads" \
  dieti-estates:1.0

per eventuale debugging:

docker logs dieti-backend


!! alcune proprietà potrebbero comparire senza immagini perchè non sono presenti nel dockerfile

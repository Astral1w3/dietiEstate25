# DietiEstates2025 - Frontend

## Setup del Progetto
1. Installare Node.js (versione >=20.0.0)
2. Clonare il repository
3. Aprire il terminale nella cartella del progetto
4. Eseguire `npm install` per installare le dipendenze
5. Eseguire `npm start` per avviare l'applicazione in modalità sviluppo
6. Aggiungere un file .env contenente le variabili d'ambiente:
    REACT_APP_GEOAPIFY_API_KEY 
    REACT_APP_GOOGLE 
7. Aprire [http://localhost:3000](http://localhost:3000) per visualizzare l'app nel browser
8. Di base l'app e' collegata al backend su azure, per modificarlo andare su: 
    src/services/api.js
    rimuovere il commento //baseURL: 'http://localhost:8080/api'


## Script Disponibili
- `npm start`: Avvia l'app in modalità sviluppo
- `npm run build`: Crea la build di produzione

## Tecnologie Utilizzate
- React 19.1.0
- React Router DOM 7.5.0
- Axios
- Leaflet per le mappe
- Chart.js per i grafici
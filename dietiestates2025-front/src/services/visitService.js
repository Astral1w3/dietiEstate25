import api from './api';

/**
 * Recupera le date già prenotate per un dato immobile.
 * @param {number} propertyId L'ID dell'immobile.
 * @returns {Promise<Date[]>} Una promessa che si risolve con un array di oggetti Date.
 */
export const getBookedDates = async (propertyId) => {
    try {
        const response = await api.get(`/visits/property/${propertyId}/booked-dates`);
        return response.data.map(dateString => new Date(dateString));
    } catch (error) {
        console.error("Errore nel recupero delle date prenotate:", error);
        throw error;
    }
};

/**
 * Invia una richiesta per prenotare una visita.
 * @param {object} visitData Dati della visita { propertyId, visitDate }.
 * @returns {Promise<object>} Una promessa che si risolve con i dati della visita creata.
 */
export const bookVisit = async (visitData) => {
    try {
        const response = await api.post('/visits/book', visitData);
        return response.data;
    } catch (error) {
        console.error("Errore durante la prenotazione della visita:", error);
        throw error;
    }
};

/**
 * Recupera tutte le prenotazioni per l'utente loggato (agente/manager/admin).
 * Chiama l'endpoint protetto /visits/agent-bookings.
 * 
 * @returns {Promise<Array>} Una promessa che si risolve con un array di oggetti BookingDetailsDTO.
 */
export const getMyBookings = async () => {
    try {
        const response = await api.get('/visits/agent-bookings'); 
        
        return response.data;
    } catch (error){
        console.error("Errore nel recupero delle prenotazioni dell'agente:", error.response || error);
        throw error;
    }
};
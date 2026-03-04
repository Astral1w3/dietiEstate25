import api from './api';

/**
 * Crea una nuova offerta per una proprietà.
 * @param {object} offerData - Dati dell'offerta ({ propertyId, offerPrice }).
 * @returns {Promise<any>} - La risposta del server con i dati dell'offerta creata.
 */
export const createOffer = async (offerData) => {
    try {
        const response = await api.post('/offers', offerData); 
        return response.data;
    } catch (error) {
        console.error("Errore nell'invio dell'offerta:", error.response || error);
        throw error;
    }
};

/**
 * Recupera tutte le offerte ricevute per le proprietà dell'utente loggato (agente).
 * @returns {Promise<Array>} Una promessa che si risolve con un array di oggetti di offerta.
 */
export const getMyOffers = async () => {
    try {
        const response = await api.get('/offers/agent-offers'); 
        return response.data;
    } catch (error) {
        console.error("Errore nel recupero delle offerte:", error);
        throw error;
    }
};

/**
 * Accetta un'offerta pendente.
 * @param {number} offerId L'ID dell'offerta da accettare.
 * @returns {Promise<object>} L'offerta aggiornata.
 */
export const acceptOffer = async (offerId) => {
    try {
        const response = await api.post(`/offers/${offerId}/accept`);
        return response.data;
    } catch (error) {
        console.error("Errore nell'accettare l'offerta:", error);
        throw error;
    }
};

/**
 * Rifiuta un'offerta pendente.
 * @param {number} offerId L'ID dell'offerta da rifiutare.
 * @returns {Promise<object>} L'offerta aggiornata.
 */
export const declineOffer = async (offerId) => {
    try {
        const response = await api.post(`/offers/${offerId}/decline`);
        return response.data;
    } catch (error) {
        console.error("Errore nel rifiutare l'offerta:", error);
        throw error;
    }
};
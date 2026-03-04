import api from './api';

/**
 * Recupera una singola proprietà tramite il suo ID.
 * @param {number | string} propertyId - L'ID della proprietà.
 * @returns {Promise<object>}
 */
export const getPropertyById = async (propertyId) => {
    try {
        const response = await api.get(`/properties/${propertyId}`);
        return response.data;
    } catch (error) {
        console.error(`Errore nel recupero della proprietà con ID ${propertyId}:`, error);
        throw error;
    }
};


/**
 * Cerca le proprietà in base a una località, con supporto per la paginazione.
 * @param {string} location - La località da cercare.
 * @param {number} page - Il numero della pagina da recuperare (parte da 0).
 * @param {number} size - Il numero di elementi per pagina.
 * @returns {Promise<object>} - Restituisce l'intero oggetto Page dal backend.
 */
export const searchPropertiesByLocation = async (location, page = 0, size = 10) => {
    if (!location) {
        return {
            content: [],
            totalPages: 0,
            totalElements: 0,
            number: 0
        };
    }
    try {
        const response = await api.get('/properties/search', {
            params: {
                location: location,
                page: page,
                size: size
            }
        });
        
        return response.data;
    } catch (error) {
        console.error("Errore nel recupero delle proprietà:", error);
        throw error;
    }
};


/**
 * Crea una nuova proprietà, includendo il caricamento di immagini.
 * @param {object} propertyData - I dati JSON della proprietà.
 * @param {File[]} images - Un array di oggetti File da caricare.
 * @returns {Promise<object>}
 */
export const createProperty = async (propertyData, images) => {
    try {
        const formData = new FormData();

        formData.append('propertyData', JSON.stringify(propertyData));

        images.forEach(image => {
            formData.append('images', image);
        });

        const response = await api.post('/properties', formData);
        
        return response.data;
    } catch (error) {
        console.error("Errore nella creazione della proprietà:", error);
        throw error;
    }
};


/**
 * Traccia la visualizzazione di una proprietà.
 * Fa una chiamata POST per incrementare il contatore sul backend.
 * @param {string | number} propertyId - L'ID della proprietà da tracciare.
 */
export const trackPropertyView = async (propertyId) => {
    try {
        await api.post(`/properties/${propertyId}/increment-view`);
    } catch (error) {
        console.error('Failed to track property view:', error);
    }
};

/**
 * Aggiorna lo stato di una proprietà.
 * @param {string | number} propertyId - L'ID della proprietà.
 * @param {string} newState - Il nuovo stato da impostare (es. 'OCCUPIED', 'AVAILABLE').
 * @returns {Promise<object>}
 */
export const updatePropertyState = async (propertyId, newState) => {
    try {
        const response = await api.patch(`/properties/${propertyId}/state`, { state: newState });
        return response.data;
    } catch (error) {
        console.error(`Errore durante l'aggiornamento dello stato per la proprietà ${propertyId}:`, error);
        throw error;
    }
};
/**
 * Cancella una proprietà.
 * @param {string | number} propertyId - L'ID della proprietà da cancellare.
 * @returns {Promise<void>} - Non restituisce contenuto in caso di successo.
 */
export const deleteProperty = async (propertyId) => {
    try {
        await api.delete(`/properties/${propertyId}`);
    } catch (error) {
        console.error(`Errore durante la cancellazione della proprietà ${propertyId}:`, error);
        throw error;
    }
};
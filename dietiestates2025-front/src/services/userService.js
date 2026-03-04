import api from './api'
/**
 * Crea un nuovo utente nel sistema.
 * @param {object} userData - L'oggetto contenente i dati del nuovo utente.
 * @param {string} userData.username - Il nome utente per il nuovo account.
 * @param {string} userData.email - L'indirizzo email del nuovo utente.
 * @param {string} userData.password - La password per il nuovo account.
 * @param {string} userData.role - Il ruolo assegnato al nuovo utente (es. 'admin', 'user').
 * @param {string} userData.creatorEmail - L'email dell'utente che sta creando questo nuovo account.
 * @returns {Promise<object>} Una promessa che si risolve con i dati dell'utente creato.
 */
export const createUser = async (userData) => {
    try {
        const response = await api.post('/management/users', userData);
        return response.data;
    } catch (error) {
        console.error("Errore durante la creazione dell'utente:", error);
        throw error;
    }
};
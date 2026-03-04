// Esportiamo i ruoli per averli disponibili ovunque
export const ROLES = {
  USER: 'user',
  AGENT: 'agent',
  MANAGER: 'manager',
  ADMIN: 'admin',
};

// Mappa che associa un ruolo a un array di "chiavi di funzionalità"
export const PERMISSIONS = {
  [ROLES.USER]: ['viewDetails', 'changePassword', 'logout'],
  [ROLES.AGENT]: ['viewDetails', 'addProperty', 'viewDashboard', 'bookingsView', 'offersView', 'changePassword', 'logout'],
  [ROLES.MANAGER]: ['viewDetails', 'createAgent', 'changePassword', 'logout'],
  [ROLES.ADMIN]: ['viewDetails', 'changePassword', 'createManager', 'createAgent', 'logout'],
};

// Mappa che associa le chiavi di funzionalità a etichette leggibili per il menu
export const MENU_OPTIONS = {
    viewDetails: 'Personal Details',
    addProperty: 'Add Property',
    viewDashboard: 'Dashboard',
    createAgent: 'Create Agent',
    createManager: 'Create Manager',
    changePassword: 'Change Password',
    logout: 'Log Out',
    bookingsView: 'Bookings View',
    offersView: 'Offers View',
};
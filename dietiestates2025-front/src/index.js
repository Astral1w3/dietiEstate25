import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import { GoogleOAuthProvider } from '@react-oauth/google';
import './index.css'

import { AuthProvider } from './context/AuthContext'; 
 
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
   <React.StrictMode>
      <BrowserRouter>
         <AuthProvider>
            <GoogleOAuthProvider clientId={process.env.REACT_APP_GOOGLE}>
               <App />
            </GoogleOAuthProvider>
         </AuthProvider>
      </BrowserRouter>
   </React.StrictMode>
);
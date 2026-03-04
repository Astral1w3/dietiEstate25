import React, { createContext, useState, useContext, useEffect, useCallback } from 'react';
import api from '../services/api';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [user, setUser] = useState(null);

  const logout = useCallback(() => {
    localStorage.removeItem('token');
    setUser(null);
    setToken(null);
    window.location.href = '/';
  }, []);

  useEffect(() => {
    if (token) {
      try {
        const decodedUser = jwtDecode(token);

        if (decodedUser.exp * 1000 < Date.now()) {
            console.log("Token scaduto, logout in corso.");
            logout();
        } else {
            setUser(decodedUser);
        }

      } catch (error) {
        console.error("Token non valido trovato in memoria, logout in corso.", error);
        logout();
      }
    }
  }, [token, logout]);

  const login = async (email, password) => {
    try {
      const response = await api.post('/auth/login', {
        email: email,
        password: password
      });

      const { jwt } = response.data;
      handleSuccessfulAuth(jwt);

    } catch (error) {
      console.error("Errore durante il login:", error);
      throw error;
    }
  };

  const register = async (email, username, password) => {
    try {
        const response = await api.post('/auth/register', {
            email: email,
            username: username,
            password: password
        });
        return response.data;
    } catch (error) {
        console.error("Errore durante la registrazione:", error.response?.data || error.message);
        throw error;
    }
  };

  const loginWithGoogle = async (googleUserData) => {
    try {
      const response = await api.post('/auth/google-login', {
        email: googleUserData.email,
        name: googleUserData.name,
        googleId: googleUserData.sub,
      });

      const { jwt } = response.data;
      handleSuccessfulAuth(jwt);

    } catch (error) {
      console.error("Errore durante il login con Google:", error);
      throw error;
    }
  };
  
  const handleSuccessfulAuth = (jwt) => {
    localStorage.setItem('token', jwt);
    const decodedUser = jwtDecode(jwt);
    setUser(decodedUser);
    setToken(jwt);
  };

  const value = {
    user,
    token,
    isAuthenticated: !!token,
    login,
    logout,
    register,
    loginWithGoogle,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  return useContext(AuthContext);
};

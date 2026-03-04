import React, { lazy, Suspense } from "react";
import { Routes, Route } from "react-router-dom";

import { AuthProvider } from './context/AuthContext'; 
import Header from './components/Header/Header.js'; 
import NoMatch from './components/NoMatch/NoMatch.js';
import RoleBasedProtectedRoute from './components/RoleBasedProtectedRoute';
import PropertyDetailPage from './pages/PropertyDetailPage/PropertyDetailPage';
import "./App.css";

function App() {

  const Home = lazy(() => import('./pages/Home/Home.js'));
  const Profile = lazy(() => import('./pages/Profile/Profile.js'));
  const PropertiesPage = lazy(() => import('./pages/PropertiesPage/PropertiesPage.js'));

  const ROLES = {
    USER: 'user',
    AGENT: 'agent',
    MANAGER: 'manager',
    ADMIN: 'admin',
  };

   const spinnerContainerStyle = {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: '100vh',
  };

  const spinnerStyle = {
    width: '50px',
    height: '50px',
    border: '6px solid #f4f4f4',
    borderTop: '6px solid #e24747',
    borderRadius: '50%',
    animation: 'spin 1s linear infinite',
  };


  return (
    <AuthProvider>
      <div className="App">
        <Header />

        <Suspense 
          fallback={
            <div style={spinnerContainerStyle}>
              <div style={spinnerStyle}></div>
            </div>
          }
        >
          <Routes>
            
            <Route path="/" element={<Home />} />
            <Route path="/properties" element={<PropertiesPage />} />
            <Route path="/property/:propertyId" element={<PropertyDetailPage />} /> 
            
            <Route
              path="/profile"
              element={
                <RoleBasedProtectedRoute allowedRoles={[ROLES.USER, ROLES.AGENT, ROLES.MANAGER, ROLES.ADMIN]}>
                  <Profile />
                </RoleBasedProtectedRoute>
              }
            />

            <Route path="*" element={<NoMatch />} />
          </Routes>
        </Suspense>
      </div>
    </AuthProvider>
  );
}

export default App;
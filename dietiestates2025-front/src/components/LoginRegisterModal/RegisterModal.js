import './LoginRegisterModal.css'; 
import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';

const RegisterModal = ({ isOpen, onSwitch }) => {
  const { register } = useAuth();

  const [email, setEmail] = useState('');
  const [name, setName] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  if (!isOpen) {
    return null;
  }

  const handleContentClick = (e) => {
    e.stopPropagation();
  };
  
  const handleRegisterSubmit = async (e) => {
      e.preventDefault();
      setError('');
      setSuccess('');

      if (password !== confirmPassword) {
        setError("Le password non coincidono!");
        return;
      }

      try {
        await register(email, name, password);
        
        setSuccess("Registrazione completata! Ora puoi effettuare il login.");
        
        setTimeout(() => {
          onSwitch('login');
        }, 2000);

      } catch (err) {
        const errorMessage = err.response?.data?.message || "Si è verificato un errore. Riprova.";
        setError(errorMessage);
      }
  };

  return (
    <div className="modal-overlay" onClick={() => onSwitch('none')}>
      <div className="modal-content" onClick={handleContentClick}>
        <button className="close-button" onClick={() => onSwitch('none')}>×</button>
        
        <h2>Create your account</h2>
        <form onSubmit={handleRegisterSubmit}>
          <label htmlFor="name">Full Name</label>
          <input 
            className='input-generic'
            type="text"
            id="name"
            placeholder="Enter your full name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />

          <label htmlFor="email">Email</label>
          <input 
            className='input-generic'
            type="email"
            id="email"
            placeholder="Enter email address"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />

          <label htmlFor="reg-password">Password</label>
          <input 
            className='input-generic'
            type="password"
            id="reg-password"
            placeholder="Create a password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          
          <label htmlFor="confirm-password">Confirm Password</label>
          <input 
            className='input-generic'
            type="password"
            id="confirm-password"
            placeholder="Confirm your password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />
          
          {error && <p className="error-message">{error}</p>}
          {success && <p className="success-message">{success}</p>}
          
          <button type="submit" className="btn btn-primary btn-login">Register</button>
        </form>
      </div>
    </div>
  );
};

export default RegisterModal;
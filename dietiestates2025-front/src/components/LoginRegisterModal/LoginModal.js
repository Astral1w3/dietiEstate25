import './LoginRegisterModal.css';
import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useGoogleLogin } from '@react-oauth/google';

const LoginModal = ({ isOpen, onSwitch }) => {
   const { login, loginWithGoogle } = useAuth(); // Ottieni entrambe le funzioni dal contesto
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);

  // Gestione del successo del login con Google
  const handleGoogleLoginSuccess = async (tokenResponse) => {
    // Nota: useGoogleLogin ora restituisce un token di accesso, non un JWT.
    // Per ottenere le info dell'utente, dobbiamo fare una chiamata all'API di Google.
    try {
        const accessToken = tokenResponse.access_token;
        const response = await fetch('https://www.googleapis.com/oauth2/v3/userinfo', {
            headers: {
                'Authorization': `Bearer ${accessToken}`,
            },
        });

        const googleUserData = await response.json();
        console.log("Dati utente da Google API:", googleUserData);

        // Ora chiama la funzione del contesto con i dati ottenuti
        await loginWithGoogle(googleUserData);
        onSwitch('none'); // Chiudi il modale

    } catch (err) {
        console.error("Errore nel recupero dati utente da Google:", err);
        setError("Accesso con Google non riuscito. Riprova.");
    }
  };

  // Gestione dell'errore del login con Google
  const handleGoogleLoginError = () => {
    setError("Accesso con Google non riuscito. Riprova.");
    console.log('Login Failed');
  };

  // Il gancio `useGoogleLogin` ora è chiamato incondizionatamente ad ogni render
  const googleLogin = useGoogleLogin({
    onSuccess: handleGoogleLoginSuccess,
    onError: handleGoogleLoginError,
    // flow: 'auth-code', // Opzionale: per ottenere un codice da inviare al backend
  });


  if (!isOpen) {
    return null;
  }

  const handleContentClick = (e) => {
    e.stopPropagation();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (!email || !password) {
      setError("Email e password sono obbligatori.");
      return;
    }

    try {
      await login(email, password);
      onSwitch('none');
    } catch (err) {
      setError("Credenziali non valide. Riprova.");
      console.error(err);
    }
  };

  const content = (
    <>
      <h2>Sign up or Log in</h2>
      <form onSubmit={handleSubmit}>
        <label htmlFor="email">Email</label>
        <input
          className='input-generic'
          type="email"
          id="email"
          placeholder="Enter email address"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <label htmlFor="password" style={{ marginTop: '1rem' }}>Password</label>
        <input
          className='input-generic'
          type="password"
          id="password"
          placeholder="Enter password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button type="submit" className="btn btn-primary btn-login">Login</button>

        {error && <p className="error-message" style={{ marginTop: '1rem' }}>{error}</p>}
      </form>

      <p className="register-prompt" style={{ marginTop: '1rem' }}>
        Don't have an account? <a href="#!" className="register-link" onClick={(e) => {
          e.preventDefault();
          onSwitch('register');
        }}>
          Register now
        </a>
      </p>
      <div className="divider">OR</div>

      <div className="social-login">
        <button className="social-btn google" onClick={() => googleLogin()}>Sign in with Google</button>
      </div>

      <p className="terms">
        I accept DietiEstates25's <a href="#!">Terms of Use</a> and <a href="#!">Privacy Notice</a>.
      </p>
    </>
  );

  return (
    <div className="modal-overlay" onClick={() => onSwitch('none')}>
      <div className="modal-content" onClick={handleContentClick}>
        <button className="close-button" onClick={() => onSwitch('none')}>×</button>
        {content}
      </div>
    </div>
  );
};

export default LoginModal;
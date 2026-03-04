import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext'; 
import LoginModal from '../LoginRegisterModal/LoginModal';
import RegisterModal from '../LoginRegisterModal/RegisterModal';
import './Header.css';

const Header = () => {
    const [modalView, setModalView] = useState('none');
    const { isAuthenticated, user } = useAuth();
    const location = useLocation();

    
    const transparentHeaderRoutes = ['/']; 
    const isTransparentTheme = transparentHeaderRoutes.includes(location.pathname);
    
    const headerClasses = `main-header ${isTransparentTheme ? 'theme-transparent' : 'theme-solid'}`;

    const switchModal = (target) => setModalView(target);


    return (
        <>
            <header className={headerClasses}>
                <div className="nav-left">
                    <Link to="/" className="logo">DietiEstates25</Link>
                    <nav className="main-nav">
                        <Link to="/properties?location=Napoli&type=Sale">Sale</Link>
                        <Link to="/properties?location=Napoli&type=Rent">Rent</Link>
                    </nav>
                </div>
                <div className="nav-right">
                    
                    {isAuthenticated ? (
                        <>
                            
                            <span className="welcome-message">Hello, {user?.username}!</span> 
                            <Link to="/profile" className="profile-icon" aria-label="Vai al profilo">
                                <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                    <circle cx="12" cy="7" r="4"></circle>
                                </svg>
                            </Link>
                        </>
                    ) : (   
                        <button onClick={() => setModalView('login')} className="btn-login">
                            Signup or Login
                        </button>
                    )}

                    <button className="hamburger-menu" aria-label="Open menu">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="3" y1="12" x2="21" y2="12"></line><line x1="3" y1="6" x2="21" y2="6"></line><line x1="3" y1="18" x2="21" y2="18"></line></svg>
                    </button>
                </div>
            </header>

            
            {!isAuthenticated && modalView === 'login' && (
                <LoginModal isOpen={true} onSwitch={switchModal} />
            )}
            {!isAuthenticated && modalView === 'register' && (
                <RegisterModal isOpen={true} onSwitch={switchModal} />
            )}
        </>
    );
};

export default Header;
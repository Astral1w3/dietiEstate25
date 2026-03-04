import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { PERMISSIONS, MENU_OPTIONS } from '../../config/permissions';
import './ProfileMenu.css';

const ProfileMenu = ({ activeView, onSelectView }) => {
    const { user } = useAuth();
    
    if (!user || !user.role) {
        return null;
    }

    const availableOptions = PERMISSIONS[user.role] || [];

    return (
        <nav className="profile-menu">
            <ul>
                {availableOptions.map(optionKey => (
                    <li key={optionKey}>
                        <button
                            className={activeView === optionKey ? 'active' : ''}
                            onClick={() => onSelectView(optionKey)}
                        >
                            {MENU_OPTIONS[optionKey]}
                        </button>
                    </li>
                ))}
            </ul>
        </nav>
    );
};

export default ProfileMenu;
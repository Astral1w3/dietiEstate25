import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';

import ProfileMenu from '../../components/ProfileMenu/ProfileMenu';
import ViewProfileDetails from '../../components/ViewProfileDetails/ViewProfileDetails';
import AddPropertyForm from '../../components/AddPropertyForm/AddPropertyForm';
import CreateUserForm from '../../components/CreateUserForm/CreateUserForm';
import ChangePasswordForm from '../../components/ChangePasswordForm/ChangePasswordForm';
import AgentDashboard from '../../components/AgentDashboard/AgentDashboard';
import Logout from '../../components/Logout/Logout'
import BookingsView from '../../components/BookingsView/BookingsView';
import OffersView from '../../components/OffersView/OffersView'

import './Profile.css';

const ProfilePage = () => {
    const { user } = useAuth();
    const [activeView, setActiveView] = useState('viewDetails');

    const renderActiveView = () => {
        switch (activeView) {
            case 'viewDetails':
                return <ViewProfileDetails />;
            case 'addProperty':
                return <AddPropertyForm />;
            case 'viewDashboard':
                return <AgentDashboard />;
            case 'createAgent':
                return <CreateUserForm roleToCreate="Agent" />;
            case 'createManager':
                return <CreateUserForm roleToCreate="Manager" />;
            case 'changePassword':
                return <ChangePasswordForm />;
            case 'logout':
                return <Logout />;
            case 'bookingsView':
                return <BookingsView/>
            case 'offersView':
                return <OffersView/>
            default:
                return <ViewProfileDetails />;
        }
    };

    return (
        <div className="profile-page-container">
            <header className="profile-header">
                <h1>Hello, {user?.username || 'User'}!</h1>
                <p>Role: {user?.role}</p>
            </header>
            <main className="profile-main-content">
                <div className="profile-menu-container">
                    <ProfileMenu activeView={activeView} onSelectView={setActiveView} />
                </div>
                <div className="profile-view-container">
                    {renderActiveView()}
                </div>
            </main>
        </div>
    );
};

export default ProfilePage;
import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import './ViewProfileDetails.css';

const ViewProfileDetails = () => {
    const { user } = useAuth();
    const [formData] = useState({
        username: user?.username || 'ERROR',
        email: user?.email || 'ERROR',
    });

    return (
        <div className="card">
            <h2>Personal details</h2>
            <hr />
            <form className="personal-details-form">
                <div className="form-group">
                    <label htmlFor="username">Username</label>
                    <input type="text" id="Username" value={formData.username} readOnly />
                </div>
                <div className="form-group full-width">
                    <label htmlFor="email">Email ID</label>
                    <input type="email" id="email" value={formData.email} readOnly />
                </div>
            </form>
        </div>
    );
};

export default ViewProfileDetails;
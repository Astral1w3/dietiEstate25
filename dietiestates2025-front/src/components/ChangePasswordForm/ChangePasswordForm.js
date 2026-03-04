import React, { useState, useEffect } from 'react';
import './ChangePasswordForm.css';
import { useAuth } from '../../context/AuthContext';
import api from '../../services/api';
import ConfirmationModal from '../ConfirmationModal/ConfirmationModal';

const ChangePasswordForm = () => {
    const { user } = useAuth();
    const [passwords, setPasswords] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
    });
    const [isPasswordSet, setIsPasswordSet] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [message, setMessage] = useState({ text: '', type: '' });

    const [isModalOpen, setIsModalOpen] = useState(false);

    useEffect(() => {
        if (!user) return;
        
        const checkPasswordStatus = async () => {
            setIsLoading(true);
            try {
                const response = await api.get(`/user/${user.email}/has-password`);
                setIsPasswordSet(response.data.hasPassword);
            } catch (error) {
                const errorMsg = error.response?.data || "Failed to verify password status.";
                console.error("Error checking password status:", errorMsg);
                setMessage({ text: errorMsg, type: 'error' });
            } finally {
                setIsLoading(false);
            }
        };
        checkPasswordStatus();
    }, [user]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setPasswords(prevState => ({ ...prevState, [name]: value }));
    };


    const handleSubmit = (e) => {
        e.preventDefault();
        setMessage({ text: '', type: '' });

        if ((isPasswordSet && !passwords.currentPassword) || !passwords.newPassword || !passwords.confirmPassword) {
            setMessage({ text: 'Please fill in all required fields.', type: 'error' });
            return;
        }
        if (passwords.newPassword.length < 8) {
            setMessage({ text: 'New password must be at least 8 characters long.', type: 'error' });
            return;
        }
        if (passwords.newPassword !== passwords.confirmPassword) {
            setMessage({ text: 'New passwords do not match.', type: 'error' });
            return;
        }
        
        setIsModalOpen(true);
    };

    const handleConfirmSubmit = async () => {
    setIsModalOpen(false);

    if (!user) {
        setMessage({ text: 'User is not logged in.', type: 'error' });
        return;
    }

    setIsSubmitting(true);

    try {
        const payload = {
            email: user.email,
            currentPassword: passwords.currentPassword,
            newPassword: passwords.newPassword,
        };
        if (!isPasswordSet) {
            delete payload.currentPassword;
        }

        const response = await api.patch('/user/change-password', payload);

        const successText = response.data.message || 'Password updated successfully!';
        
        setMessage({ text: successText, type: 'success' });
        setPasswords({ currentPassword: '', newPassword: '', confirmPassword: '' });
        if (!isPasswordSet) {
            setIsPasswordSet(true);
        }
        } catch (error) {
            const errorText = error.response?.data?.message || 'An error occurred while updating the password.';
            
            console.error("Failed to submit new password:", error.response?.data || error);
            setMessage({ text: errorText, type: 'error' });
        } finally {
            setIsSubmitting(false);
        }
    };

    if (isLoading) return <div className="card-loading">Checking account status...</div>;
    if (isPasswordSet === null && message.text) return <div className="card-error">{message.text}</div>;
    
    return (
        <div className="card">
            <h2>{isPasswordSet ? 'Change Your Password' : 'Set Your Account Password'}</h2>
            <hr />
            <form onSubmit={handleSubmit} noValidate>
                {isPasswordSet && (
                    <div className="form-group full-width">
                        <label htmlFor="currentPassword">Current Password</label>
                        <input type="password" id="currentPassword" name="currentPassword" value={passwords.currentPassword} onChange={handleChange} required />
                    </div>
                )}
                <div className="form-group full-width">
                    <label htmlFor="newPassword">New Password</label>
                    <input type="password" id="newPassword" name="newPassword" value={passwords.newPassword} onChange={handleChange} minLength="8" required />
                </div>
                <div className="form-group full-width">
                    <label htmlFor="confirmPassword">Confirm New Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" value={passwords.confirmPassword} onChange={handleChange} required />
                </div>
                {message.text && (
                    <div className={`form-message ${message.type}`}>
                        {message.text}
                    </div>
                )}
                <div className="form-actions">
                    <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
                        {isSubmitting ? 'Updating...' : 'Update Password'}
                    </button>
                </div>
            </form>

            
            <ConfirmationModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onConfirm={handleConfirmSubmit}
                title="Confirm Password Change"
            >
                <p>Are you sure you want to update your password?</p>
            </ConfirmationModal>
        </div>
    );
};

export default ChangePasswordForm;
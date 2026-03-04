import React, { useState } from 'react';
import './CreateUserForm.css';
import { createUser } from '../../services/userService'
import { useAuth } from '../../context/AuthContext'

const CreateUserForm = ({ roleToCreate }) => {
    const { user } = useAuth();
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
    });

    const [message, setMessage] = useState({ text: '', type: '' });
    const [isLoading, setIsLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prevState => ({
            ...prevState,
            [name]: value
        }));
    };

    const validateEmail = (email) => {
        const re = /\S+@\S+\.\S+/;
        return re.test(email);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage({ text: '', type: '' });

        if (!formData.username || !formData.email || !formData.password) {
            setMessage({ text: 'All fields are required.', type: 'error' });
            return;
        }
        if (!validateEmail(formData.email)) {
            setMessage({ text: 'Please enter a valid email address.', type: 'error' });
            return;
        }
        if (formData.password.length < 8) {
            setMessage({ text: 'Password must be at least 8 characters long.', type: 'error' });
            return;
        }

        setIsLoading(true);


        try {
            const newUser = {
                username: formData.username,
                email: formData.email,
                userPassword: formData.password,
                roleName: roleToCreate,
                emailCreator:  user.email
            };

            const createdUser = await createUser(newUser);

            setMessage({
                text: `${roleToCreate} "${createdUser.username}" creato con successo!`,
                type: 'success'
            });

            setFormData({ username: '', email: '', password: '' });

        } catch (error) {
            setMessage({ text: 'Si è verificato un errore. Riprova.', type: 'error' });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="card">
            <h2>Create New {roleToCreate}</h2>
            <hr />
            <form onSubmit={handleSubmit} noValidate>
                <div className="form-group full-width">
                    <label htmlFor="username">Username</label>
                    <input
                        type="text"
                        id="username"
                        name="username"
                        value={formData.username}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="form-group full-width">
                    <label htmlFor="email">Email Address</label>
                    <input
                        type="email"
                        id="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="form-group full-width">
                    <label htmlFor="password">Password</label>
                    <input
                        type="password"
                        id="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                        placeholder="Min. 8 characters"
                    />
                </div>

                
                {message.text && (
                    <div className={`form-message ${message.type}`}>
                        {message.text}
                    </div>
                )}

                <div className="form-actions">
                    <button type="submit" className="btn btn-primary" disabled={isLoading}>
                        {isLoading ? 'Creating...' : `Create ${roleToCreate}`}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CreateUserForm;
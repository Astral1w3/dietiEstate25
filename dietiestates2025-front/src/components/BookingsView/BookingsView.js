import React, { useState, useMemo, useEffect } from 'react';
import { Link } from 'react-router-dom'; 
import { FaCalendarAlt } from 'react-icons/fa';

import { getMyBookings } from '../../services/visitService';

import StatCard from '../StatCard/StatCard';
import './BookingsView.css';

const BookingsView = () => {
    const [bookings, setBookings] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    
    const filter = 'All';

    useEffect(() => {
        const fetchBookings = async () => {
            try {
                setIsLoading(true);
                const data = await getMyBookings();
                setBookings(data);
                setError(null);
            } catch (err) {
                setError("Failed to load bookings. Please try again later.");
                console.error(err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchBookings();
    }, []);

    const filteredBookings = useMemo(() => {
        if (filter === 'All') {
            return bookings;
        }
        return bookings.filter(b => b.status === filter); 
    }, [filter, bookings]);

    const totalBookings = bookings.length;

    if (isLoading) {
        return <div className="dashboard-view"><h2>Loading bookings...</h2></div>;
    }

    if (error) {
        return <div className="dashboard-view"><h2 className="error-message">{error}</h2></div>;
    }

    return (
        <div className="dashboard-view">
            <h2>Manage Bookings</h2>
            <hr />

            <div className="stats-grid">
                <StatCard icon={<FaCalendarAlt />} title="Total Bookings" value={totalBookings} />
            </div>

            <div className="dashboard-section">
                <div className="section-header">
                    <h3>All Bookings</h3>
                </div>

                <div className="table-container">
                    {filteredBookings.length > 0 ? (
                        <table className="data-table">
                            <thead>
                                <tr>
                                    <th>Property</th>
                                    <th>Client</th>
                                    <th>Visit Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredBookings.map(booking => (
                                    <tr key={booking.id_booking}> 
                                        <td>
                                            <Link to={`/property/${booking.id_property}`} className="property-link">
                                                {booking.propertyAddress}
                                            </Link>
                                        </td>
                                        <td className="client-info">
                                            <div>{booking.clientName}</div>
                                            <small>{booking.email}</small>
                                        </td>
                                        <td>{new Date(booking.visit_date).toLocaleString('it-IT', { dateStyle: 'short' })}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    ) : (
                        <p>No bookings found for the selected filter.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default BookingsView;

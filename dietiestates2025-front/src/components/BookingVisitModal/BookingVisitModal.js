import React, { useState, useEffect } from 'react';
import DatePicker from 'react-datepicker';
import { bookVisit, getBookedDates } from '../../services/visitService';
import SuccessView from '../SuccessView/SuccessView';


import 'react-datepicker/dist/react-datepicker.css';
import './BookingVisitModal.css';

const BookingVisitModal = ({ isOpen, onClose, propertyId }) => {
    const [selectedDate, setSelectedDate] = useState(null);
    const [error, setError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isSuccess, setIsSuccess] = useState(false);
    const [bookedDates, setBookedDates] = useState([]);
    const [isLoadingDates, setIsLoadingDates] = useState(true);

    useEffect(() => {
        if (isOpen && propertyId) {
            const fetchBookedDates = async () => {
                setIsLoadingDates(true);
                try {
                    const dates = await getBookedDates(propertyId);
                    setBookedDates(dates);
                } catch (err) {
                    setError("Unable to load available dates.");
                } finally {
                    setIsLoadingDates(false);
                }
            };

            fetchBookedDates();
        }
    }, [isOpen, propertyId]);

    if (!isOpen) {
        return null;
    }

    const handleClose = () => {
        setSelectedDate(null);
        setError('');
        onClose();
        setIsSuccess(false);
    };

    const handleSubmit = async () => {
        if (!selectedDate) {
            setError("Please select a date.");
            return;
        }

        setIsSubmitting(true);
        setError('');

        try {
            await bookVisit({
                propertyId: propertyId,
                visitDate: selectedDate.toISOString(),
            });
            setIsSuccess(true);
            setTimeout(() => {
                handleClose();
            }, 2500);
        } catch (err) {
            console.error("Error while booking the visit:", err);
            setError("An error occurred. Please try again later.");
        } finally {
            setIsSubmitting(false);
        }
    };
 return (
        <div className="modal-overlay" onClick={handleClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>

                {isSuccess ? (
                    <SuccessView title="Request Sent!">
                        <p>
                            Your visit request for the day <br/>
                            <strong>{selectedDate.toLocaleDateString('it-IT')}</strong> was sent. <br/>
                            See you soon!
                        </p>
                    </SuccessView>
                ) : (
                    <>
                        <h2>Select a date for your visit</h2>
                        
                        {isLoadingDates ? (
                            <p>Loading calendar...</p>
                        ) : (
                            <DatePicker
                                selected={selectedDate}
                                onChange={(date) => {
                                    setSelectedDate(date);
                                    if (error) setError('');
                                }}
                                inline
                                minDate={new Date()}
                                excludeDates={bookedDates.map(date => new Date(date))}
                                locale="it"
                            />
                        )}

                        {error && <p className="modal-error">{error}</p>}

                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={handleClose} disabled={isSubmitting}>
                                Cancel
                            </button>
                            <button className="btn btn-primary" onClick={handleSubmit} disabled={isSubmitting || !selectedDate || isLoadingDates}>
                                {isSubmitting ? 'Sending...' : 'Send Request'}
                            </button>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
};

export default BookingVisitModal;
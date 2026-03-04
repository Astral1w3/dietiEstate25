import React from 'react';
import { Link } from 'react-router-dom';
import './NoMatch.css';

function NoMatch() {
    return (
        <div className="nomatch-container">
            <div className="nomatch-content">
                <span className="error-code">404</span>
                <h1>Page Not Found</h1>
                <p>
                    Oops! It looks like you followed a broken link or the page has been moved.
                </p>
                
                <Link to="/" className="btn btn-primary">
                    Go Back to Homepage
                </Link>
            </div>
        </div>
    );
}

export default NoMatch;
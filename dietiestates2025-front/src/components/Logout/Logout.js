import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const ViewProfileDetails = () => {


    const {logout} = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        navigate('/');  

        
        setTimeout(() => {
            logout();
        }, 50);
    };

    return (
        <div className="card">
            <h2>Log Out</h2>
            <hr/>
            <div  style={{ 
                            display: 'flex',
                            flexDirection: 'column', 
                            minHeight: '200px', 
                            alignItems: 'center',
                        }}  >
                
                <h3>Are you sure of logging out?</h3>
                <button className='btn btn-primary' onClick={handleLogout}>Log out</button>
            </div>
        </div>
    );
};

export default ViewProfileDetails;
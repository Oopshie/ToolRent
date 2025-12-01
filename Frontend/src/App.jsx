import './App.css'
import Home from './components/Home'
import Navbar from './components/NavBar'
import ToolsList from './components/ToolsList'
import AddEditTool from './components/AddEditTool' 
import ClientsList from './components/ClientsList'
import AddEditClient from './components/AddEditClient'  
import RentPage from './components/RentPage'
import RentList from './components/RentsList'
import RatePage from './components/RatePage'
import KardexList from './components/KardexList'
import ActiveRentsReport from './components/ActiveRentsReport'
import LateClientsReport from './components/LateClientsReport'
import ToolRankingReport from './components/ToolRankingReport'
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom'
import { useKeycloak } from '@react-keycloak/web'

function App() {
  const { keycloak, initialized } = useKeycloak();

  if (!initialized) return <div>Cargando...</div>;
  
  const isLoggedIn = keycloak.authenticated;
  const roles = keycloak.tokenParsed?.realm_access?.roles || [];

  const PrivateRoute = ({ element, rolesAllowed }) => {
    if (!isLoggedIn) {
      keycloak.login();
      return null;
    }
    if (rolesAllowed && !rolesAllowed.some(r => roles.includes(r))) {
      return <h2>No tienes permiso para ver esta página</h2>;
    }
    return element;
  };
  
  if (!isLoggedIn) { 
    keycloak.login(); 
    return null; 
  }
return (
    <Router>
      <div className="container">
        <Navbar>  </Navbar> 
        <Routes>
          <Route path="/" element={<Navigate to="/home" replace />} />
          <Route path="/home" element={<Home />} />

          <Route
            path="/Tools"
            element={<PrivateRoute element={<ToolsList/>} rolesAllowed={["ADMIN", "EMPLOYEE"]} />}
          />

          <Route
            path="/Tool/add"
            element={<PrivateRoute element={<AddEditTool/>} rolesAllowed={["ADMIN"]} />}
          />

          <Route
            path="/Tool/edit/:id"
            element={<PrivateRoute element={<AddEditTool/>} rolesAllowed={["ADMIN"]} />}
          />

          <Route
            path="/Clients"
            element={<PrivateRoute element={<ClientsList/>} rolesAllowed={["ADMIN", "EMPLOYEE"]} />}
          />
          
          <Route
            path="/Client/add"
            element={<PrivateRoute element={<AddEditClient/>} rolesAllowed={["ADMIN", "EMPLOYEE"]} />}
          />

          <Route
            path="/Client/edit/:id"
            element={<PrivateRoute element={<AddEditClient/>} rolesAllowed={["ADMIN"]} />}
          />

          <Route
            path="/Rent"
            element={<PrivateRoute element={<RentPage/>} rolesAllowed={["ADMIN", "EMPLOYEE"]} />}
          />

          <Route
            path="/Rents"
            element={<PrivateRoute element={<RentList />} rolesAllowed={["ADMIN", "EMPLOYEE"]} />}
          />

          <Route 
            path="/rates"
            element={<PrivateRoute element={<RatePage />} rolesAllowed={["ADMIN"]} />}
          />

          <Route
            path="/kardex"
            element={<PrivateRoute element={<KardexList />} rolesAllowed={["ADMIN"]} />}
          />
  
          <Route 
            path="/reports/active-rents" 
            element={<PrivateRoute element={<ActiveRentsReport />} rolesAllowed={["ADMIN", "EMPLOYEE"]} />}
          />
          <Route 
            path="/reports/late-clients" 
            element={<PrivateRoute element={<LateClientsReport />} rolesAllowed={["ADMIN", "EMPLOYEE"]} />}
          />
          <Route 
            path="/reports/tool-ranking" 
            element={<PrivateRoute element={<ToolRankingReport />} rolesAllowed={["ADMIN", "EMPLOYEE"]} />}
          />

        </Routes>

      </div>
    </Router>
  );
}export default App

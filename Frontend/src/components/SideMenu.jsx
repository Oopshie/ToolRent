import { ListItemButton } from "@mui/material";
import { ListItemText } from "@mui/material";
import { Divider } from "@mui/material";
import { List } from "@mui/material";
import { Box } from "@mui/material";
import Drawer from "@mui/material/Drawer";
import { useNavigate } from "react-router-dom";
import { useKeycloak } from "@react-keycloak/web";


export default function SideMenu({ open, toggleDrawer }) {
    const navigate = useNavigate();
    const { keycloak } = useKeycloak();

    // Obtener roles desde el token de Keycloak
    const roles = keycloak?.tokenParsed?.realm_access?.roles || [];
    const isAdmin = roles.includes("ADMIN");

    const listOptions = () => (
        <Box
            role="presentation"
            onClick={toggleDrawer(false)}
        >
            <List>
                <ListItemButton onClick={() => navigate("/home")}>
                    <ListItemText primary="Inicio" />
                </ListItemButton>
                
                <Divider />
                
                <ListItemButton onClick={() => navigate("/rent")}>
                    <ListItemText primary="Nuevo Arriendo" />
                </ListItemButton>

                <Divider />

                <ListItemButton onClick={() => navigate("/tools")}>
                    <ListItemText primary="Herramientas" />
                </ListItemButton>

                <Divider />

                <ListItemButton onClick={() => navigate("/clients")}>
                    <ListItemText primary="Clientes" />
                </ListItemButton>

                <Divider />
                <ListItemButton onClick={() => navigate("/rents")}>
                    <ListItemText primary="Arriendos" />
                </ListItemButton>

                {isAdmin && (
                <>
                    <Divider />
                    <ListItemButton onClick={() => navigate("/rates")}>
                    <ListItemText primary="Tarifas" />
                    </ListItemButton>
                </>
                )}
                
                {isAdmin && (
                <>
                    <Divider />
                    <ListItemButton onClick={() => navigate("/kardex")}>
                    <ListItemText primary="Kardex general" />
                    </ListItemButton>
                </>
                )}

                <Divider />
                <ListItemButton onClick={() => navigate("/reports/active-rents")}>
                    <ListItemText primary="Reporte Rentas Activas" />
                </ListItemButton>

                <Divider />
                <ListItemButton onClick={() => navigate("/reports/late-clients")}>
                    <ListItemText primary="Reporte Clientes con Atraso" />
                </ListItemButton>

                <Divider />
                <ListItemButton onClick={() => navigate("/reports/tool-ranking")}>
                    <ListItemText primary="Reporte Ranking de Herramientas" />
                </ListItemButton>

            </List>

            <Divider />
        </Box>
    );
   
    return (
        <div>
            <Drawer anchor="left" open={open} onClose={toggleDrawer(false)}>
                {listOptions()}
            </Drawer>
        </div>
    );
}
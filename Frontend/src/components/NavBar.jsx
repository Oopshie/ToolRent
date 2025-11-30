import * as React from "react";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import Sidemenu from "./SideMenu";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import MenuIcon from "@mui/icons-material/Menu";
import { useState } from "react";
import { useKeycloak } from "@react-keycloak/web";

export default function Navbar() {
  const [open, setOpen] = useState(false);
  const { keycloak, initialized } = useKeycloak();

  const toggleDrawer = (open) => (event) => {
    setOpen(open);
  };

  return (
    <Box sx={{ flexGrow: 1 }}>
      {/* Barra fija arriba */}
      <AppBar position="fixed"  sx={{ backgroundColor: "#6B1B0D", color: "#FFFFFF" }}>
        <Toolbar>
          <IconButton
            size="large"
            edge="start"
            color="inherit"
            aria-label="menu"
            sx={{ mr: 2 }}
            onClick={toggleDrawer(true)}
          >
            <MenuIcon />
          </IconButton>

          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Tool Rent: Préstamo de Herramientas
          </Typography>

          {initialized && (
            <>
              {keycloak.authenticated ? (
                <>
                  <Typography sx={{ mr: 2 }}>
                    {keycloak.tokenParsed?.preferred_username ||
                      keycloak.tokenParsed?.email}
                  </Typography>
                  <Button color="inherit" onClick={() => keycloak.logout()}>
                    Logout
                  </Button>
                </>
              ) : (
                <Button color="inherit" onClick={() => keycloak.login()}>
                  Login
                </Button>
              )}
            </>
          )}
        </Toolbar>
      </AppBar>

      {/* Espacio para que el contenido no quede tapado */}
      <Toolbar />

      <Sidemenu open={open} toggleDrawer={toggleDrawer}></Sidemenu>
    </Box>
  );
}

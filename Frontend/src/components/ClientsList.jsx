import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useKeycloak } from "@react-keycloak/web";
import clientService from "../services/clientService";

import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";

import PersonAddIcon from "@mui/icons-material/PersonAdd";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

const ClientsList = () => {
  const [clients, setClients] = useState([]);
  const navigate = useNavigate();
  const { keycloak } = useKeycloak();

  const roles = keycloak.tokenParsed?.realm_access?.roles || [];
  const isAdmin = roles.includes("ADMIN");

  const capitalizeWords = (str) =>
    str
      ?.toLowerCase()
      .split(" ")
      .map((w) => w.charAt(0).toUpperCase() + w.slice(1))
      .join(" ");

  const getClientStatusText = (s) =>
    ({ 1: "Activo", 0: "Restringido" }[s] || "Desconocido");

  const getClientStatusColor = (s) =>
    ({ 1: "#4CAF50", 0: "#C44F4F" }[s] || "#9E9E9E");

  useEffect(() => {
    clientService.getAll().then((res) => setClients(res.data));
  }, []);

  const handleDelete = (id) => {
    if (window.confirm("¿Eliminar cliente?")) {
      clientService.remove(id).then(() => {
        setClients((prev) => prev.filter((c) => c.id !== id));
      });
    }
  };

  const handleEdit = (id) => navigate(`/Client/edit/${id}`);

  return (
    <div style={{ padding: "20px" }}>
      {/* -------------------------------------------------- */}
      {/* TÍTULO */}
      {/* -------------------------------------------------- */}
      <h2
        style={{
          textAlign: "center",
          fontSize: "28px",
          color: "#6B1B0D",
          marginTop: "40px",
          marginBottom: "10px",
          fontWeight: "600",
        }}
      >
        Clientes
      </h2>

      {/* línea decorativa */}
      <div
        style={{
          width: "80px",
          height: "3px",
          backgroundColor: "#6B1B0D",
          margin: "0 auto 25px",
          borderRadius: "3px",
        }}
      ></div>

      {/* -------------------------------------------------- */}
      {/* BOTÓN AÑADIR CLIENTE */}
      {/* -------------------------------------------------- */}
      {isAdmin && (
        <div style={{ textAlign: "right", marginBottom: "15px" }}>
          <Link to="/Client/add" style={{ textDecoration: "none" }}>
            <Button
              variant="contained"
              sx={{
                backgroundColor: "#A97458",
                color: "white",
                "&:hover": { backgroundColor: "#8B5F47" },
                borderRadius: "8px",
                paddingX: "18px",
                paddingY: "8px",
                fontWeight: "600",
              }}
              startIcon={<PersonAddIcon />}
            >
              Añadir Cliente
            </Button>
          </Link>
        </div>
      )}

      {/* -------------------------------------------------- */}
      {/* TABLA */}
      {/* -------------------------------------------------- */}
      <TableContainer
        component={Paper}
        sx={{
          borderRadius: "20px",
          overflow: "hidden",
          boxShadow: "0 4px 12px rgba(0,0,0,0.15)",
        }}
      >
        <Table size="small">
          <TableHead>
            <TableRow>
              {["Nombre", "RUT", "Email", "Teléfono", isAdmin ? "Estado" : "", isAdmin ? "Acciones" : ""]
                .filter(Boolean)
                .map((title, index, arr) => (
                  <TableCell
                    key={title}
                    sx={{
                      fontWeight: "bold",
                      color: "#F7EFE5",
                      backgroundColor: "#6B1B0D",
                      fontSize: "14px",
                      padding: "10px",
                      borderTopLeftRadius: index === 0 ? "20px" : 0,
                      borderTopRightRadius: index === arr.length - 1 ? "20px" : 0,
                    }}
                  >
                    {title}
                  </TableCell>
                ))}
            </TableRow>
          </TableHead>

          <TableBody>
            {clients.map((client) => (
              <TableRow key={client.id}>
                <TableCell>{capitalizeWords(client.name)}</TableCell>
                <TableCell>{client.rut}</TableCell>
                <TableCell>{client.email}</TableCell>
                <TableCell>{client.phoneNumber}</TableCell>

                {isAdmin && (
                  <TableCell
                    sx={{
                      color: getClientStatusColor(client.status),
                      fontWeight: "bold",
                    }}
                  >
                    {getClientStatusText(client.status)}
                  </TableCell>
                )}

                {isAdmin && (
                  <TableCell>
                    <Button
                      variant="contained"
                      sx={{
                        backgroundColor: "#A97458",
                        color: "white",
                        "&:hover": { backgroundColor: "#8B5F47" },
                        marginRight: "10px",
                      }}
                      size="small"
                      startIcon={<EditIcon />}
                      onClick={() => handleEdit(client.id)}
                    >
                      Editar
                    </Button>

                    <Button
                      variant="contained"
                      sx={{
                        backgroundColor: "#6B1B0D",
                        "&:hover": { backgroundColor: "#5A0E05" },
                        color: "white",
                      }}
                      size="small"
                      startIcon={<DeleteIcon />}
                      onClick={() => handleDelete(client.id)}
                    >
                      Eliminar
                    </Button>
                  </TableCell>
                )}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
};

export default ClientsList;

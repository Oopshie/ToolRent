import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useKeycloak } from '@react-keycloak/web';
import toolService from "../services/toolService";

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

// ...imports que ya tienes

const ToolsList = () => {
  const [tools, setTools] = useState([]);
  const navigate = useNavigate();
  const { keycloak } = useKeycloak();

  const isAdmin = keycloak.tokenParsed?.realm_access?.roles.includes("ADMIN");

  const capitalizeWords = (str) =>
    str
      ?.toLowerCase()
      .split(" ")
      .map((w) => w.charAt(0).toUpperCase() + w.slice(1))
      .join(" ");

  const getStatusText = (status) =>
    ({
      1: "Disponible",
      2: "Prestado",
      3: "En reparación",
      4: "Dada de baja",
    }[status] || "Desconocido");

  const getStatusColor = (status) =>
    ({
      1: "#4CAF50",
      2: "#FF9800",
      3: "#C44F4F",
      4: "#9E9E9E",
    }[status] || "#333");

  const init = () => {
    toolService.getAll().then((res) => setTools(res.data));
  };

  useEffect(() => {
    init();
  }, []);

  const handleDelete = (id) => {
    if (window.confirm("¿Eliminar herramienta?")) {
      toolService.remove(id).then(() => init());
    }
  };

  const handleEdit = (id) => navigate(`/Tool/edit/${id}`);

  return (
    <div style={{ padding: "20px" }}>
      {/* Título */}
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
        Herramientas
      </h2>

      <div
        style={{
          width: "80px",
          height: "3px",
          backgroundColor: "#6B1B0D",
          margin: "0 auto 25px",
          borderRadius: "3px",
        }}
      ></div>

      {/* Botón Añadir */}
      {isAdmin && (
        <div style={{ textAlign: "right", marginBottom: "15px" }}>
          <Link to="/Tool/add" style={{ textDecoration: "none" }}>
            <Button
              variant="contained"
              sx={{
                backgroundColor: "#A97458",
                color: "white",
                "&:hover": { backgroundColor: "#8B5F47" },
                borderRadius: "8px",
                px: "18px",
                py: "8px",
                fontWeight: "600",
              }}
              startIcon={<PersonAddIcon />}
            >
              Añadir Herramienta
            </Button>
          </Link>
        </div>
      )}

      {/* Tabla */}
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
              {[
                "Nombre",
                "Categoría",
                "Valor Reemplazo",
                "Stock",
                "Estado",
                isAdmin ? "Acciones" : "",
              ]
                .filter(Boolean)
                .map((title, index, array) => (
                  <TableCell
                    key={title}
                    sx={{
                      fontWeight: "bold",
                      color: "#F7EFE5",
                      backgroundColor: "#6B1B0D",
                      fontSize: "14px",
                      padding: "10px",
                      borderTopLeftRadius: index === 0 ? "20px" : "0",
                      borderTopRightRadius:
                        index === array.length - 1 ? "20px" : "0",
                    }}
                    align={
                      ["Valor Reemplazo", "Stock", "Estado"].includes(title)
                        ? "right"
                        : "left"
                    }
                  >
                    {title}
                  </TableCell>
                ))}
            </TableRow>
          </TableHead>

          <TableBody>
            {tools.map((tool) => (
              <TableRow key={tool.id}>
                <TableCell>{capitalizeWords(tool.name)}</TableCell>
                <TableCell>{capitalizeWords(tool.category)}</TableCell>

                <TableCell align="right">{tool.replacementValue}</TableCell>

                <TableCell align="right">{tool.stock}</TableCell>
                
                <TableCell
                  align="right"
                  style={{
                    color: getStatusColor(tool.status),
                    fontWeight: "bold",
                  }}
                >
                  {getStatusText(tool.status)}
                </TableCell>

                {isAdmin && (
                  <TableCell align="left">
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
                      onClick={() => handleEdit(tool.id)}
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
                      onClick={() => handleDelete(tool.id)}
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

export default ToolsList;

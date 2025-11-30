import { useEffect, useState } from "react"; 
import { useNavigate, useLocation } from "react-router-dom";
import clientService from "../services/clientService";
import toolService from "../services/toolService";
import rentService from "../services/rentService";
import { useKeycloak } from "@react-keycloak/web";

import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";

export default function RentPage() {

    const navigate = useNavigate();
    const location = useLocation();

    const { keycloak } = useKeycloak();
    const employeeId = keycloak?.tokenParsed?.sub;

    const searchParams = new URLSearchParams(location.search);
    const rutFromCreation = searchParams.get("rut");

    const [rut, setRut] = useState("");
    const [client, setClient] = useState(null);
    const [notFound, setNotFound] = useState(false);

    const [tools, setTools] = useState([]);
    const [selectedToolId, setSelectedToolId] = useState(null);

    const [finishDate, setFinishDate] = useState("");
    const [message, setMessage] = useState("");

    const getStatusText = (s) => ["", "Disponible", "Prestado", "En reparación", "Dada de baja"][s];
    const getStatusColor = (s) => ["", "#4caf50", "#ff9800", "#f44336", "#9e9e9e"][s];

    const getClientStatusText = (status) => {
        switch (status) {
            case 1: return "Activo";
            case 0: return "Inactivo";
            default: return "Desconocido";
        }
    };

    const capitalizeWords = (str) => {
        if (!str) return "";
        return str
            .toLowerCase()
            .split(" ")
            .map(w => w.charAt(0).toUpperCase() + w.slice(1))
            .join(" ");
    };

    useEffect(() => {
        toolService.getAvailable()
            .then(res => setTools(res.data))
            .catch(err => console.log("Error cargando herramientas disponibles", err));
    }, []);

    async function searchClient(rutParam) {
        setMessage("");

        const rutToSearch = rutParam || rut;

        try {
            const res = await clientService.getByRut(rutToSearch);
            setClient(res.data);
            setNotFound(false);
        } catch {
            setClient(null);
            setNotFound(true);
        }
    }

    useEffect(() => {
        if (rutFromCreation) {
            setRut(rutFromCreation);
            searchClient(rutFromCreation);
        }
    }, [rutFromCreation]);

    async function createRent() {

        if (!selectedToolId || !finishDate) {
            setMessage("Debes seleccionar herramienta y fecha");
            return;
        }

        try {
            const res = await rentService.createRent({
                rut,
                toolId: selectedToolId,
                finishDate,
                userId: employeeId
            });

            if (typeof res.data === "string") {
                setMessage("⚠ " + res.data);
            } else {
                setMessage("✔ Arriendo registrado con éxito");
                setTimeout(() => navigate("/rents"), 800);
            }

        } catch (error) {
            setMessage("Error al registrar arriendo");
            console.error(error);
        }
    }

    // -------------------------------------------------------
    //                   ⬇️  RETURN CORRECTO ⬇️
    // -------------------------------------------------------
    return (
        <div style={{ paddingTop: "40px", display: "flex", justifyContent: "center" }}>
            <div
                style={{
                    width: "100%",
                    maxWidth: "900px",
                    backgroundColor: "white",
                    padding: "35px",
                    borderRadius: "20px",
                    boxShadow: "0 4px 12px rgba(0,0,0,0.15)"
                }}
            >

                {/* TÍTULO */}
                <h2
                    style={{
                        textAlign: "center",
                        fontSize: "32px",
                        color: "#6B1B0D",
                        fontWeight: "700",
                        marginBottom: "5px"
                    }}
                >
                    Registrar Arriendo
                </h2>

                <div
                    style={{
                        width: "90px",
                        height: "3px",
                        backgroundColor: "#6B1B0D",
                        margin: "0 auto 30px",
                        borderRadius: "3px"
                    }}
                ></div>

                {/* BUSCAR CLIENTE */}
                <div style={{ marginBottom: "25px", textAlign: "center" }}>
                    <label style={{ fontWeight: "bold", color: "#6B1B0D" }}>RUT del cliente:</label>
                    <input
                        type="text"
                        value={rut}
                        onChange={(e) => setRut(e.target.value)}
                        style={{
                            width: "250px",
                            padding: "10px",
                            borderRadius: "10px",
                            border: "1px solid #C9A28B",
                            backgroundColor: "#3f3f3f",
                            color: "white",
                            marginLeft: "10px"
                        }}
                    />
                    <Button
                        variant="contained"
                        sx={{
                            ml: 2,
                            backgroundColor: "#6B1B0D",
                            "&:hover": { backgroundColor: "#5e180c" }
                        }}
                        onClick={() => searchClient()}
                    >
                        Buscar
                    </Button>
                </div>

                {/* CLIENTE NO ENCONTRADO */}
                {notFound && (
                    <div style={{ color: "red", textAlign: "center", marginBottom: "20px" }}>
                        Cliente no encontrado.<br />
                        <Button
                            variant="contained"
                            sx={{
                                mt: 2,
                                backgroundColor: "#6B1B0D",
                                "&:hover": { backgroundColor: "#5e180c" }
                            }}
                            onClick={() => navigate(`/Client/Add?from=rent&rut=${rut}`)}
                        >
                            Crear Cliente
                        </Button>
                    </div>
                )}

                {/* CLIENTE ENCONTRADO */}
                {client && (
                    <Paper
                        sx={{
                            p: 3,
                            mb: 3,
                            borderRadius: "20px",
                            boxShadow: "0 4px 12px rgba(0,0,0,0.15)"
                        }}
                    >
                        <h3 style={{ color: "#6B1B0D" }}>Cliente encontrado</h3>
                        <p><b>Nombre:</b> {capitalizeWords(client.name)}</p>
                        <p><b>Email:</b> {client.email}</p>
                        <p><b>Teléfono:</b> {client.phoneNumber}</p>
                        <p><b>Estado:</b> {capitalizeWords(getClientStatusText(client.status))}</p>
                    </Paper>
                )}

                {/* TABLA DE HERRAMIENTAS */}
                {client && (
                    <TableContainer
                        component={Paper}
                        sx={{
                            borderRadius: "20px",
                            overflow: "hidden",
                            mb: 4,
                            boxShadow: "0 4px 12px rgba(0,0,0,0.15)"
                        }}
                    >
                        <h3
                            style={{
                                textAlign: "center",
                                padding: "15px",
                                color: "#6B1B0D",
                                fontWeight: "600"
                            }}
                        >
                            Herramientas Disponibles
                        </h3>

                        <Table size="small">
                            <TableHead>
                                <TableRow>
                                    <TableCell sx={{ fontWeight: "bold", backgroundColor: "#6B1B0D", color: "white" }}>Seleccionar</TableCell>
                                    <TableCell sx={{ fontWeight: "bold", backgroundColor: "#6B1B0D", color: "white" }}>Nombre</TableCell>
                                    <TableCell sx={{ fontWeight: "bold", backgroundColor: "#6B1B0D", color: "white" }}>Categoría</TableCell>
                                    <TableCell sx={{ fontWeight: "bold", backgroundColor: "#6B1B0D", color: "white" }} align="right">Valor Reposición</TableCell>
                                    <TableCell sx={{ fontWeight: "bold", backgroundColor: "#6B1B0D", color: "white" }} align="center">Estado</TableCell>
                                </TableRow>
                            </TableHead>

                            <TableBody>
                                {tools.map(tool => (
                                    <TableRow key={tool.id}>
                                        <TableCell>
                                            <input
                                                type="radio"
                                                name="toolSelect"
                                                onChange={() => setSelectedToolId(tool.id)}
                                            />
                                        </TableCell>

                                        <TableCell>{capitalizeWords(tool.name)}</TableCell>
                                        <TableCell>{capitalizeWords(tool.category)}</TableCell>
                                        <TableCell align="right">${tool.replacementValue}</TableCell>
                                        <TableCell
                                            align="center"
                                            style={{
                                                color: getStatusColor(tool.status),
                                                fontWeight: "bold"
                                            }}
                                        >
                                            {getStatusText(tool.status)}
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}

                {/* FECHA */}
                {client && (
                    <div style={{ textAlign: "center", marginBottom: "25px" }}>
                        <label style={{ fontWeight: "bold", color: "#6B1B0D" }}>
                            Fecha de devolución:
                        </label>
                        <input
                            type="date"
                            value={finishDate}
                            onChange={(e) => setFinishDate(e.target.value)}
                            style={{
                                width: "200px",
                                padding: "10px",
                                borderRadius: "10px",
                                border: "1px solid #C9A28B",
                                backgroundColor: "#3f3f3f",
                                color: "white",
                                marginLeft: "10px"
                            }}
                        />
                    </div>
                )}

                {/* BOTÓN REGISTRAR */}
                {client && (
                    <div style={{ textAlign: "center" }}>
                        <Button
                            variant="contained"
                            sx={{
                                backgroundColor: "#6B1B0D",
                                color: "white",
                                fontWeight: "600",
                                "&:hover": { backgroundColor: "#5e180c" }
                            }}
                            onClick={createRent}
                        >
                            Registrar Arriendo
                        </Button>
                    </div>
                )}

                {/* MENSAJE */}
                {message && (
                    <p style={{ marginTop: "20px", textAlign: "center", fontWeight: "bold" }}>
                        {message}
                    </p>
                )}

            </div>
        </div>
    );
}

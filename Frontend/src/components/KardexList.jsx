import { useEffect, useState } from "react";
import kardexService from "../services/kardexService";
import toolService from "../services/toolService";

import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";

export default function KardexList() {
    const [kardex, setKardex] = useState([]);
    const [tools, setTools] = useState([]);

    // filtros
    const [selectedTool, setSelectedTool] = useState("");
    const [fromDate, setFromDate] = useState("");
    const [toDate, setToDate] = useState("");

    const capitalizeWords = (str) => {
        if (!str) return "—";
        return String(str)
            .toLowerCase()
            .split(" ")
            .map(w => w.charAt(0).toUpperCase() + w.slice(1))
            .join(" ");
    };

    const formatDate = (isoString) => {
        if (!isoString) return "—";
            const date = new Date(isoString);
            return date.toLocaleString("es-CL", {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit"
            });
    };


    const getMovementText = (type) => {
        switch (type) {
            case 1: return "Préstamo";
            case 2: return "Devolución";
            case 3: return "De baja";
            case 4: return "Ingreso";
            case 5: return "En Reparación";
            default: return "Desconocido";
        }
    };

    const getMovementColor = (type) => {
        switch (type) {
            case 1: return "#8C3A2B"; // préstamo
            case 2: return "#2E8B57"; // devolución
            case 3: return "#C44F4F"; // baja
            case 4: return "#7A9E52"; // ingreso
            case 5: return "#B38A4A"; // reparación
            default: return "#5C4B3A"; // desconocido
        }
    };


    // cargar todos los movimientos
    const loadAll = () => {
        kardexService.getAll()
            .then(res => setKardex(res.data))
            .catch(err => console.error("Error al cargar kardex:", err));
    };

    // cargar herramientas para filtro
    useEffect(() => {
        toolService.getAll()
            .then(res => setTools(res.data))
            .catch(err => console.error("Error cargando tools:", err));

        loadAll();
    }, []);

    // aplicar filtro por herramienta
    const filterByTool = () => {
        if (!selectedTool) return;
        kardexService.getByTool(selectedTool)
            .then(res => setKardex(res.data))
            .catch(err => console.error("Error en filtro por herramienta:", err));
    };

    // aplicar filtro por fecha
    const filterByDate = () => {
        if (!fromDate || !toDate) return;
        kardexService.getByDateRange(fromDate, toDate)
            .then(res => setKardex(res.data))
            .catch(err => console.error("Error en filtro por fechas:", err));
    };

    // limpiar filtros
    const clearFilters = () => {
        setSelectedTool("");
        setFromDate("");
        setToDate("");
        loadAll();
    };

    return (
        <div style={{ padding: "20px" }}>
        <h2
            style={{
                textAlign: "center",
                fontSize: "28px",
                color: "#6B1B0D",
                marginTop: "40px",
                marginBottom: "10px",
                fontWeight: "600"
            }}
        >
        Kardex
        </h2>
        <div
        style={{
            width: "80px",
            height: "3px",
            backgroundColor: "#6B1B0D",
            margin: "0 auto 25px",
            borderRadius: "3px"
        }}
        ></div>

            {/* FILTROS */}
            <Paper sx={{ p: 2, mb: 3, borderRadius: "20px" }}>
                <h3
                    style={{
                        textAlign: "center",
                        color: "#6B1B0D",
                        fontSize: "20px",
                        marginBottom: "15px",
                        fontWeight: "600"
                    }}
                >
                    Filtros
                </h3>


                {/* Filtro por herramienta */}
                <div style={{ marginBottom: "1rem" }}>
                    <label><b>Herramienta:</b> </label>
                    <Select
                        value={selectedTool}
                        onChange={(e) => setSelectedTool(e.target.value)}
                        displayEmpty
                        sx={{
                            ml: 2,
                            width: "250px",
                            "& .MuiOutlinedInput-notchedOutline": {
                                borderColor: "#6B1B0D"
                            },
                            "&:hover .MuiOutlinedInput-notchedOutline": {
                                borderColor: "#6B1B0D"
                            },
                            "&.Mui-focused .MuiOutlinedInput-notchedOutline": {
                                borderColor: "#6B1B0D"
                            }
                        }}
                    >
                        <MenuItem value="">
                            <em>Seleccionar herramienta</em>
                        </MenuItem>

                        {tools.map(t => (
                            <MenuItem key={t.id} value={t.id}>
                                {t.id} - {capitalizeWords(t.name)} ({capitalizeWords(t.category)})
                            </MenuItem>
                        ))}
                    </Select>

                    <Button
                        variant="contained"
                        sx={{
                            ml: 2,
                            backgroundColor: "#A97458",
                            color: "white",
                            "&:hover": {
                                backgroundColor: "#8B5F47"
                            }
                        }}
                        onClick={filterByTool}
                        disabled={!selectedTool}
                    >
                        Filtrar
                    </Button>
                </div>

                {/* Filtro por fechas */}
                <div style={{ marginBottom: "1rem" }}>
                    <label><b>Desde:</b></label>
                    <input
                        type="date"
                        value={fromDate}
                        onChange={(e) => setFromDate(e.target.value)}
                        style={{ marginLeft: "10px" }}
                    />

                    <label style={{ marginLeft: "20px" }}><b>Hasta:</b></label>
                    <input
                        type="date"
                        value={toDate}
                        onChange={(e) => setToDate(e.target.value)}
                        style={{ marginLeft: "10px" }}
                    />

                    <Button
                        variant="contained"
                        sx={{
                            ml: 2,
                            backgroundColor: "#A97458",
                            color: "white",
                            "&:hover": {
                                backgroundColor: "#8B5F47"
                            }
                        }}
                        onClick={filterByDate}
                        disabled={!fromDate || !toDate}
                    >
                        Filtrar
                    </Button>
                </div>

                {/* limpiar */}
                <Button variant="outlined"
                sx={{color: "#6B1B0D",
                    borderColor: "#6B1B0D",
                    "&:hover": {
                        backgroundColor: "#6B1B0D22",
                        borderColor: "#6B1B0D",}  
                }}
                onClick={clearFilters}>
                    Limpiar filtros
                </Button>
            </Paper>

            {/* TABLA */}
            <TableContainer
                component={Paper}
                sx={{
                    borderRadius: "20px",
                    overflow: "hidden",
                    boxShadow: "0 4px 12px rgba(0,0,0,0.15)"
                }}
            >
                <Table size="small">

                    {/* ENCABEZADO */}
                    <TableHead>
                        <TableRow>
                            {["Fecha", "Tipo", "Herramienta", "Categoría", "Cantidad", "Empleado"].map((title, index) => (
                                <TableCell
                                    key={title}
                                    sx={{
                                        fontWeight: "bold",
                                        color: "#F7EFE5",
                                        backgroundColor: "#6B1B0D",
                                        fontSize: "14px",
                                        padding: "10px",
                                        borderTopLeftRadius: index === 0 ? "20px" : "0",
                                        borderTopRightRadius: index === 5 ? "20px" : "0"
                                    }}
                                    align={title === "Cantidad" ? "right" : "left"}
                                >
                                    {title}
                                </TableCell>
                            ))}
                        </TableRow>
                    </TableHead>

                    {/* CUERPO */}
                    <TableBody>
                        {kardex.map(k => (
                            <TableRow key={k.id}>
                                
                                <TableCell>{formatDate(k.movementDate)}</TableCell>

                                <TableCell
                                    sx={{
                                        color: getMovementColor(k.movementType),
                                        fontWeight: "bold"
                                    }}
                                >
                                    {getMovementText(k.movementType)}
                                </TableCell>

                                <TableCell>{capitalizeWords(k.toolName)}</TableCell>
                                <TableCell>{capitalizeWords(k.toolCategory)}</TableCell>

                                <TableCell align="right">{k.quantity}</TableCell>

                                <TableCell>{capitalizeWords(k.employeeName)}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>

                </Table>
            </TableContainer>
                    </div>
        );
}

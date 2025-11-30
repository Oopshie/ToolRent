import { useEffect, useState } from "react";
import rentService from "../services/rentService";

import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";

import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import Typography from "@mui/material/Typography";
import Checkbox from "@mui/material/Checkbox";
import FormControlLabel from "@mui/material/FormControlLabel";

export default function RentList() {
  const [rents, setRents] = useState([]);

  const [open, setOpen] = useState(false);
  const [selectedRentId, setSelectedRentId] = useState(null);

  const [damaged, setDamaged] = useState(false);
  const [irreparable, setIrreparable] = useState(false);

  const [totalToShow, setTotalToShow] = useState(null);
  const [showTotalModal, setShowTotalModal] = useState(false);

  const init = () => {
    rentService.getAll().then((response) => {
      setRents(response.data);
    });
  };

  useEffect(() => {
    init();
  }, []);

  const capitalizeWords = (str) => {
    if (!str) return "—";
    return String(str)
      .toLowerCase()
      .split(" ")
      .filter(Boolean)
      .map((w) => w.charAt(0).toUpperCase() + w.slice(1))
      .join(" ");
  };

  const isLate = (rent) => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const finish = new Date(rent.finishDate);
    finish.setHours(0, 0, 0, 0);

    return !rent.returnDate && finish < today;
  };

  const getStatusColor = (rent) => {
    if (isLate(rent)) return "#C44F4F"; // rojo suave vino
    if (rent.active) return "#7A9E52"; // verde suave
    return "#9e9e9e";
  };

  const getStatusText = (rent) => {
    if (isLate(rent)) return "Atrasado";
    if (rent.active) return "Activo";
    return "Finalizado";
  };

  const openReturnModal = (rentId) => {
    setSelectedRentId(rentId);
    setDamaged(false);
    setIrreparable(false);
    setOpen(true);
  };

  const confirmReturn = async () => {
    try {
      const res = await rentService.returnRent(
        selectedRentId,
        damaged,
        irreparable
      );

      setTotalToShow(res.data.totalAmount);
      setOpen(false);
      setShowTotalModal(true);

      init();
    } catch (error) {
      console.error("Error al devolver herramienta:", error);
    }
  };

  return (
    <>
      {/* TÍTULO */}
      <h2
        style={{
          textAlign: "center",
          fontSize: "28px",
          color: "#6B1B0D",
          marginTop: "30px",
          marginBottom: "5px",
          fontWeight: "600",
        }}
      >
        Listado de Arriendos
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

      <TableContainer
        component={Paper}
        sx={{
          borderRadius: "20px",
          overflow: "hidden",
          width: "90%",
          margin: "auto",
          boxShadow: "0 4px 12px rgba(0,0,0,0.15)",
        }}
      >
        <Table size="small">
          <TableHead>
            <TableRow>
              {[
                "Cliente",
                "Herramienta",
                "Empleado",
                "Fecha Inicio",
                "Fecha Término",
                "Fecha Devolución",
                "Estado",
                "Monto Total",
              ].map((title, i) => (
                <TableCell
                  key={i}
                  sx={{
                    fontWeight: "bold",
                    color: "#F7EFE5",
                    backgroundColor: "#6B1B0D",
                    fontSize: "14px",
                    padding: "10px",
                    textAlign: i >= 3 ? "center" : "left",
                  }}
                >
                  {title}
                </TableCell>
              ))}
            </TableRow>
          </TableHead>

          <TableBody>
            {rents.map((rent) => (
              <TableRow key={rent.id}>
                <TableCell>{capitalizeWords(rent.clientName)}</TableCell>
                <TableCell>{capitalizeWords(rent.toolName)}</TableCell>
                <TableCell>{capitalizeWords(rent.employeeName)}</TableCell>

                <TableCell align="center">{rent.startDate}</TableCell>
                <TableCell align="center">{rent.finishDate}</TableCell>
                <TableCell align="center">{rent.returnDate ?? "—"}</TableCell>

                <TableCell
                  align="center"
                  style={{
                    color: getStatusColor(rent),
                    fontWeight: "bold",
                  }}
                >
                  {getStatusText(rent)}
                </TableCell>

                <TableCell align="center">
                  {rent.active ? (
                    <Button
                      variant="contained"
                      sx={{
                        backgroundColor: "#A97458",
                        "&:hover": { backgroundColor: "#8B5F47" },
                      }}
                      onClick={() => openReturnModal(rent.id)}
                    >
                      Devolver
                    </Button>
                  ) : (
                    <b>${rent.totalAmount}</b>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* MODAL 1: DAÑADO / IRREPARABLE */}
      <Dialog
        open={open}
        onClose={() => setOpen(false)}
        PaperProps={{
          style: {
            borderRadius: "20px",
            padding: "10px",
            backgroundColor: "#FFFFFF",
          },
        }}
      >
        <DialogTitle
          style={{
            color: "#6B1B0D",
            fontWeight: "700",
            textAlign: "center",
          }}
        >
          Devolver Herramienta
        </DialogTitle>

        <DialogContent>
          <Typography style={{ marginBottom: "10px", color: "#6B1B0D" }}>
            ¿La herramienta está dañada?
          </Typography>

          <FormControlLabel
            control={
              <Checkbox
                checked={damaged}
                onChange={(e) => {
                  setDamaged(e.target.checked);
                  if (!e.target.checked) setIrreparable(false);
                }}
                sx={{ color: "#6B1B0D" }}
              />
            }
            label="Sí, está dañada"
            sx={{ color: "#6B1B0D" }}
          />

          {damaged && (
            <FormControlLabel
              control={
                <Checkbox
                  checked={irreparable}
                  onChange={(e) => setIrreparable(e.target.checked)}
                  sx={{ color: "#6B1B0D" }}
                />
              }
              label="Es irreparable"
              sx={{ color: "#6B1B0D" }}
            />
          )}
        </DialogContent>

        <DialogActions>
          <Button
            onClick={() => setOpen(false)}
            sx={{
              color: "#6B1B0D",
              fontWeight: "bold",
            }}
          >
            Cancelar
          </Button>

          <Button
            variant="contained"
            onClick={confirmReturn}
            sx={{
              backgroundColor: "#6B1B0D",
              "&:hover": { backgroundColor: "#5A150B" },
            }}
          >
            Confirmar
          </Button>
        </DialogActions>
      </Dialog>

      {/* MODAL 2: TOTAL A PAGAR */}
      <Dialog
        open={showTotalModal}
        onClose={() => setShowTotalModal(false)}
        PaperProps={{
          style: {
            borderRadius: "20px",
            backgroundColor: "#FFFFFF",
            padding: "10px",
          },
        }}
      >
        <DialogTitle
          style={{
            color: "#6B1B0D",
            fontWeight: "700",
            textAlign: "center",
          }}
        >
          Devolución Exitosa
        </DialogTitle>

        <DialogContent>
          <Typography
            variant="h6"
            style={{ textAlign: "center", color: "#6B1B0D" }}
          >
            Total a pagar:
            <br />
            <b style={{ fontSize: "22px" }}>${totalToShow}</b>
          </Typography>
        </DialogContent>

        <DialogActions>
          <Button
            variant="contained"
            onClick={() => setShowTotalModal(false)}
            sx={{
              backgroundColor: "#6B1B0D",
              "&:hover": { backgroundColor: "#5A150B" },
            }}
          >
            Cerrar
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}

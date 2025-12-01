import { useEffect, useState } from "react";
import reportService from "../services/reportService";
import "../report.css";

export default function LateClientsReport() {
  const [late, setLate] = useState([]);
  const [filtered, setFiltered] = useState([]);

  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");

  useEffect(() => {
    reportService.getLateClients().then((res) => {
      console.log("📌 Datos recibidos en LateClientsReport:", res.data);
      setLate(res.data);
      setFiltered(res.data);
    });
  }, []);

  const filter = () => {
    if (!from && !to) {
      setFiltered(late);
      return;
    }

    const result = late.filter((c) => {
      const d = c.finishdate; // viene como string desde el backend
      return (!from || d >= from) && (!to || d <= to);
    });

    setFiltered(result);
  };

  return (
    <div className="report-container">
      <div className="report-box">
        <h2 className="report-title">Clientes con Atraso</h2>
        <div className="divider"></div>

        {/* FILTROS */}
        <div className="filter-row">
          <label>Desde:</label>
          <input type="date" value={from} onChange={(e) => setFrom(e.target.value)} />

          <label>Hasta:</label>
          <input type="date" value={to} onChange={(e) => setTo(e.target.value)} />

          <button className="filter-btn" onClick={filter}>Filtrar</button>
        </div>

        {/* TABLA */}
        <table className="report-table">
          <thead>
            <tr>
              <th>Cliente</th>
              <th>Fecha de Término</th>
            </tr>
          </thead>

          <tbody>
            {filtered.length === 0 ? (
              <tr>
                <td colSpan="2" style={{ textAlign: "center" }}>
                  No hay clientes con atraso en este rango
                </td>
              </tr>
            ) : (
              filtered.map((c) => (
                <tr key={c.rentid}>
                  <td>{c.clientname}</td>
                  <td>{c.finishdate}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

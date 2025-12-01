import { useEffect, useState } from "react";
import reportService from "../services/reportService";
import "../report.css"; 

export default function ActiveRentsReport() {
  const [rents, setRents] = useState([]);
  const [filtered, setFiltered] = useState([]);

  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");

  useEffect(() => {
    reportService.getActiveRents().then((res) => {
      console.log("Active rents recibidos:", res.data); // Debug útil
      setRents(res.data);
      setFiltered(res.data);
    });
  }, []);

  const filter = () => {
    if (!from && !to) {
      setFiltered(rents);
      return;
    }

    const result = rents.filter((r) => {
      const d = r.startDate;
      return (!from || d >= from) && (!to || d <= to);
    });

    setFiltered(result);
  };

  return (
    <div className="report-container">
      <div className="report-box">
        <h2 className="report-title">Arriendos Activos</h2>
        <div className="divider"></div>

        {/* FILTRO */}
        <div className="filter-row">
          <label>Desde:</label>
          <input
            type="date"
            value={from}
            onChange={(e) => setFrom(e.target.value)}
          />

          <label>Hasta:</label>
          <input
            type="date"
            value={to}
            onChange={(e) => setTo(e.target.value)}
          />

          <button className="filter-btn" onClick={filter}>
            Filtrar
          </button>
        </div>

        {/* TABLA */}
        <table className="report-table">
          <thead>
            <tr>
              <th>Cliente</th>
              <th>Herramienta</th>
              <th>Fecha Inicio</th>
              <th>Fecha Término</th>
            </tr>
          </thead>

          <tbody>
            {filtered.length === 0 ? (
              <tr>
                <td colSpan="4" style={{ textAlign: "center" }}>
                  No hay arriendos en este rango
                </td>
              </tr>
            ) : (
              filtered.map((r) => (
                <tr key={r.rentId ?? r.id}>
                  <td>{r.clientName}</td>
                  <td>{r.toolName}</td>
                  <td>{r.startDate}</td>
                  <td>{r.finishDate}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

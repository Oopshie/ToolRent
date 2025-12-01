import { useEffect, useState } from "react";
import reportService from "../services/reportService";
import "../report.css";

export default function ToolRankingReport() {
  const [ranking, setRanking] = useState([]);

  useEffect(() => {
    reportService.getToolRanking().then((res) => {
      console.log("Ranking recibido:", res.data);
      setRanking(res.data);
    });
  }, []);

  return (
    <div className="report-container">
      <div className="report-box">
        <h2 className="report-title">Ranking de Herramientas</h2>
        <div className="divider"></div>

        <table className="report-table">
          <thead>
            <tr>
              <th>Herramienta</th>
              <th>Veces Arrendada</th>
            </tr>
          </thead>

          <tbody>
            {ranking.length === 0 ? (
              <tr>
                <td colSpan="2" style={{ textAlign: "center" }}>
                  No hay datos disponibles
                </td>
              </tr>
            ) : (
              ranking.map((row, index) => (
                <tr key={index}>
                  <td>{row.toolName}</td>
                  <td>{row.timesUsed}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

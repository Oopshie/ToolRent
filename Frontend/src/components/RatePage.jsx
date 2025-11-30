import { useEffect, useState } from "react";
import rateService from "../services/rateService";

export default function RatePage() {

  const [dailyRent, setDailyRent] = useState("");
  const [dailyLate, setDailyLate] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    rateService.getLatest().then(res => {
      if (res.data) {
        setDailyRent(res.data.dailyRentalRate);
        setDailyLate(res.data.dailyLateFeeRent);
      }
    });
  }, []);

  const saveRates = async () => {
    if (!dailyRent || !dailyLate) {
      setMessage("Completa todos los campos.");
      return;
    }

    try {
      await rateService.create({
        dailyRentalRate: Number(dailyRent),
        dailyLateFeeRent: Number(dailyLate)
      });

      setMessage("Tarifas guardadas correctamente.");

    } catch (error) {
      setMessage("Error al guardar tarifas.");
      console.error(error);
    }
  };

  // ===== ESTILOS UNIFICADOS =====

  const container = {
    width: "450px",
    margin: "50px auto",
    padding: "30px",
    backgroundColor: "white",
    borderRadius: "20px",
    boxShadow: "0 4px 12px rgba(0,0,0,0.15)"
  };

  const label = {
    color: "#6B1B0D",
    textAlign: "center",
    fontWeight: "500",
    marginBottom: "6px",
    marginTop: "12px",
    fontSize: "16px"
  };

  const input = {
    width: "100%",
    padding: "12px",
    borderRadius: "8px",
    border: "1px solid #444",
    backgroundColor: "#333",
    color: "white",
    fontSize: "16px",
    marginBottom: "15px",
    boxSizing: "border-box",
  };

  const button = {
    width: "100%",
    padding: "12px",
    borderRadius: "10px",
    border: "none",
    backgroundColor: "#6B1B0D",
    color: "white",
    fontWeight: "600",
    fontSize: "16px",
    cursor: "pointer",
    marginTop: "15px"
  };

  return (
    <div style={container}>
      {/* Título */}
      <h2 style={{ textAlign: "center", color: "#6B1B0D", fontSize: "26px", marginBottom: "10px" }}>
        Configuración de Tarifas
      </h2>

      <div style={{
        width: "80px",
        height: "3px",
        backgroundColor: "#6B1B0D",
        margin: "0 auto 25px",
        borderRadius: "3px"
      }}></div>

      {/* Campos */}

      <label style={label}>Tarifa diaria de arriendo:</label>
      <input
        type="number"
        value={dailyRent}
        onChange={e => setDailyRent(e.target.value)}
        style={input}
      />

      <label style={label}>Multa diaria por atraso:</label>
      <input
        type="number"
        value={dailyLate}
        onChange={e => setDailyLate(e.target.value)}
        style={input}
      />

      <button style={button} onClick={saveRates}>
        Guardar Tarifas
      </button>

      {message && (
        <p style={{ marginTop: "15px", textAlign: "center", color: "#6B1B0D", fontWeight: "600" }}>
          {message}
        </p>
      )}
    </div>
  );
}

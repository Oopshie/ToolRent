import { useParams, useNavigate } from "react-router-dom";
import toolService from "../services/toolService";
import { useState, useEffect } from "react";

const AddEditTool = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  // Campos
  const [name, setName] = useState("");
  const [category, setCategory] = useState("");
  const [replacementValue, setReplacementValue] = useState("");
  const [status, setStatus] = useState(1);

  // Autocomplete
  const [backendNames, setBackendNames] = useState([]);
  const [backendCategories, setBackendCategories] = useState([]);
  const [showNameSuggestions, setShowNameSuggestions] = useState([]);
  const [showCategorySuggestions, setShowCategorySuggestions] = useState([]);

  const isEditing = Boolean(id);

  // Capitalizar palabras
  const capitalize = (str) =>
    str
      .toLowerCase()
      .split(" ")
      .map((w) => w.charAt(0).toUpperCase() + w.slice(1))
      .join(" ");

  // ====== CARGA INICIAL ======
  useEffect(() => {
    toolService.getAll().then((res) => {
      setBackendNames([...new Set(res.data.map((t) => capitalize(t.name)))]);
      setBackendCategories([...new Set(res.data.map((t) => capitalize(t.category)))]);
    });

    if (isEditing) {
      toolService.get(id).then((res) => {
        setName(capitalize(res.data.name));
        setCategory(capitalize(res.data.category));
        setReplacementValue(res.data.replacementValue);
        setStatus(res.data.status);
      });
    }
  }, [id]);

  // ====== AUTOCOMPLETADO ======
  const handleNameChange = (e) => {
    const value = capitalize(e.target.value);
    setName(value);

    if (!isEditing && value.length > 0) {
      setShowNameSuggestions(
        backendNames.filter((n) => n.toLowerCase().includes(value.toLowerCase()))
      );
    } else {
      setShowNameSuggestions([]);
    }
  };

  const handleCategoryChange = (e) => {
    const value = capitalize(e.target.value);
    setCategory(value);

    if (!isEditing && value.length > 0) {
      setShowCategorySuggestions(
        backendCategories.filter((c) => c.toLowerCase().includes(value.toLowerCase()))
      );
    } else {
      setShowCategorySuggestions([]);
    }
  };

  const checkDuplicate = async (name, category) => {
    if (isEditing || !name || !category) return;
    const res = await toolService.checkDuplicate(name, category);
    if (res.data.exists && res.data.suggestedPrice) {
      setReplacementValue(res.data.suggestedPrice);
    }
  };

  // ====== GUARDAR ======
  const saveTool = (e) => {
    e.preventDefault();

    const tool = {
      id,
      name: capitalize(name),
      category: capitalize(category),
      replacementValue: Number(replacementValue),
      status: Number(status),
    };

    const action = isEditing ? toolService.update(tool) : toolService.create(tool);

    action
      .then(() => navigate("/Tools"))
      .catch((err) => console.log("Error al guardar herramienta:", err));
  };

  // ======= ESTILOS GLOBAL =======
  const inputStyle = {
    width: "100%",
    padding: "12px",
    borderRadius: "8px",
    border: "1px solid #444",
    backgroundColor: "#333",
    color: "white",
    fontSize: "16px",
    boxSizing: "border-box",
    marginBottom: "18px",
  };

  const buttonStyle = {
    width: "100%",
    padding: "12px",
    borderRadius: "8px",
    border: "1px solid #444",
    backgroundColor: "#6B1B0D",
    color: "white",
    fontWeight: "bold",
    fontSize: "16px",
    cursor: "pointer",
    boxSizing: "border-box",
    marginTop: "10px",
  };

  const dropdownStyle = {
    border: "1px solid #751908",
    borderRadius: "6px",
    listStyle: "none",
    padding: "0",
    marginTop: "5px",
    maxHeight: "150px",
    overflowY: "auto",
    backgroundColor: "#FFFFFF",
    boxShadow: "0 4px 12px rgba(117,25,8,0.4)",
    zIndex: 999,
    position: "absolute",
    width: "100%",
  };

  const liStyle = {
    padding: "8px",
    cursor: "pointer",
    color: "#751908",
  };

  return (
    <div style={{ display: "flex", justifyContent: "center", marginTop: "50px" }}>
      <div
        style={{
          width: "450px",
          backgroundColor: "white",
          padding: "30px",
          borderRadius: "20px",
          boxShadow: "0 4px 12px rgba(0,0,0,0.15)",
          position: "relative",
        }}
      >
        <h2
          style={{
            textAlign: "center",
            color: "#6B1B0D",
            fontSize: "26px",
            marginBottom: "10px",
            fontWeight: "600",
          }}
        >
          {isEditing ? "Editar Herramienta" : "Nueva Herramienta"}
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

        <form onSubmit={saveTool}>
          {/* Nombre */}
          <label>Nombre:</label>
          <input
            type="text"
            value={name}
            onChange={handleNameChange}
            onBlur={() => checkDuplicate(name, category)}
            style={inputStyle}
          />

          {!isEditing && showNameSuggestions.length > 0 && (
            <ul style={dropdownStyle}>
              {showNameSuggestions.map((n) => (
                <li
                  key={n}
                  onClick={() => {
                    setName(n);
                    setShowNameSuggestions([]);
                    checkDuplicate(n, category);
                  }}
                  style={liStyle}
                >
                  {n}
                </li>
              ))}
            </ul>
          )}

          {/* Categoría */}
          <label>Categoría:</label>
          <input
            type="text"
            value={category}
            onChange={handleCategoryChange}
            onBlur={() => checkDuplicate(name, category)}
            style={inputStyle}
          />

          {!isEditing && showCategorySuggestions.length > 0 && (
            <ul style={dropdownStyle}>
              {showCategorySuggestions.map((c) => (
                <li
                  key={c}
                  onClick={() => {
                    setCategory(c);
                    setShowCategorySuggestions([]);
                    checkDuplicate(name, c);
                  }}
                  style={liStyle}
                >
                  {c}
                </li>
              ))}
            </ul>
          )}

          {/* Valor */}
          <label>Valor de Reposición:</label>
          <input
            type="number"
            value={replacementValue}
            onChange={(e) => setReplacementValue(e.target.value)}
            style={inputStyle}
          />

          {/* Estado (solo editar) */}
          {isEditing && (
            <>
              <label>Estado:</label>
              <select
                value={status}
                onChange={(e) => setStatus(e.target.value)}
                style={inputStyle}
              >
                <option value={1}>Disponible</option>
                <option value={2}>Prestada</option>
                <option value={3}>En reparación</option>
                <option value={4}>Dada de baja</option>
              </select>
            </>
          )}

          {/* BOTÓN */}
          <button type="submit" style={buttonStyle}>
            {isEditing ? "Actualizar Herramienta" : "Agregar Herramienta"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default AddEditTool;

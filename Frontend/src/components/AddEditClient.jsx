import { useParams, useNavigate, useLocation } from "react-router-dom";
import clientService from "../services/clientService";
import { useState, useEffect } from "react";

const AddEditClient = () => {
    const [Name, setName] = useState("");
    const [Rut, setRut] = useState("");
    const [Email, setEmail] = useState("");
    const [Phone, setPhone] = useState("");
    const [Status, setStatus] = useState(1);

    const [titleClientForm, setTitleClientForm] = useState("");

    const { id } = useParams();
    const Navigate = useNavigate();
    const location = useLocation();

    const fromRent = new URLSearchParams(location.search).get("from") === "rent";
    const rutFromRent = new URLSearchParams(location.search).get("rut");

    const saveClient = (e) => {
        e.preventDefault();

        const client = {
            id,
            name: Name,
            rut: Rut,
            email: Email,
            phoneNumber: Phone,
            status: Status
        };

        const redirect = fromRent ? `/rent?rut=${Rut}` : "/Clients";
        const call = id ? clientService.update(client) : clientService.create(client);

        call.then(() => Navigate(redirect))
            .catch((err) => console.error("Error guardando cliente:", err));
    };

    useEffect(() => {
        if (id) {
            setTitleClientForm("Editar Cliente");

            clientService.get(id)
                .then((res) => {
                    setName(res.data.name);
                    setRut(res.data.rut);
                    setEmail(res.data.email);
                    setPhone(res.data.phoneNumber);
                    setStatus(res.data.status);
                })
                .catch((error) => console.error("Error cargando cliente:", error));

        } else {
            setTitleClientForm("Nuevo Cliente");
            if (rutFromRent) setRut(rutFromRent);
        }
    }, [id]);

    const inputStyle = {
        width: "100%",
        padding: "12px",
        borderRadius: "8px",
        border: "1px solid #444",
        backgroundColor: "#333",
        color: "white",
        fontSize: "16px",
        marginBottom: "18px",
        boxSizing: "border-box",
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
                    {titleClientForm}
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

                <form onSubmit={saveClient}>
                    <label>Nombre:</label>
                    <input
                        type="text"
                        value={Name}
                        onChange={(e) => setName(e.target.value)}
                        style={inputStyle}
                    />

                    <label>Rut:</label>
                    <input
                        type="text"
                        value={Rut}
                        onChange={(e) => setRut(e.target.value)}
                        style={inputStyle}
                    />

                    <label>Email:</label>
                    <input
                        type="email"
                        value={Email}
                        onChange={(e) => setEmail(e.target.value)}
                        style={inputStyle}
                    />

                    <label>Teléfono:</label>
                    <input
                        type="text"
                        value={Phone}
                        onChange={(e) => setPhone(e.target.value)}
                        style={inputStyle}
                    />

                    <button type="submit" style={buttonStyle}>
                        {id ? "Actualizar Cliente" : "Agregar Cliente"}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default AddEditClient;

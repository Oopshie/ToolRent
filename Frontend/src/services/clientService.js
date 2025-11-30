import httpTool from "../http-common";

// Obtener todos los clientes
const getAll = () => {
    return httpTool.get('/api/clients/clients')
        .catch(error => {
            console.log("Ruta /api/clients/ falló, intentando /api/clients/clients");
            return httpTool.get('/api/clients/clients');
        });
};

// Crear cliente
const create = (data) => {
    return httpTool.post("/api/clients/", data);
};

// Obtener cliente por ID
const get = (id) => {
    return httpTool.get(`/api/clients/${id}`);
};

// Obtener cliente por RUT
const getByRut = (rut) => {
    return httpTool.get(`/api/clients/getByRut/${rut}`)
        .catch(error => {
            console.log("Ruta /api/clients/getByRut/ falló, intentando /api/clients/clients/getByRut");
            return httpTool.get(`/api/clients/clients/getByRut/${rut}`);
        });
};

// Actualizar cliente
const update = (data) => {
    return httpTool.put(`/api/clients/`, data);
};

// Eliminar cliente
const remove = (id) => {
    return httpTool.delete(`/api/clients/${id}`);
};

export default { getAll, create, get, getByRut, update, remove };

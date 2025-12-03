import httpTool from "../http-common"

const getAll=() => {
    // Intentar primero con la ruta estándar REST
    return httpTool.get('/api/tools/')
        .catch(error => {
            console.log('Ruta /api/tools/ falló, intentando /api/tools/tools');
            // Si falla, intentar con la ruta duplicada como en clientService
            return httpTool.get('/api/tools/tools');
        });
}

// Obtener solo herramientas disponibles
const getAvailable = () => {
    return httpTool.get('/api/tools/available')
        .catch(error => {
            console.log('Ruta /api/tools/available falló, intentando /api/tools/tools/available');
            return httpTool.get('/api/tools/tools/available');
        });
};

const create=data => {
    return httpTool.post("/api/tools/", data);
}

const get=id => {
    return httpTool.get(`/api/tools/${id}`);
}

const update = data => {
    return httpTool.put(`/api/tools/${data.id}`, data);
}

const remove=id => {
    return httpTool.delete(`/api/tools/${id}`);
}       

// Nuevo método para verificar duplicados y obtener precio sugerido
const checkDuplicate = (name, category) => {
    return httpTool.get(`/api/tools/check-duplicate?name=${encodeURIComponent(name)}&category=${encodeURIComponent(category)}`);
}


export default { getAll, getAvailable, create, get, update, remove, checkDuplicate }; 
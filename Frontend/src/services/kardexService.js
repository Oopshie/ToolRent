import httpTool from "../http-common"; // mismo estilo que tus otros services

// Obtener TODOS los movimientos del Kardex
const getAll = () => {
    return httpTool.get('/api/kardex/all')
        .catch(error => {
            console.log("Ruta /api/kardex/all falló", error);
            throw error;
        });
};

// Movimientos filtrados por herramienta
const getByTool = (toolId) => {
    return httpTool.get(`/api/kardex/tool/${toolId}`)
        .catch(error => {
            console.log(`Ruta /api/kardex/tool/${toolId} falló`, error);
            throw error;
        });
};

// Movimientos filtrados por fechas
const getByDateRange = (from, to) => {
    return httpTool.get(`/api/kardex/date-range?from=${from}&to=${to}`)
        .catch(error => {
            console.log("Ruta /api/kardex/date-range falló", error);
            throw error;
        });
};

export default { getAll, getByTool, getByDateRange };

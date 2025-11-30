import httpTool from "../http-common";


const createRent = (data) => {
    return httpTool.post('/api/rent', data)
        .catch(error => {
            console.log("Ruta /api/rent falló, intentando /api/rent/");
            return httpTool.post('/api/rent/', data);
        });
};

const getAll = () => {
  return httpTool.get("/api/rent/all");
};

// Finalizar arriendo (devolver herramienta)
const finishRent = (rentId, data) => {
    return httpTool.put(`/api/rent/finish/${rentId}`, data)
        .catch(error => {
            console.log("Ruta /api/rent/finish falló, intentando /api/rent/rents/finish");
            return httpTool.put(`/api/rent/rents/finish/${rentId}`, data);
        });
};

const returnRent = (rentId, damaged, irreparable) => {
  return httpTool.post(`/api/rent/return/${rentId}`, {
    damaged,
    irreparable,
  });
};


export default { createRent, getAll, finishRent, returnRent };
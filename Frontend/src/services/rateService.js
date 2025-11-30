import httpTool from "../http-common";

const getLatest = () => {
  return httpTool.get("/api/rate/latest");
};

const create = (data) => {
  return httpTool.post("/api/rate/", data);
};

export default {
  getLatest,
  create
};

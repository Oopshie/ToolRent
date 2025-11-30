import axios from "axios";
import keycloak from "./services/keycloak";

const toolRentBackendServer = import.meta.env.VITE_TOOL_RENT_BACKEND_SERVER;
const toolRentBackendPort = import.meta.env.VITE_TOOL_RENT_BACKEND_PORT;

console.log(toolRentBackendServer)
console.log(toolRentBackendPort)

const api = axios.create({
  baseURL: `http://${toolRentBackendServer}:${toolRentBackendPort}`,
  headers: {
    "Content-Type": "application/json"
  } 
});

api.interceptors.request.use(async (config) => {
  console.log('Making request to:', config.url);
  console.log('Keycloak authenticated:', keycloak.authenticated);
  
  if (keycloak.authenticated) {
    await keycloak.updateToken(30);
    config.headers.Authorization = `Bearer ${keycloak.token}`;
    console.log('Added Authorization header');
  } else {
    console.log('Not authenticated, no Authorization header added');
  }
  
  return config;
}, (error) => {
  console.error('Request interceptor error:', error);
  return Promise.reject(error);
});

export default api;
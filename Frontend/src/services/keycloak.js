import Keycloak from "keycloak-js"

const keycloak = new Keycloak({
  url: "http://localhost:9090",
  realm: "sisph-realm",
  clientId: "sisph-frontend",
});

export default keycloak;

import httpTool from "../http-common";

const getActiveRents = () => {
    return httpTool.get('/api/reports/active');
}

const getLateClients = () => {
    return httpTool.get('/api/reports/late');
}

const getToolRanking = () => {
    return httpTool.get('/api/reports/ranking');
}

export default {getActiveRents, getLateClients, getToolRanking};
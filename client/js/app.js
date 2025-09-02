class ChronoCrittersApp {
    constructor() {
        this.authService = new AuthService();
        this.userService = new UserService();
        this.lobbyService = new LobbyService(this.authService);
    }

    init() {
        // Load saved session on startup
        this.authService.loadSavedSession();
        
        // Setup global functions for HTML onclick handlers
        window.loginPreset = (username, password) => {
            this.authService.loginPreset(username, password);
        };

        window.logout = () => {
            this.authService.logout();
            this.lobbyService.disconnect();
        };

        window.fetchRoster = () => {
            this.userService.fetchRoster();
        };

        window.selectPreset = (playerId) => {
            this.userService.selectPreset(playerId);
        };

        window.connectWebSocket = () => {
            this.lobbyService.connectWebSocket();
        };

        window.joinMatchmaking = () => {
            this.lobbyService.joinMatchmaking();
        };
    }
}

// Initialize the application when the page loads
window.onload = function() {
    const app = new ChronoCrittersApp();
    app.init();
};
// Global services
let authService;
let userService;
let lobbyService;
let battleUI;

// Initialize services when page loads
document.addEventListener('DOMContentLoaded', function() {
    authService = new AuthService();
    userService = new UserService(authService);
    lobbyService = new LobbyService(authService);
    battleUI = new BattleUI(lobbyService, authService);
    
    // Set battle UI reference in lobby service
    lobbyService.setBattleUI(battleUI);
    
    // Check if user is already logged in - FIX: use correct method name
    if (authService.isAuthenticated()) {
        authService.loadSavedSession(); // Load the saved session data
        updateAuthUI();
        document.getElementById('connectBtn').disabled = false;
    }
});

// Authentication functions
function loginPreset(username, password) {
    // FIX: use correct method name
    authService.loginPreset(username, password)
        .then(() => {
            updateAuthUI();
            document.getElementById('connectBtn').disabled = false;
        })
        .catch(error => {
            document.getElementById('authStatus').textContent = 'Login failed: ' + error.message;
            document.getElementById('authStatus').className = 'status error';
        });
}

function logout() {
    authService.logout();
    updateAuthUI();
    lobbyService.disconnect();
    document.getElementById('connectBtn').disabled = true;
}

function updateAuthUI() {
    const user = authService.getCurrentUser();
    const authStatus = document.getElementById('authStatus');
    
    if (user && user.username) {
        authStatus.textContent = `Logged in as: ${user.username} (ID: ${user.userId})`;
        authStatus.className = 'status success';
    } else {
        authStatus.textContent = 'Not logged in';
        authStatus.className = 'status info';
    }
}

// Roster functions
function fetchRoster() {
    userService.fetchRoster();
}

function selectPreset(playerId) {
    userService.selectPreset(playerId);
}

function displayRoster(player) {
    // Implementation for displaying roster
}

// Matchmaking functions
function connectWebSocket() {
    if (authService.isAuthenticated()) {
        lobbyService.connectWebSocket();
    } else {
        alert('Please login first');
    }
}

function joinMatchmaking() {
    lobbyService.joinMatchmaking();
}

function leaveBattle() {
    if (battleUI) {
        battleUI.hideBattle();
    }
}
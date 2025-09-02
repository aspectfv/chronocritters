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
    
    // Check if user is already logged in
    if (authService.isLoggedIn()) {
        updateAuthUI();
        document.getElementById('connectBtn').disabled = false;
    }
});

// Authentication functions
function loginPreset(username, password) {
    authService.login(username, password)
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
    
    if (user) {
        authStatus.textContent = `Logged in as: ${user.username} (ID: ${user.userId})`;
        authStatus.className = 'status success';
    } else {
        authStatus.textContent = 'Not logged in';
        authStatus.className = 'status info';
    }
}

// Roster functions
function fetchRoster() {
    const playerId = document.getElementById('playerId').value;
    if (!playerId) {
        alert('Please enter a player ID');
        return;
    }
    
    userService.getPlayer(playerId)
        .then(player => displayRoster(player))
        .catch(error => {
            document.getElementById('roster').innerHTML = `<div class="status error">Error: ${error.message}</div>`;
        });
}

function selectPreset(playerId) {
    document.getElementById('playerId').value = playerId;
    fetchRoster();
}

function displayRoster(player) {
    const rosterDiv = document.getElementById('roster');
    
    let html = `<h3>${player.username}'s Roster:</h3>`;
    
    if (player.roster && player.roster.length > 0) {
        player.roster.forEach((critter, index) => {
            html += `
                <div style="border: 1px solid #ddd; padding: 10px; margin: 10px 0; border-radius: 5px;">
                    <h4>${critter.name} (${critter.type})</h4>
                    <p><strong>Base Stats:</strong> HP: ${critter.baseStats.health}, ATK: ${critter.baseStats.attack}, DEF: ${critter.baseStats.defense}</p>
                    <p><strong>Abilities:</strong></p>
                    <ul>
                        ${critter.abilities.map(ability => 
                            `<li>${ability.name} (${ability.type}) - Power: ${ability.power}</li>`
                        ).join('')}
                    </ul>
                </div>
            `;
        });
    } else {
        html += '<p>No critters in roster</p>';
    }
    
    rosterDiv.innerHTML = html;
}

// Matchmaking functions
function connectWebSocket() {
    lobbyService.connectWebSocket();
}

function joinMatchmaking() {
    lobbyService.joinMatchmaking();
}

// Battle functions
function leaveBattle() {
    battleUI.leaveBattle();
}
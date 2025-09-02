class LobbyService {
    constructor(authService) {
        this.authService = authService;
        this.stompClient = null;
        this.baseUrl = 'http://localhost:8081';
    }

    connectWebSocket() {
        if (!this.authService.isAuthenticated()) {
            alert('Please login first');
            return;
        }

        const socket = new SockJS(`${this.baseUrl}/ws`);
        this.stompClient = Stomp.over(socket);

        const headers = {
            'Authorization': 'Bearer ' + this.authService.getToken()
        };

        this.stompClient.connect(headers, (frame) => {
            // Set session attributes after connection
            if (frame.headers['session-id']) {
                const user = this.authService.getCurrentUser();
                this.stompClient.send('/app/session/set', {}, JSON.stringify({
                    userId: user.userId,
                    username: user.username
                }));
            }

            document.getElementById('matchmakingStatus').textContent = 'Connected to lobby';
            document.getElementById('matchmakingStatus').className = 'status success';
            document.getElementById('joinMatchmakingBtn').disabled = false;
            
            // Subscribe to personal matchmaking status updates
            const user = this.authService.getCurrentUser();
            this.stompClient.subscribe(`/user/${user.userId}/matchmaking/status`, (message) => {
                const match = JSON.parse(message.body);
                this.handleMatchFound(match);
            });
            
        }, (error) => {
            console.error('Connection error:', error);
            document.getElementById('matchmakingStatus').textContent = 'Connection failed: ' + error;
            document.getElementById('matchmakingStatus').className = 'status error';
        });
    }

    joinMatchmaking() {
        if (this.stompClient && this.stompClient.connected) {
            // Send join request - session attributes will be used by backend
            this.stompClient.send('/app/matchmaking/join', {}, '');
            
            document.getElementById('matchmakingStatus').textContent = 'Joining matchmaking queue...';
            document.getElementById('matchmakingStatus').className = 'status warning';
            document.getElementById('joinMatchmakingBtn').disabled = true;
        } else {
            alert('Please connect to lobby first');
        }
    }

    handleMatchFound(match) {
        const statusDiv = document.getElementById('matchmakingStatus');
        const battleDiv = document.getElementById('battleId');
        
        statusDiv.textContent = 'Match found! Preparing battle...';
        statusDiv.className = 'status success';
        battleDiv.innerHTML = `<div>Battle ID: <span id="battleIdValue">${match.battleId}</span></div>`;
        
        // Determine opponent
        const currentUserId = this.authService.getCurrentUser().userId;
        const opponent = match.playerOneId === currentUserId ? match.playerTwoId : match.playerOneId;
        const opponentName = opponent === 'p1' ? 'BlueOak' : 'RedAsh';
        
        // Immediately show confirmation dialog
        if (confirm(`Match found!\n\nYou vs ${opponentName}\nBattle ID: ${match.battleId}\n\nClick OK to proceed to battle`)) {
            // Subscribe to battle updates
            this.subscribeToBattle(match.battleId);
        }
    }

    subscribeToBattle(battleId) {
        // Subscribe to specific battle updates
        this.stompClient.subscribe(`/app/battle/${battleId}`, (battleState) => {
            if (battleState) {
                // Handle battle state updates
                document.getElementById('matchmakingStatus').textContent = 'Battle started!';
                document.getElementById('matchmakingStatus').className = 'status success';
            }
        });
        
        // Also listen for battle events
        this.stompClient.subscribe(`/topic/battle/${battleId}`, (message) => {
            const event = JSON.parse(message.body);
            
            if (event.battleState) {
                document.getElementById('matchmakingStatus').textContent = 'Battle is ready!';
                document.getElementById('matchmakingStatus').className = 'status success';
            }
        });
    }

    disconnect() {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.disconnect();
        }
        
        document.getElementById('joinMatchmakingBtn').disabled = true;
        document.getElementById('matchmakingStatus').textContent = 'Not connected to matchmaking';
        document.getElementById('matchmakingStatus').className = 'status info';
        document.getElementById('battleId').textContent = '';
    }
}
class LobbyService {
    constructor(authService) {
        this.authService = authService;
        this.stompClient = null;
        this.battleUI = null;
    }

    setBattleUI(battleUI) {
        this.battleUI = battleUI;
    }

    connectWebSocket() {
        const socket = new SockJS('http://localhost:8081/ws');
        this.stompClient = Stomp.over(socket);
        
        const headers = {
            'userId': this.authService.getCurrentUser().userId,
            'username': this.authService.getCurrentUser().username
        };

        this.stompClient.connect(headers, (frame) => {
            console.log('Connected to lobby: ' + frame);
            
            // Subscribe to matchmaking status updates
            this.stompClient.subscribe('/user/matchmaking/status', (message) => {
                const match = JSON.parse(message.body);
                this.handleMatchFound(match);
            });

            this.stompClient.subscribe('/user/matchmaking/error', (message) => {
                const error = message.body;
                document.getElementById('matchmakingStatus').textContent = 'Error: ' + error;
                document.getElementById('matchmakingStatus').className = 'status error';
                document.getElementById('joinMatchmakingBtn').disabled = false;
            });
            
            // Update UI
            document.getElementById('matchmakingStatus').textContent = 'Connected to matchmaking';
            document.getElementById('matchmakingStatus').className = 'status success';
            document.getElementById('joinMatchmakingBtn').disabled = false;
        }, (error) => {
            console.error('STOMP error:', error);
            document.getElementById('matchmakingStatus').textContent = 'Connection failed';
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
            // Show battle UI
            if (this.battleUI) {
                this.battleUI.showBattle(match.battleId);
            }
        }
    }

    subscribeToBattleUpdates(battleId, callback) {
        if (!this.stompClient || !this.stompClient.connected) {
            console.error('Not connected to lobby service');
            return;
        }

        // Subscribe to battle state updates
        this.stompClient.subscribe(`/topic/battle/${battleId}`, (message) => {
            const battleState = JSON.parse(message.body);
            callback(battleState);
        });
    }

    executeAbility(battleId, playerId, abilityId) {
        return new Promise((resolve, reject) => {
            if (!this.stompClient || !this.stompClient.connected) {
                reject(new Error('Not connected to lobby service'));
                return;
            }

            // Subscribe to the response for this specific action
            const subscription = this.stompClient.subscribe(`/user/battle/${battleId}/result`, (message) => {
                const battleState = JSON.parse(message.body);
                subscription.unsubscribe();
                resolve(battleState);
            });

            // Send ability execution request
            const request = {
                playerId: playerId,
                abilityId: abilityId
            };

            this.stompClient.send(`/app/battle/${battleId}/ability`, {}, JSON.stringify(request));

            // Set timeout for request
            setTimeout(() => {
                subscription.unsubscribe();
                reject(new Error('Request timeout'));
            }, 10000);
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
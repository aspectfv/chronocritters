class BattleUI {
    constructor(lobbyService, authService) {
        this.lobbyService = lobbyService;
        this.authService = authService;
        this.currentBattleId = null;
        this.currentBattleState = null;
        this.lastDisplayedLog = null;
    }

    showBattle(battleId) {
        this.currentBattleId = battleId;
        
        // Hide other sections and show battle
        document.querySelectorAll('.section').forEach(section => {
            if (!section.classList.contains('battle-container')) {
                section.style.display = 'none';
            }
        });
        
        document.getElementById('battleSection').style.display = 'block';
        document.getElementById('battleInfo').textContent = `Battle ID: ${battleId}`;
        
        // Subscribe to battle updates
        this.lobbyService.subscribeToBattleUpdates(battleId, (battleState) => {
            this.updateBattleState(battleState);
        });
    }

    updateBattleState(battleState) {
        this.currentBattleState = battleState;
        
        // Check if battle has ended
        if (battleState.activePlayerId === null) {
            this.handleBattleEnd(battleState);
            return;
        }
        
        // Update player states with correct prefixes that match HTML IDs
        this.updateCritterDisplay('playerOne', battleState.playerOne);
        this.updateCritterDisplay('playerTwo', battleState.playerTwo);
        
        // Update active player indicator
        const currentUserId = this.authService.getCurrentUser().userId;
        this.updateActivePlayer(battleState.activePlayerId, currentUserId);
        
        // Update abilities based on whose turn it is
        const currentPlayer = this.getCurrentPlayer();
        const isPlayerTurn = battleState.activePlayerId === currentUserId;
        this.updateAbilities(currentPlayer, isPlayerTurn);
        
        // Update battle log
        if (battleState.lastActionLog && battleState.lastActionLog !== this.lastDisplayedLog) {
            this.updateBattleLog(battleState.lastActionLog);
            this.lastDisplayedLog = battleState.lastActionLog;
        }
    }

    updateCritterDisplay(playerPrefix, playerState) {
        const activeCritter = playerState.roster[playerState.activeCritterIndex];
        
        if (!activeCritter) return;

        const stats = activeCritter.stats;
        const maxHp = stats.maxHp;
        const healthPercentage = (stats.currentHp / maxHp) * 100;

        // Fix the IDs to match your HTML
        const critterNameElement = document.getElementById(`${playerPrefix}CritterName`);
        const healthFillElement = document.getElementById(`${playerPrefix}HealthFill`);
        const hpElement = document.getElementById(`${playerPrefix}Hp`);
        const atkElement = document.getElementById(`${playerPrefix}Atk`);
        const defElement = document.getElementById(`${playerPrefix}Def`);

        // Check if elements exist before setting properties
        if (critterNameElement) {
            critterNameElement.textContent = activeCritter.name;
        }

        if (healthFillElement) {
            healthFillElement.style.width = `${healthPercentage}%`;
            
            // Change color based on health percentage
            if (healthPercentage > 60) {
                healthFillElement.style.background = '#28a745';
            } else if (healthPercentage > 30) {
                healthFillElement.style.background = '#ffc107';
            } else {
                healthFillElement.style.background = '#dc3545';
            }
        }

        // Update stats display
        if (hpElement) {
            hpElement.textContent = `${stats.currentHp}/${maxHp}`;
        }
        if (atkElement) {
            atkElement.textContent = stats.currentAtk;
        }
        if (defElement) {
            defElement.textContent = stats.currentDef;
        }
    }

    updateActivePlayer(activePlayerId, currentUserId) {
        const playerOneSide = document.getElementById('playerOneSide');
        const playerTwoSide = document.getElementById('playerTwoSide');
        
        // Reset classes
        playerOneSide.className = 'player-side';
        playerTwoSide.className = 'player-side';
        
        if (!activePlayerId) {
            // Battle is over
            return;
        }

        // Highlight active player
        if (activePlayerId === this.currentBattleState.playerOne.id) {
            playerOneSide.classList.add('active');
            playerTwoSide.classList.add('inactive');
        } else {
            playerTwoSide.classList.add('active');
            playerOneSide.classList.add('inactive');
        }
    }

    updateAbilities(currentPlayer, isPlayerTurn) {
        const abilityContainer = document.getElementById('abilityButtons');
        abilityContainer.innerHTML = '';

        if (!currentPlayer || !currentPlayer.roster) return;

        const activeCritter = currentPlayer.roster[currentPlayer.activeCritterIndex];
        if (!activeCritter || !activeCritter.abilities) return;

        activeCritter.abilities.forEach(ability => {
            const button = document.createElement('button');
            button.className = 'ability-btn';
            button.textContent = `${ability.name} (${ability.type}, Power: ${ability.power})`;
            button.disabled = !isPlayerTurn;
            
            if (isPlayerTurn) {
                button.onclick = () => this.useAbility(ability.id);
            }
            
            abilityContainer.appendChild(button);
        });

        if (!isPlayerTurn) {
            const waitingMsg = document.createElement('div');
            waitingMsg.textContent = 'Waiting for opponent\'s turn...';
            waitingMsg.style.fontStyle = 'italic';
            waitingMsg.style.color = '#6c757d';
            abilityContainer.appendChild(waitingMsg);
        }
    }

    updateBattleLog(actionLog) {
        if (!actionLog) return;

        const logContent = document.getElementById('battleLogContent');
        const logEntry = document.createElement('div');
        logEntry.className = 'log-entry';
        logEntry.textContent = actionLog;
        
        logContent.appendChild(logEntry);
        logContent.scrollTop = logContent.scrollHeight;
    }

    useAbility(abilityId) {
        if (!this.currentBattleId) {
            console.error('No active battle');
            return;
        }

        const currentUserId = this.authService.getCurrentUser().userId;
        
        // Check if it's the player's turn
        if (!this.currentBattleState || this.currentBattleState.activePlayerId !== currentUserId) {
            console.warn('Not your turn!');
            return;
        }
        
        // Disable all ability buttons to prevent double-clicking
        document.querySelectorAll('.ability-btn').forEach(btn => btn.disabled = true);

        // Execute ability - the response will come through the WebSocket subscription
        this.lobbyService.executeAbility(this.currentBattleId, currentUserId, abilityId)
            .catch(error => {
                console.error('Failed to execute ability:', error);
                
                // Re-enable buttons on error - but only if we still have a battle state
                if (this.currentBattleState) {
                    this.updateAbilities(
                        this.getCurrentPlayer(), 
                        this.currentBattleState.activePlayerId === currentUserId
                    );
                }
            });
    }

    getCurrentPlayer() {
        if (!this.currentBattleState) return null;
        
        const currentUserId = this.authService.getCurrentUser().userId;
        return this.currentBattleState.playerOne.id === currentUserId 
            ? this.currentBattleState.playerOne 
            : this.currentBattleState.playerTwo;
    }

    handleBattleEnd(battleState) {
        const currentUserId = this.authService.getCurrentUser().userId;
        
        // Determine winner from the battle log or remaining health
        let winner = null;
        let winnerName = '';
        
        if (battleState.lastActionLog && battleState.lastActionLog.includes('wins the battle!')) {
            // Extract winner from log message
            if (battleState.lastActionLog.includes(battleState.playerOne.username + ' wins')) {
                winner = battleState.playerOne.id;
                winnerName = battleState.playerOne.username;
            } else if (battleState.lastActionLog.includes(battleState.playerTwo.username + ' wins')) {
                winner = battleState.playerTwo.id;
                winnerName = battleState.playerTwo.username;
            }
        } else {
            // Fallback: determine winner by remaining health
            const playerOneAlive = battleState.playerOne.roster.some(critter => critter.stats.currentHp > 0);
            const playerTwoAlive = battleState.playerTwo.roster.some(critter => critter.stats.currentHp > 0);
            
            if (playerOneAlive && !playerTwoAlive) {
                winner = battleState.playerOne.id;
                winnerName = battleState.playerOne.username;
            } else if (playerTwoAlive && !playerOneAlive) {
                winner = battleState.playerTwo.id;
                winnerName = battleState.playerTwo.username;
            }
        }
        
        // Show battle end UI
        this.showBattleEndUI(winner, winnerName, currentUserId);
        
        // Update final battle log
        if (battleState.lastActionLog && battleState.lastActionLog !== this.lastDisplayedLog) {
            this.updateBattleLog(battleState.lastActionLog);
            this.lastDisplayedLog = battleState.lastActionLog;
        }
        
        // Disable all ability buttons
        document.querySelectorAll('.ability-btn').forEach(btn => btn.disabled = true);
    }

    showBattleEndUI(winnerId, winnerName, currentUserId) {
        const isPlayerWinner = winnerId === currentUserId;
        
        // Create battle end overlay
        const overlay = document.createElement('div');
        overlay.className = 'battle-end-overlay';
        overlay.innerHTML = `
            <div class="battle-end-modal">
                <div class="battle-end-header ${isPlayerWinner ? 'victory' : 'defeat'}">
                    <h2>${isPlayerWinner ? '🎉 VICTORY! 🎉' : '💀 DEFEAT 💀'}</h2>
                </div>
                <div class="battle-end-content">
                    <p class="winner-announcement">
                        ${isPlayerWinner ? 'Congratulations! You won the battle!' : `${winnerName} wins the battle!`}
                    </p>
                    <div class="battle-stats">
                        <h3>Battle Results</h3>
                        <div class="stat-row">
                            <span>Winner:</span>
                            <span class="winner-name">${winnerName}</span>
                        </div>
                        <div class="stat-row">
                            <span>Battle Duration:</span>
                            <span>Complete</span>
                        </div>
                    </div>
                    <div class="battle-end-actions">
                        <button id="viewBattleLogBtn" class="btn btn-secondary">View Full Battle Log</button>
                        <button id="returnToLobbyBtn" class="btn btn-primary">Return to Lobby</button>
                    </div>
                </div>
            </div>
        `;
        
        // Add to page
        document.body.appendChild(overlay);
        
        // Add event listeners
        document.getElementById('viewBattleLogBtn').addEventListener('click', () => {
            this.showFullBattleLog();
        });
        
        document.getElementById('returnToLobbyBtn').addEventListener('click', () => {
            this.returnToLobby();
            document.body.removeChild(overlay);
        });
        
        // Auto-close after 30 seconds
        setTimeout(() => {
            if (document.body.contains(overlay)) {
                this.returnToLobby();
                document.body.removeChild(overlay);
            }
        }, 30000);
    }

    showFullBattleLog() {
        const logContent = document.getElementById('battleLogContent');
        const allEntries = Array.from(logContent.children);
        
        const logModal = document.createElement('div');
        logModal.className = 'battle-log-modal-overlay';
        logModal.innerHTML = `
            <div class="battle-log-modal">
                <div class="battle-log-header">
                    <h3>Full Battle Log</h3>
                    <button class="close-btn">&times;</button>
                </div>
                <div class="battle-log-full-content">
                    ${allEntries.map(entry => `<div class="log-entry">${entry.textContent}</div>`).join('')}
                </div>
            </div>
        `;
        
        document.body.appendChild(logModal);
        
        // Close modal handler
        logModal.querySelector('.close-btn').addEventListener('click', () => {
            document.body.removeChild(logModal);
        });
        
        // Close on overlay click
        logModal.addEventListener('click', (e) => {
            if (e.target === logModal) {
                document.body.removeChild(logModal);
            }
        });
    }

    returnToLobby() {
        // Show other sections again
        document.querySelectorAll('.section').forEach(section => {
            section.style.display = 'block';
        });
        
        // Hide battle section
        document.getElementById('battleSection').style.display = 'none';
        
        // Reset battle state
        this.currentBattleId = null;
        this.currentBattleState = null;
        this.lastDisplayedLog = null;
        
        // Clear battle log
        document.getElementById('battleLogContent').innerHTML = '';
        
        // Re-enable matchmaking button
        document.getElementById('joinMatchmakingBtn').disabled = false;
        document.getElementById('matchmakingStatus').textContent = 'Ready for matchmaking';
        document.getElementById('matchmakingStatus').className = 'status success';
        document.getElementById('battleId').innerHTML = '';
    }

    leaveBattle() {
        // Show other sections again
        document.querySelectorAll('.section').forEach(section => {
            if (!section.classList.contains('battle-container')) {
                section.style.display = 'block';
            }
        });
        
        document.getElementById('battleSection').style.display = 'none';
        this.currentBattleId = null;
        this.currentBattleState = null;
        
        // Reset matchmaking status
        document.getElementById('matchmakingStatus').textContent = 'Left battle';
        document.getElementById('matchmakingStatus').className = 'status info';
        document.getElementById('joinMatchmakingBtn').disabled = false;
    }
}
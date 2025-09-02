class BattleUI {
    constructor(lobbyService, authService) {
        this.lobbyService = lobbyService;
        this.authService = authService;
        this.currentBattleId = null;
        this.currentBattleState = null;
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
        
        if (!battleState) {
            console.error('No battle state received');
            return;
        }

        const currentUserId = this.authService.getCurrentUser().userId;
        const isPlayerOne = battleState.playerOne.id === currentUserId;
        const currentPlayer = isPlayerOne ? battleState.playerOne : battleState.playerTwo;
        const opponent = isPlayerOne ? battleState.playerTwo : battleState.playerOne;

        // Update player names
        document.getElementById('playerOneName').textContent = battleState.playerOne.username;
        document.getElementById('playerTwoName').textContent = battleState.playerTwo.username;

        // Update critter information
        this.updateCritterDisplay('playerOne', battleState.playerOne);
        this.updateCritterDisplay('playerTwo', battleState.playerTwo);

        // Update active player styling
        this.updateActivePlayer(battleState.activePlayerId, currentUserId);

        // Update abilities for current player
        this.updateAbilities(currentPlayer, battleState.activePlayerId === currentUserId);

        // Update battle log
        this.updateBattleLog(battleState.lastActionLog);

        // Check if battle is over
        if (!battleState.activePlayerId) {
            this.handleBattleEnd();
        }
    }

    updateCritterDisplay(playerPrefix, playerState) {
        const activeCritter = playerState.roster[playerState.activeCritterIndex];
        
        if (!activeCritter) return;

        const stats = activeCritter.stats;
        const maxHp = stats.maxHp; // Change this line - use stats.maxHp instead of baseStats.health
        const healthPercentage = (stats.currentHp / maxHp) * 100;

        // Update critter name
        document.getElementById(`${playerPrefix}CritterName`).textContent = activeCritter.name;

        // Update health bar
        const healthFill = document.getElementById(`${playerPrefix}HealthFill`);
        healthFill.style.width = `${healthPercentage}%`;
        
        // Change color based on health percentage
        if (healthPercentage > 60) {
            healthFill.style.background = '#28a745';
        } else if (healthPercentage > 30) {
            healthFill.style.background = '#ffc107';
        } else {
            healthFill.style.background = '#dc3545';
        }

        // Update stats display
        document.getElementById(`${playerPrefix}Hp`).textContent = `${stats.currentHp}/${maxHp}`;
        document.getElementById(`${playerPrefix}Atk`).textContent = stats.currentAtk;
        document.getElementById(`${playerPrefix}Def`).textContent = stats.currentDef;
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
        
        // Disable all ability buttons to prevent double-clicking
        document.querySelectorAll('.ability-btn').forEach(btn => btn.disabled = true);

        this.lobbyService.executeAbility(this.currentBattleId, currentUserId, abilityId)
            .then(battleState => {
                this.updateBattleState(battleState);
            })
            .catch(error => {
                console.error('Failed to execute ability:', error);
                // Re-enable buttons on error
                this.updateAbilities(
                    this.getCurrentPlayer(), 
                    this.currentBattleState.activePlayerId === currentUserId
                );
            });
    }

    getCurrentPlayer() {
        if (!this.currentBattleState) return null;
        
        const currentUserId = this.authService.getCurrentUser().userId;
        return this.currentBattleState.playerOne.id === currentUserId 
            ? this.currentBattleState.playerOne 
            : this.currentBattleState.playerTwo;
    }

    handleBattleEnd() {
        document.getElementById('battleInfo').textContent = 'Battle Ended!';
        document.getElementById('battleInfo').className = 'status success';
        
        // Disable all ability buttons
        document.querySelectorAll('.ability-btn').forEach(btn => btn.disabled = true);
        
        // Show battle result
        setTimeout(() => {
            alert('Battle has ended! Check the battle log for results.');
        }, 1000);
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
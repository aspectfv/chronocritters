class UserService {
    constructor() {
        this.baseUrl = 'http://localhost:8080';
    }

    selectPreset(playerId) {
        document.getElementById('playerId').value = playerId;
        this.fetchRoster();
    }

    async fetchRoster() {
        const playerId = document.getElementById('playerId').value;
        const query = `query($id: ID!) { 
            getPlayer(id: $id) { 
                roster { 
                    name 
                    type 
                    baseStats { 
                        health 
                        attack 
                        defense 
                    } 
                } 
            } 
        }`;
        
        try {
            const response = await fetch(`${this.baseUrl}/graphql`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ query, variables: { id: playerId } })
            });
            
            const result = await response.json();
            const roster = result.data?.getPlayer?.roster || [];
            const rosterDiv = document.getElementById('roster');
            
            if (roster.length === 0) {
                rosterDiv.innerHTML = '<p>No critters found.</p>';
                return;
            }
            
            rosterDiv.innerHTML = '<ul>' + roster.map(critter =>
                `<li><strong>${critter.name}</strong> (${critter.type})<br>
                Health: ${critter.baseStats.health}, Attack: ${critter.baseStats.attack}, Defense: ${critter.baseStats.defense}</li>`
            ).join('') + '</ul>';
        } catch (error) {
            console.error('Error fetching roster:', error);
            document.getElementById('roster').innerHTML = '<p>Error loading roster.</p>';
        }
    }
}
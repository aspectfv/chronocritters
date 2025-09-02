class AuthService {
    constructor() {
        this.jwtToken = null;
        this.currentUser = null;
        this.currentUserId = null;
        this.baseUrl = 'http://localhost:8080';
    }

    async loginPreset(username, password) {
        try {
            const response = await fetch(`${this.baseUrl}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            if (response.ok) {
                this.jwtToken = await response.text();
                this.currentUser = username;
                // Map usernames to IDs for session attributes
                this.currentUserId = username === 'BlueOak' ? 'p1' : 'p2';
                
                localStorage.setItem('jwtToken', this.jwtToken);
                localStorage.setItem('currentUser', this.currentUser);
                localStorage.setItem('currentUserId', this.currentUserId);
                
                document.getElementById('authStatus').textContent = `Logged in as ${username}`;
                document.getElementById('authStatus').className = 'status success';
                document.getElementById('connectBtn').disabled = false;
                
                return true;
            } else {
                throw new Error('Login failed');
            }
        } catch (error) {
            console.error('Login error:', error);
            document.getElementById('authStatus').textContent = 'Login failed';
            document.getElementById('authStatus').className = 'status error';
            return false;
        }
    }

    logout() {
        this.jwtToken = null;
        this.currentUser = null;
        this.currentUserId = null;
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('currentUser');
        localStorage.removeItem('currentUserId');
        
        document.getElementById('authStatus').textContent = 'Not logged in';
        document.getElementById('authStatus').className = 'status info';
        document.getElementById('connectBtn').disabled = true;
        
        return true;
    }

    loadSavedSession() {
        const savedToken = localStorage.getItem('jwtToken');
        const savedUser = localStorage.getItem('currentUser');
        const savedUserId = localStorage.getItem('currentUserId');
        
        if (savedToken && savedUser && savedUserId) {
            this.jwtToken = savedToken;
            this.currentUser = savedUser;
            this.currentUserId = savedUserId;
            document.getElementById('authStatus').textContent = `Logged in as ${savedUser}`;
            document.getElementById('authStatus').className = 'status success';
            document.getElementById('connectBtn').disabled = false;
            return true;
        }
        return false;
    }

    getToken() {
        return this.jwtToken;
    }

    getCurrentUser() {
        return {
            username: this.currentUser,
            userId: this.currentUserId
        };
    }

    isAuthenticated() {
        return this.jwtToken !== null;
    }
}
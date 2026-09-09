# Deploying ChronoCritters to an Oracle Cloud Always Free VM

The whole stack — three Spring Boot services, MongoDB, and the React client —
runs as five containers on a single VM. Caddy fronts everything on one origin
and terminates TLS, so the browser only ever talks to one host and no backend
port is exposed to the internet.

```
                    ┌──────────── Oracle Cloud VM (ARM) ────────────┐
                    │                                               │
browser ──80/443──► │  caddy ─┬─ /            → client static files │
                    │         ├─ /graphql*    → user:8080           │
                    │         ├─ /ws*         → lobby:8081          │
                    │         └─ /battle/*    → gamelogic:8082      │
                    │                                               │
                    │  gamelogic ──gRPC──► user:9090                │
                    │  gamelogic ──HTTP──► lobby:8081               │
                    │  lobby     ──HTTP──► gamelogic:8082           │
                    │  user      ─────────► mongo:27017             │
                    └───────────────────────────────────────────────┘
```

Everything below the proxy lives on a private Docker network.

---

## 1. Create the Oracle Cloud account

Sign up at [cloud.oracle.com](https://cloud.oracle.com/). A card is required for
identity verification but Always Free resources are not charged.

**Choose your home region carefully — it cannot be changed later.** Ampere A1
capacity is the scarce resource here, and popular regions (Frankfurt, London,
Ashburn, Phoenix) are frequently exhausted. Pick the least busy region that is
still geographically reasonable for you.

## 2. Launch the VM

In the console: **Compute → Instances → Create instance**.

| Setting | Value |
|---|---|
| Image | Canonical Ubuntu 24.04 |
| Shape | `VM.Standard.A1.Flex` (Ampere, ARM) |
| OCPUs / Memory | 2 OCPU / 12 GB |
| Boot volume | 50 GB (default) |
| SSH keys | Upload your public key, or let Oracle generate one and download it |

Note the 2/12 ceiling: Oracle halved the Always Free A1 allowance from 4 OCPU /
24 GB in June 2026, and instances above the new limit are terminated. 2 OCPU and
12 GB is comfortably enough for this stack.

**If you get "Out of host capacity"**, that is the single most common blocker.
Options, in order of effort: try a different availability domain, retry every few
hours (capacity frees up in bursts), or upgrade the account to Pay-As-You-Go —
still $0 within Always Free limits, but PAYG accounts get provisioning priority.

Record the instance's **public IP address** when it finishes provisioning.

## 3. Open the firewall — both layers

Oracle blocks inbound traffic in two independent places, and missing the second
one is the classic "my site is unreachable but the server is fine" trap.

**Layer 1 — VCN security list.** Networking → Virtual Cloud Networks → your VCN →
your subnet → Security List → **Add Ingress Rules**:

| Source CIDR | IP Protocol | Destination Port Range |
|---|---|---|
| `0.0.0.0/0` | TCP | 80 |
| `0.0.0.0/0` | TCP | 443 |

**Layer 2 — the instance's own iptables.** Oracle's Ubuntu images ship with
`iptables` rules that reject everything except SSH. SSH in and run:

```bash
ssh ubuntu@<YOUR_PUBLIC_IP>

sudo iptables -I INPUT 6 -m state --state NEW -p tcp --dport 80 -j ACCEPT
sudo iptables -I INPUT 6 -m state --state NEW -p tcp --dport 443 -j ACCEPT
sudo netfilter-persistent save
```

## 4. Install Docker

```bash
sudo apt-get update && sudo apt-get upgrade -y
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
newgrp docker          # or log out and back in
docker compose version # confirm the plugin is present
```

Everything is built natively for ARM on the VM itself, so there is no
cross-compilation step and no image registry to set up.

## 5. Deploy

```bash
sudo apt-get install -y git
git clone https://github.com/aspectfv/chronocritters
cd chronocritters

cp .env.example .env
```

Edit `.env` and set a real secret — the services refuse to start without one:

```bash
# generates a 48-byte key; paste the output as JWT_SECRET
openssl rand -base64 48
```

Leave `SITE_ADDRESS=:80` for now so you can test over plain HTTP with the IP.
Then build and start everything:

```bash
docker compose up -d --build
```

The first build takes roughly 5–10 minutes (Maven downloads the full dependency
tree, then the reactor builds all five modules once). Subsequent builds reuse the
layer cache and are much faster.

Check that everything came up:

```bash
docker compose ps
docker compose logs -f user   # should end with the data seeding + "Started UserApplication"
```

Now open `http://<YOUR_PUBLIC_IP>` in a browser. Register two accounts in two
browser windows, queue both, and confirm a battle starts and both sides stay in
sync — that exercises GraphQL, the WebSocket, the REST battle API, and the gRPC
hop in one pass.

## 6. Add a domain and HTTPS

Point an `A` record at the VM's public IP. Any registrar works; a free option is
a subdomain from [DuckDNS](https://www.duckdns.org/) or similar.

Then set the domain in `.env` and restart the proxy:

```bash
# .env
SITE_ADDRESS=chronocritters.example.com

docker compose up -d web
```

Caddy provisions and renews a Let's Encrypt certificate automatically on first
request. Nothing else needs to change — the client derives its API and WebSocket
URLs from whatever origin it was served from, so HTTPS and WSS just work.

---

## Operating it

```bash
docker compose logs -f              # all services
docker compose logs -f gamelogic    # one service
docker compose restart lobby        # restart one service
docker compose down                 # stop everything (volumes survive)
docker compose up -d --build        # redeploy after a git pull
```

**Back up the database:**

```bash
docker compose exec -T mongo mongodump --archive --db=chronocritters > backup-$(date +%F).archive
```

**Restore:**

```bash
docker compose exec -T mongo mongorestore --archive --drop < backup-2026-09-09.archive
```

**Free up disk after repeated rebuilds** (Maven layers accumulate):

```bash
docker system prune -af
```

---

## Notes and caveats

- **Single instance only.** `BattleService` keeps active battles in an in-memory
  map and `BattleTimerService` holds live turn timers, so none of these services
  can be scaled to more than one replica. Restarting `gamelogic` or `lobby` drops
  any battle in progress. Moving that state to Redis is the prerequisite for
  scaling out, if you ever want to.
- **The seed runs on first startup.** The `user` service populates critters and
  abilities against an empty database. If you wipe `mongo-data`, it re-seeds.
- **Internal endpoints are blocked at the proxy.** `POST /battle/{id}`,
  `/battle/{id}/timeout`, and `/battle/{id}/update` are service-to-service calls
  and Caddy returns 404 for them from outside; they still work container-to-
  container. The `X-Service-Auth` header those calls carry is not a real
  authentication mechanism, so keeping them off the public interface matters.
- **`JWT_SECRET` must be identical** for `user` and `lobby` — one signs tokens,
  the other verifies them on WebSocket connect. Compose passes the same value to
  both. Changing it invalidates every issued token.
- **Memory headroom.** Three JVMs at `MaxRAMPercentage=60` plus MongoDB sit
  comfortably under 12 GB. If you ever move to a 1 GB box, set
  `JAVA_OPTS=-Xmx256m` per service and add swap.

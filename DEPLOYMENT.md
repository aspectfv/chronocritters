# Deploying ChronoCritters

The whole stack — three Spring Boot services, MongoDB, and the React client —
runs as five containers on a single VM. Caddy fronts everything on one origin
and terminates TLS, so the browser only ever talks to one host and no backend
port is exposed to the internet.

```
                    ┌─────────────── host VM (ARM) ─────────────────┐
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

The deployment is the same wherever it runs; only the account setup, firewall
and instance sizing differ. Pick the section for your host — **AWS EC2** below,
or Oracle Cloud, GCP and Azure further down.

---

## AWS EC2

A `t4g.small` (2 vCPU Graviton, 2 GB) runs the standard stack comfortably —
the services measure ~680 MB under load and MongoDB adds a few hundred more —
so none of the low-memory compromises further down apply.

### 1. Confirm which plan the account is on

Since July 2025 a new account picks a **Free plan** or a **Paid plan**. The Free
plan starts with $100 in credits, rising to $200 once you complete five
onboarding tasks (launch and terminate an EC2 instance, configure an RDS
database, deploy a Lambda, test a Bedrock prompt, and set a budget). It runs for
six months or until the credits are gone, then the account closes unless you
upgrade. Crucially, a Free plan account **cannot incur charges** — services stop
instead of billing you.

If you chose the Paid plan, overages go to your card, so set a budget before
anything else.

### 2. Launch the instance

EC2 → **Launch instance**:

| Setting | Value |
|---|---|
| AMI | Ubuntu Server 24.04 LTS, **64-bit (Arm)** |
| Instance type | `t4g.small` — 2 vCPU, 2 GB |
| Key pair | Create one and download the `.pem` |
| Storage | 30 GB gp3 |

Under **Network settings → security group**, add inbound rules:

| Type | Port | Source |
|---|---|---|
| SSH | 22 | My IP |
| HTTP | 80 | `0.0.0.0/0` |
| HTTPS | 443 | `0.0.0.0/0` |

The security group is the only gate here — unlike Oracle there is no second
host-level firewall to open.

### 3. Give it a stable address

EC2 → **Elastic IPs** → Allocate, then Associate with the instance. Without one
the public IP changes every stop/start. It is free while attached to a running
instance and billed only when left unattached.

### 4. Deploy

```bash
chmod 400 chronocritters.pem
ssh -i chronocritters.pem ubuntu@<ELASTIC_IP>

# 2 GB is enough to run the stack but tight to build it; swap is cheap insurance
sudo fallocate -l 2G /swapfile && sudo chmod 600 /swapfile
sudo mkswap /swapfile && sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER && newgrp docker
sudo apt-get install -y git

git clone https://github.com/Aspectzxcc/chronocritters.git
cd chronocritters
cp .env.example .env
openssl rand -base64 48        # paste as JWT_SECRET in .env

docker compose up -d --build
```

Graviton is ARM, so the images build natively on the instance exactly as they do
on Oracle's Ampere shape. Open `http://<ELASTIC_IP>` to check it.

### 5. Domain and HTTPS

Point an `A` record at the Elastic IP, set `SITE_ADDRESS` in `.env` to that
hostname, and run `docker compose up -d web`. Caddy handles the certificate.

### 6. Set a budget

Billing → **Budgets** → create a low threshold alert. This is also one of the
five tasks that unlock the second $100 of credit.

### What it costs

Roughly **$15/month** — about $12.26 for the instance and $2.40 for the 30 GB
volume — so around $88 across the six months, comfortably inside $200. AWS
includes 100 GB/month of free egress. Stop the instance between demos to stretch
credits further; the EBS volume still bills while it is stopped.

### Getting more resume value out of it

The EC2 path above is the cheap, reliable one. To make the deployment itself
worth talking about, the next steps are Terraform for the VPC, security group,
instance and Elastic IP, plus GitHub Actions using OIDC (no long-lived AWS keys)
to build images into ECR and deploy on push.

The fuller showcase — ECS Fargate with an ALB and Cloud Map service discovery for
the gRPC hop — is the architecture interviewers recognise, but the ALB alone is
about $18/month and three Fargate tasks another $27, which would burn $200 in
roughly four months. It is worth doing if you scale the tasks to zero between
demos and treat it as a portfolio artifact rather than a live service.

---

## Oracle Cloud Always Free

### 1. Create the Oracle Cloud account

Sign up at [cloud.oracle.com](https://cloud.oracle.com/). A card is required for
identity verification but Always Free resources are not charged.

**Choose your home region carefully — it cannot be changed later.** Ampere A1
capacity is the scarce resource here, and popular regions (Frankfurt, London,
Ashburn, Phoenix) are frequently exhausted. Pick the least busy region that is
still geographically reasonable for you.

### 2. Launch the VM

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

### 3. Open the firewall — both layers

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

### 4. Install Docker

```bash
sudo apt-get update && sudo apt-get upgrade -y
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
newgrp docker          # or log out and back in
docker compose version # confirm the plugin is present
```

Everything is built natively for ARM on the VM itself, so there is no
cross-compilation step and no image registry to set up.

### 5. Deploy

```bash
sudo apt-get install -y git
git clone https://github.com/Aspectzxcc/chronocritters.git
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

### 6. Add a domain and HTTPS

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

## Alternative: Google Cloud or Azure

If an Oracle account cannot be created — their signup rejects a lot of cards and
regions — the remaining free VMs are all **1 GB of RAM**, against Oracle's 12 GB.
The stack still fits, but only with the database moved off the box and the JVM
heaps constrained.

| Option | RAM | Duration | Notes |
|---|---|---|---|
| GCP `e2-micro` | 1 GB | Always free | `us-west1`, `us-central1` or `us-east1` only; 1 GB/month egress |
| Azure `B1s` | 1 GB | 12 months | Any region; 750 hours/month |

The deployment is identical to the Oracle steps above except for three changes.

**1. Put MongoDB on Atlas.** Create a free M0 cluster at
[mongodb.com/atlas](https://www.mongodb.com/cloud/atlas/register), allow access
from the VM's IP, and put the connection string in `.env`:

```
MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/chronocritters
```

**2. Build the images somewhere else.** Maven and `tsc` will exhaust 1 GB during
the build. Either build locally and push to a registry such as GHCR, or add swap
before building:

```bash
sudo fallocate -l 4G /swapfile && sudo chmod 600 /swapfile
sudo mkswap /swapfile && sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

Keep the swap file afterwards regardless — it is what absorbs the JVMs' startup
spikes.

**3. Start with the low-memory overlay**, which shrinks the heaps and skips the
bundled database:

```bash
docker compose -f docker-compose.yml -f docker-compose.lowmem.yml \
  up -d --scale mongo=0
```

Expect slower startup and slower first requests than on Oracle's 12 GB shape.
Confirm nothing is being OOM-killed with `docker compose ps` and `free -h` after
a few battles.

Firewall rules differ slightly: GCP uses VPC firewall rules
(`gcloud compute firewall-rules create allow-web --allow tcp:80,tcp:443`) and
Azure uses a Network Security Group. Neither has Oracle's second iptables layer.

**Worth knowing:** a Hetzner CX22 is roughly €4/month for 2 vCPU and 4 GB, which
removes every constraint in this section — no Atlas requirement, no heap tuning,
no swap dependency, and the plain `docker compose up -d --build` path from the
Oracle instructions works unchanged.

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

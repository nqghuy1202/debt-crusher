# Deploy DebtCrusher (HL Balance) lên VPS

Kiến trúc theo đúng sơ đồ đã thống nhất: **Nginx cài trực tiếp trên VPS** làm reverse proxy (port
80/443, public ra internet) → route vào 2 container Docker chỉ bind `127.0.0.1` (không public trực
tiếp): `debtcrusher-frontend` (Nginx serve static build của Vite, port nội bộ 3000) và
`debtcrusher-backend` (Spring Boot jar, port nội bộ 8080). MySQL chạy container riêng, không mở port
ra ngoài, chỉ backend trong cùng docker network truy cập được.

```
Browser → Nginx (host, :80/:443) → :3000 (container frontend) / :8080,/auth,/debts,/payoff,... (container backend) → MySQL (container, chỉ nội bộ)
```

Ubuntu **24.04 LTS (noble)** — hỗ trợ chính thức đến 04/2029, không cần workaround gì đặc biệt.
Domain giả định `YOUR_DOMAIN` — thay bằng domain thật của bạn ở mọi bước.

## 1. Trỏ domain về VPS

Ở nơi quản lý domain, thêm bản ghi DNS trỏ về IP VPS (nhà cung cấp VPS cho IP này sau khi tạo VPS):

| Type | Name | Value |
|------|------|-------|
| A    | @ (hoặc `app`, nếu deploy ở subdomain) | `<IP_VPS>` |

DNS có thể mất vài phút đến vài giờ để lan truyền. Kiểm tra: `ping YOUR_DOMAIN` (hoặc
`nslookup YOUR_DOMAIN`) phải trả về đúng IP VPS trước khi làm bước xin SSL ở cuối.

## 2. Chuẩn bị VPS lần đầu

SSH vào VPS bằng thông tin nhà cung cấp gửi qua email (`ssh root@<IP_VPS>`), rồi:

```bash
apt update && apt upgrade -y

# Docker + Docker Compose plugin — script get.docker.com tự nhận diện noble và cài docker-ce bản mới nhất.
curl -fsSL https://get.docker.com | sh
docker compose version   # kiểm tra plugin compose đã có, không hiện lỗi "command not found"

# Nginx (reverse proxy) + git.
apt install -y nginx git

# Firewall: chỉ mở SSH + HTTP/HTTPS. Port 3000/8080/3306 KHÔNG cần mở — chúng chỉ bind 127.0.0.1
# trong docker-compose.prod.yml, đã an toàn kể cả không có rule ufw nào cho chúng.
ufw allow OpenSSH
ufw allow 'Nginx Full'
ufw enable
```

## 3. Đưa code lên VPS

Cách khuyên dùng — qua Git (dễ `git pull` để update sau này). Nếu repo local chưa có remote:

```bash
# Trên máy local (đã làm nếu bạn có GitHub repo cho project này)
git remote add origin git@github.com:<user>/<repo>.git
git push -u origin master
```

Trên VPS:

```bash
git clone <URL_repo> /opt/debtcrusher
cd /opt/debtcrusher/environment
```

Nếu chưa muốn tạo repo GitHub, cách nhanh thay thế — copy thẳng thư mục từ máy Windows lên VPS bằng
`rsync` (chạy lệnh này ở Git Bash trên máy local, không phải trên VPS):

```bash
rsync -avz --exclude node_modules --exclude target --exclude .idea \
  /c/Users/batuo/IdeaProjects/risk-log/ root@<IP_VPS>:/opt/debtcrusher/
```

## 4. Cấu hình secrets

```bash
cd /opt/debtcrusher/environment
cp .env.prod.example .env.prod
nano .env.prod   # điền DB_ROOT_PASSWORD, DB_PASSWORD, JWT_SECRET — sinh bằng: openssl rand -base64 32
```

## 5. Build & chạy

```bash
docker compose -f docker-compose.prod.yml --env-file .env.prod up -d --build
docker compose -f docker-compose.prod.yml ps   # cả 3 service phải "running"/"healthy"
docker compose -f docker-compose.prod.yml logs -f backend   # Ctrl+C để thoát, kiểm tra app boot OK
```

Build lần đầu chậm (Maven tải dependency + `npm ci`) — có thể mất vài phút tuỳ cấu hình VPS.

## 6. Trỏ Nginx vào 2 container

```bash
cp /opt/debtcrusher/environment/nginx/app.conf /etc/nginx/sites-available/debtcrusher
sed -i 's/YOUR_DOMAIN/thaydoi.com/' /etc/nginx/sites-available/debtcrusher   # đổi thaydoi.com thành domain thật
ln -s /etc/nginx/sites-available/debtcrusher /etc/nginx/sites-enabled/
rm -f /etc/nginx/sites-enabled/default   # bỏ trang mặc định "Welcome to nginx"
nginx -t && systemctl reload nginx
```

Lúc này `http://YOUR_DOMAIN` đã lên được (chưa có HTTPS).

## 7. Bật HTTPS (Let's Encrypt)

Cài certbot qua snap (24.04 có sẵn snapd) — cách chính thức Certbot khuyến nghị hiện nay, không phụ
thuộc bản Ubuntu:

```bash
snap install core && snap refresh core
snap install --classic certbot
ln -s /snap/bin/certbot /usr/bin/certbot

certbot --nginx -d YOUR_DOMAIN
```

Certbot tự sửa `/etc/nginx/sites-available/debtcrusher`: thêm block 443 với chứng chỉ SSL + redirect
80→443, tự gia hạn chứng chỉ (snap tự cài sẵn timer gia hạn, không cần cấu hình thêm). Không cần sửa
tay file cấu hình sau bước này.

## 8. Kiểm tra

- `https://YOUR_DOMAIN` → trang React load được.
- Đăng ký tài khoản thử trên UI → gọi `POST /auth/register` qua Nginx vào backend → ghi MySQL. Nếu lỗi,
  xem mục Troubleshooting bên dưới.

## Cập nhật code sau này

```bash
cd /opt/debtcrusher && git pull
cd environment
docker compose -f docker-compose.prod.yml --env-file .env.prod up -d --build
```

`--build` rebuild lại image nếu code đổi; container cũ tự bị thay bằng container mới, MySQL data
không mất (volume `./data/db_data` không đụng tới trừ khi bạn tự xoá).

## Troubleshooting

- **502 Bad Gateway**: container backend/frontend chưa chạy hoặc chưa healthy. Check
  `docker compose -f docker-compose.prod.yml ps` và `docker compose -f docker-compose.prod.yml logs backend`.
- **Backend lên rồi nhưng gọi API 404/lỗi lạ qua domain**: thường do thêm `@RequestMapping` root mới ở
  backend mà quên thêm vào regex whitelist trong `environment/nginx/app.conf` (rồi
  `nginx -t && systemctl reload nginx` lại).
- **Backend crash lúc boot với `Unsupported class file version`**: cache layer Docker build bị stale —
  chạy lại `docker compose -f docker-compose.prod.yml build --no-cache backend`.
- **`certbot --nginx` báo lỗi domain không resolve**: DNS A record chưa trỏ đúng hoặc chưa kịp lan
  truyền — chờ thêm rồi thử lại, kiểm tra bằng `ping YOUR_DOMAIN`.
- **Đổi JWT_SECRET/DB_PASSWORD trong `.env.prod` sau khi đã chạy**: cần
  `docker compose -f docker-compose.prod.yml up -d --force-recreate backend mysql` để container đọc
  env mới (đổi `DB_PASSWORD` sau khi MySQL đã init lần đầu thì phải tự `ALTER USER` trong MySQL luôn,
  vì `MYSQL_PASSWORD` chỉ áp dụng lúc tạo user lần đầu).

## Gợi ý bảo mật thêm (không bắt buộc để chạy được, làm sau cũng được)

- `/actuator/metrics`, `/actuator/ratelimiters`, `/v3/api-docs`, `/swagger-ui/**` đang public
  (`SecurityConfig.PUBLIC_PATHS`) — hợp lý lúc dev, nhưng public thật thì nên chặn ở Nginx (thêm
  `deny all;` cho các path này trong `app.conf`) hoặc sửa `PUBLIC_PATHS` ở backend nếu không cần
  Swagger UI ngoài môi trường dev.
- MySQL container chỉ nên truy cập qua SSH tunnel khi cần debug (`ssh -L 3306:127.0.0.1:3306
  root@<IP_VPS>`, MySQL vẫn không map port ra host nên tunnel này an toàn), không nên mở port ra ngoài.

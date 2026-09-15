# docker build -f frontend/Dockerfile -t debtcrusher-frontend frontend/
#
# Serve bằng Nginx (không phải Node process) — chuẩn cho SPA build tĩnh của Vite: nhẹ hơn, không tốn
# RAM cho 1 process Node chỉ để trả file. Container Nginx này lắng nghe port 80 bên trong container;
# ra ngoài compose map thành host 127.0.0.1:3000, khớp đúng vai trò "Frontend App (Port 3000)" trong sơ đồ.
FROM node:22-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .

# Vite bake env vào bundle lúc build, không phải lúc chạy container -> phải truyền qua build arg.
# "" (rỗng) = gọi API cùng origin (/auth/login, /debts, ...) — khớp cách Nginx host route ở
# environment/nginx/app.conf, tránh phải cấu hình CORS cho domain khác.
ARG VITE_API_BASE_URL=""
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
RUN npm run build

FROM nginx:1.27-alpine AS runtime
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

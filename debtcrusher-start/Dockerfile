# Build context PHẢI là thư mục gốc repo (reactor đa module), không phải debtcrusher-start/:
#   docker build -f debtcrusher-start/Dockerfile -t debtcrusher-backend .
#
# Ghi chú: root pom.xml pin maven.compiler.source/target=26 (JDK 26 chưa release) — như đã note trong
# memory debtcrusher-local-build-environment, mọi lệnh mvn ở đây đều override về 25 để khớp máy dev.
# Nếu sau này JDK 26 GA thật và image eclipse-temurin:26-* xuất hiện, xoá 2 dòng -Dmaven.compiler.*
# bên dưới và đổi base image build+runtime sang 26 là dùng lại pin gốc của pom.

FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Cache dependency layer: copy pom trước, build lại chỉ khi code đổi thì mới re-run phase package.
COPY pom.xml .
COPY debtcrusher-domain/pom.xml debtcrusher-domain/pom.xml
COPY debtcrusher-infrastructure/pom.xml debtcrusher-infrastructure/pom.xml
COPY debtcrusher-application/pom.xml debtcrusher-application/pom.xml
COPY debtcrusher-controller/pom.xml debtcrusher-controller/pom.xml
COPY debtcrusher-start/pom.xml debtcrusher-start/pom.xml
RUN mvn -q -pl debtcrusher-domain,debtcrusher-infrastructure,debtcrusher-application,debtcrusher-controller,debtcrusher-start \
    -am dependency:go-offline -Dmaven.compiler.source=25 -Dmaven.compiler.target=25 || true

COPY debtcrusher-domain debtcrusher-domain
COPY debtcrusher-infrastructure debtcrusher-infrastructure
COPY debtcrusher-application debtcrusher-application
COPY debtcrusher-controller debtcrusher-controller
COPY debtcrusher-start debtcrusher-start

# clean bắt buộc: target/classes cũ (nếu build IntelliJ ở bytecode 26 lọt vào build context) sẽ bị
# coi là "up to date" và bỏ qua override -25, gây "Unsupported class file version" lúc Spring boot lên.
RUN mvn -q -pl debtcrusher-domain,debtcrusher-infrastructure,debtcrusher-application,debtcrusher-controller,debtcrusher-start \
    -am clean package -DskipTests -Dmaven.compiler.source=25 -Dmaven.compiler.target=25

FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app
# finalName=debtcrusher (debtcrusher-start/pom.xml) -> target/debtcrusher.jar (đã repackage thành executable jar)
COPY --from=build /app/debtcrusher-start/target/debtcrusher.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

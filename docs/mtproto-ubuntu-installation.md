# Сценарий установки MTProto Proxy на Ubuntu

Ниже — пошаговый сценарий с командами, готовыми для копирования.  
Перед запуском отредактируйте только переменные в блоке **0. Переменные**.

## 0. Переменные (отредактировать под ваш сервер)

```bash
export MT_USER="mtproxy"
export MT_DIR="/opt/MTProxy"
export MT_PORT="443"
export SSH_PORT="22"
export PUBLIC_IP="203.0.113.10"    # внешний IP вашего сервера
```

> Если порт `443` занят (например, nginx), задайте другой, например `8443`.

---

## 1. Подготовка Ubuntu

### 1.1 Обновление пакетов и базовые утилиты

```bash
sudo apt update
sudo apt -y upgrade
sudo apt -y install curl wget git ca-certificates ufw fail2ban unattended-upgrades apt-listchanges jq net-tools
```

### 1.2 Системный пользователь для MTProxy

```bash
id -u "$MT_USER" >/dev/null 2>&1 || sudo useradd --system --home "$MT_DIR" --shell /usr/sbin/nologin "$MT_USER"
sudo mkdir -p "$MT_DIR"/{bin,conf,logs}
sudo chown -R "$MT_USER":"$MT_USER" "$MT_DIR"
```

### 1.3 Включение автообновлений безопасности

```bash
sudo dpkg-reconfigure -plow unattended-upgrades
sudo systemctl enable --now unattended-upgrades
```

### 1.4 Базовый firewall (только SSH + MTProto)

```bash
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow "${SSH_PORT}/tcp"
sudo ufw allow "${MT_PORT}/tcp"
sudo ufw --force enable
sudo ufw status verbose
```

---

## 2. Установка зависимостей и проверка systemd

```bash
sudo apt -y install build-essential libssl-dev zlib1g-dev
systemctl --version
```

---

## 3. Установка и настройка MTProto Proxy

### 3.1 Получение исходников и сборка

```bash
sudo rm -rf "$MT_DIR/src"
sudo -u "$MT_USER" git clone https://github.com/TelegramMessenger/MTProxy.git "$MT_DIR/src"
cd "$MT_DIR/src"
sudo -u "$MT_USER" make
sudo install -m 0755 objs/bin/mtproto-proxy "$MT_DIR/bin/mtproto-proxy"
```

### 3.2 Получение актуальных `proxy-secret` и `proxy-multi.conf`

```bash
sudo -u "$MT_USER" wget -qO "$MT_DIR/conf/proxy-secret" https://core.telegram.org/getProxySecret
sudo -u "$MT_USER" wget -qO "$MT_DIR/conf/proxy-multi.conf" https://core.telegram.org/getProxyConfig
```

### 3.3 Генерация `secret` сервера

```bash
sudo -u "$MT_USER" bash -c 'head -c 16 /dev/urandom | xxd -ps -c 16 > "'"$MT_DIR"'/conf/mtproxy-secret"'
sudo chmod 600 "$MT_DIR/conf/mtproxy-secret"
export MT_SECRET="$(sudo cat "$MT_DIR/conf/mtproxy-secret")"
echo "MT_SECRET=$MT_SECRET"
```

---

## 4. systemd-сервис MTProxy

Создайте unit-файл:

```bash
sudo tee /etc/systemd/system/mtproxy.service >/dev/null <<EOF
[Unit]
Description=MTProto Proxy
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=${MT_USER}
Group=${MT_USER}
WorkingDirectory=${MT_DIR}
ExecStart=${MT_DIR}/bin/mtproto-proxy -u ${MT_USER} -p 8888 -H ${MT_PORT} -S ${MT_SECRET} --aes-pwd ${MT_DIR}/conf/proxy-secret ${MT_DIR}/conf/proxy-multi.conf --http-stats --allow-skip-dh
Restart=always
RestartSec=5
LimitNOFILE=65535
NoNewPrivileges=true
PrivateTmp=true
ProtectSystem=full
ProtectHome=true
ReadWritePaths=${MT_DIR}

[Install]
WantedBy=multi-user.target
EOF
```

Запуск и автозапуск:

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now mtproxy
sudo systemctl status mtproxy --no-pager -l
```

---

## 5. Защита и логи

### 5.1 fail2ban (базово)

```bash
sudo tee /etc/fail2ban/jail.d/sshd-local.conf >/dev/null <<'EOF'
[sshd]
enabled = true
maxretry = 5
findtime = 10m
bantime = 1h
EOF
sudo systemctl enable --now fail2ban
sudo fail2ban-client status sshd
```

### 5.2 Просмотр логов MTProxy

```bash
sudo journalctl -u mtproxy -n 100 --no-pager
sudo journalctl -u mtproxy -f
```

---

## 6. Проверка работоспособности

### 6.1 Проверка, что порт слушается

```bash
sudo ss -lntp | grep ":${MT_PORT}\b"
```

### 6.2 Проверка доступности порта извне (с другого хоста)

```bash
nc -vz ${PUBLIC_IP} ${MT_PORT}
```

### 6.3 Формирование ссылки для Telegram

```bash
echo "tg://proxy?server=${PUBLIC_IP}&port=${MT_PORT}&secret=dd${MT_SECRET}"
```

Откройте полученную ссылку в Telegram-клиенте и включите прокси.

---

## 7. Эксплуатация (операционные команды)

### Управление сервисом

```bash
sudo systemctl start mtproxy
sudo systemctl stop mtproxy
sudo systemctl restart mtproxy
sudo systemctl status mtproxy --no-pager -l
```

### Обновление MTProxy

```bash
cd "$MT_DIR/src"
sudo -u "$MT_USER" git pull
sudo -u "$MT_USER" make
sudo install -m 0755 objs/bin/mtproto-proxy "$MT_DIR/bin/mtproto-proxy"
sudo systemctl restart mtproxy
```

### Ротация `secret`

```bash
sudo systemctl stop mtproxy
sudo -u "$MT_USER" bash -c 'head -c 16 /dev/urandom | xxd -ps -c 16 > "'"$MT_DIR"'/conf/mtproxy-secret"'
export MT_SECRET="$(sudo cat "$MT_DIR/conf/mtproxy-secret")"
sudo sed -i "s/-S [0-9a-f]\{32\}/-S ${MT_SECRET}/" /etc/systemd/system/mtproxy.service
sudo systemctl daemon-reload
sudo systemctl start mtproxy
echo "Новый tg:// URL: tg://proxy?server=${PUBLIC_IP}&port=${MT_PORT}&secret=dd${MT_SECRET}"
```

### План восстановления после сбоя

```bash
sudo systemctl status mtproxy --no-pager -l
sudo journalctl -u mtproxy -n 200 --no-pager
sudo ufw status verbose
sudo ss -lntp | grep ":${MT_PORT}\b"
```

Если бинарник повреждён или отсутствует:

```bash
cd "$MT_DIR/src"
sudo -u "$MT_USER" make clean
sudo -u "$MT_USER" make
sudo install -m 0755 objs/bin/mtproto-proxy "$MT_DIR/bin/mtproto-proxy"
sudo systemctl restart mtproxy
```

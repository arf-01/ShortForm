#!/bin/bash

set -e

echo "==> Pulling latest code..."
git pull

echo "==> Building backend..."
cd backend
./gradlew clean build -x test

echo "==> Updating frontend..."
sudo cp ../frontend/index.html /var/www/shortform/
sudo cp ../frontend/app.js /var/www/shortform/
sudo cp ../frontend/style.css /var/www/shortform/

echo "==> Restarting backend..."
sudo systemctl restart shortform

echo "==> Checking backend..."
sudo systemctl is-active --quiet shortform

echo "==> Deployment successful!"

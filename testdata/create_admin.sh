#!/bin/sh

curl -X PUT "http://localhost:5984/${USER}_motech-web-security"
curl -X POST "http://localhost:5984/${USER}_motech-web-security" -d @adminuser.json -H 'Content-Type: application/json'

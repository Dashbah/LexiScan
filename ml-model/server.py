import argparse
import logging
import os

from flask import Flask, jsonify, request, make_response
from typing import List, Optional
import jwt
import hashlib

from config import ENDPOINT_BASE



class Server:
    def __init__(self):
        self._storage = {}

    def put(self, key, value, user) -> bool:
        if self._storage.get(key, None) == None:
            self._storage[key] = [value, user]
            return True
        if self._storage[key][1] != user:
            print(key)
            print(self._storage[key][1])
            print(user)
            return False
        self._storage[key] = [value, user]
        return True
    
    def get(self, key, user) -> dict:
        if self._storage.get(key, None) == None:
            return {"status": 404, "value": ""}
        if self._storage[key][1] != user:
            return {"status": 403, "value": ""}
        return {"status": 200, "value": self._storage[key][0]}


def create_app() -> Flask:
    """
    Create flask application
    """
    app = Flask(__name__)

    server = Server()

    
    @app.route(f'{ENDPOINT_BASE}/put', methods=['POST'])
    def put():
        cookie = request.cookies.get("jwt")
        if cookie == None:
            return "wrong cookie", 401
        
        try:
            file = open(args.public, mode='rb')
            public_key = file.read()
            decoded = jwt.decode(cookie, public_key, algorithms=["RS256"])
        except Exception:
            return "wrong cookie", 400
        
        key = request.args.get('key')
        body = request.get_json(force=True)
        status = server.put(key, body["value"], decoded["username"])
        if status == False:
            return "key exist", 403
        return "Ok", 200

    @app.route(f'{ENDPOINT_BASE}/get', methods=['GET'])
    def get():
        key = request.args.get('key')
        cookie = request.cookies.get("jwt")
        if cookie == None:
            return "wrong cookie", 401
        try:
            file = open(args.public, mode='rb')
            public_key = file.read()
            decoded = jwt.decode(cookie, public_key, algorithms=["RS256"])
        except Exception:
            return "wrong cookie", 400
        
        answer = server.get(key, decoded["username"])

        if answer["status"] != 200:
            return "Invalid key", answer["status"]
        return {"value": answer["value"]}

    return app


app = create_app()

args = []

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("--port", type=int, default=8091)
    parser.add_argument("--public", type=str, default="/tmp/signature.pub")
    args = parser.parse_args()
    logging.basicConfig()
    app.run(host='0.0.0.0', port=args.port)
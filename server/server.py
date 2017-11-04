#!/usr/bin/env python

# Run with:
# FLASK_APP=server.py flask run

from flask import Flask
app = Flask(__name__)

@app.route('/')
def hello():
    return 'Hello world!'

print('still good')

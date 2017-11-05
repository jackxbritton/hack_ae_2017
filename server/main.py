#!/usr/bin/env python

import flask
import flask_cors
import sys
from getpass import getpass

import backend

app = flask.Flask(__name__)
flask_cors.CORS(app)

# Serving up JSON data.
@app.route('/get_data', methods=['GET','POST'])
def get_data():
    # TODO Check necessary parameters.
    return flask.jsonify(backend.get_data(db_str))

@app.route('/bat', methods=['GET','POST'])
def bat():
    # Check necessary parameters.
    if 'uuid' not in flask.request.args:
        return '' # TODO Actual error code.
    if 'ts' not in flask.request.args:
        return '' # TODO Actual error code.
    if 'bat' not in flask.request.args:
        return '' # TODO Actual error code.
    backend.insert_battery(
        db_str,
        flask.request.args['uuid'],
        flask.request.args['ts'],
        flask.request.args['bat']
    )
    return 'Good.' # TODO Actual response.

@app.route('/gps', methods=['GET','POST'])
def gps():
    # Check necessary parameters.
    if 'uuid' not in flask.request.args:
        return '' # TODO Actual error code.
    if 'ts' not in flask.request.args:
        return '' # TODO Actual error code.
    if 'lat' not in flask.request.args:
        return '' # TODO Actual error code.
    if 'lon' not in flask.request.args:
        return '' # TODO Actual error code.
    # GPS.
    backend.insert_gps(
        db_str,
        flask.request.args['uuid'],
        flask.request.args['ts'],
        flask.request.args['lat'],
        flask.request.args['lon']
    )
    return 'Good.' # TODO Actual response.

@app.route('/push', methods=['GET','POST'])
def push():
    # Check necessary parameters.
    if 'uuid' not in flask.request.args:
        return '' # TODO Actual error code.
    if 'ts' not in flask.request.args:
        return '' # TODO Actual error code.
    if 'bat' not in flask.request.args:
        return '' # TODO Actual error code.
    if 'lat' not in flask.request.args:
        return '' # TODO Actual error code.
    if 'lon' not in flask.request.args:
        return '' # TODO Actual error code.
    # GPS.
    backend.insert(
        db_str,
        flask.request.args['uuid'],
        flask.request.args['ts'],
        flask.request.args['bat'],
        flask.request.args['lat'],
        flask.request.args['lon']
    )
    return 'Good.' # TODO Actual response.

if __name__ == '__main__':
    # CLI args.
    if len(sys.argv) < 4:
        print('Usage: ' + sys.argv[0] + ' PSQL_USER PSQL_HOST DB')
        sys.exit(1)
    # Put together the database connection string. Note the getpass()!
    db_str = 'user=\'' + sys.argv[1] + '\' host=\'' + sys.argv[2] + '\' dbname=\'' + sys.argv[3] + '\' password=\'' + getpass() + '\''
    # Run flask.
    app.run(host='0.0.0.0', debug=False)

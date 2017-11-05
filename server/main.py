#!/usr/bin/env python

import flask
import sys
from getpass import getpass

import backend

app = flask.Flask(__name__)

# Serving up JSON data.
@app.route('/get_data', methods=['GET','POST'])
def get_data():
    # Check necessary parameters.
    if 'type' not in flask.request.args:
        return flask.jsonify({'error': 'No parameter *type*.'})
    # Return jsonified data.
    if flask.request.args['type'] == 'bat':
        return flask.jsonify(backend.get_data_battery(db_str))
    if flask.request.args['type'] == 'gps':
        return flask.jsonify(backend.get_data_gps(db_str))
    else:
        return flask.jsonify({'error': 'Bad value for parameter *type*.'})

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

if __name__ == '__main__':
    # CLI args.
    if len(sys.argv) < 4:
        print('Usage: ' + sys.argv[0] + ' PSQL_USER PSQL_HOST DB')
        sys.exit(1)
    # Put together the database connection string. Note the getpass()!
    db_str = 'user=\'' + sys.argv[1] + '\' host=\'' + sys.argv[2] + '\' dbname=\'' + sys.argv[3] + '\' password=\'' + getpass() + '\''
    # Run flask.
    app.run(host='0.0.0.0', debug=False)

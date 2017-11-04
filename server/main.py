#!/usr/bin/env python

import flask
import psycopg2
import sys
from getpass import getpass

# CLI args.
if len(sys.argv) < 4:
    print('Usage: ' + sys.argv[0] + ' PSQL_USER PSQL_HOST DB')
    sys.exit(1)

# Put together the database connection string. Note the getpass()!
db_str = 'user=\'' + sys.argv[1] + '\' host=\'' + sys.argv[2] + '\' dbname=\'' + sys.argv[3] + '\' password=\'' + getpass() + '\''

# Flask stuff.
app = flask.Flask(__name__)

@app.route('/', methods=['GET','POST'])
def test():
    # Connect to the database.
    try:
        db = psycopg2.connect(db_str)
    except Exception as e:
        print('Error: ' + str(e))
    # Serve JSON.
    if 'x' in flask.request.args:
        return str(flask.request.args['x'])
    else:
        return 'no parameter \'x\''

if __name__ == '__main__':
    app.run()

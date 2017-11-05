#!/usr/bin/env python

import psycopg2
from uuid import UUID

def uuid_to_id(cursor, uuid):
    # Get user id.
    cursor.execute('SELECT id FROM users WHERE users.uuid = %s', (uuid,))
    id = cursor.fetchone()
    # Insert new user if necessary.
    if id is None:
        cursor.execute('INSERT INTO users (uuid) VALUES (%s)', (uuid,))
        cursor.execute('SELECT id FROM users WHERE users.uuid = %s', (uuid,))
        id = cursor.fetchone()
    return id[0]

def insert_battery(db_str, uuid, timestamp, battery):
    # Parse inputs.
    try:
        uuid = str(UUID(uuid))
        timestamp = int(timestamp)
        battery = float(battery)
    except:
        print('Couldn\'t parse inputs in insert_battery()')
        return
    # Connect to the database.
    try:
        db = psycopg2.connect(db_str)
    except Exception as e:
        print('Error: ' + str(e))
        return
    cursor = db.cursor()
    # Get id.
    id = uuid_to_id(cursor, uuid)
    # Insert battery row.
    cursor.execute(
        'INSERT INTO battery (level, timestamp, user_id) VALUES (%s, %s, %s)',
        (battery, timestamp, id)
    )
    db.commit()
    # Clean up.
    cursor.close()
    db.close()

def insert_gps(db_str, uuid, timestamp, lat, lon):
    # Parse inputs.
    try:
        uuid = str(UUID(uuid))
        timestamp = int(timestamp)
        lat = float(lat)
        lon = float(lon)
    except:
        print('Couldn\'t parse inputs in insert_gps()')
        return
    # Connect to the database.
    try:
        db = psycopg2.connect(db_str)
    except Exception as e:
        print('Error: ' + str(e))
    cursor = db.cursor()
    # Get id.
    id = uuid_to_id(cursor, uuid)
    # Insert GPS row.
    cursor.execute(
        'INSERT INTO gps (lat, lon, timestamp, user_id) VALUES (%s, %s, %s, %s)',
        (lat, lon, timestamp, id)
    )
    db.commit()
    # Clean up.
    cursor.close()
    db.close()
